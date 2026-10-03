import time
import numpy as np
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Dict, Any

from qiskit_optimization import QuadraticProgram
from qiskit_optimization.algorithms import MinimumEigenOptimizer
from qiskit_algorithms import QAOA
from qiskit_algorithms.optimizers import COBYLA
from qiskit_aer import AerSimulator
from qiskit.primitives import Sampler

app = FastAPI(
    title="Qiskit QAOA Portfolio Engine",
    description="Quantum Optimization Microservice for Portfolio Markowitz QUBO Mapping",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class OptimizationRequest(BaseModel):
    job_id: str
    tickers: List[str]
    returns: List[float]
    covariance_matrix: List[List[float]]
    risk_factor: float  # q parameter balancing risk vs return

class OptimizationResponse(BaseModel):
    job_id: str
    optimal_bitstring: str
    selected_tickers: List[str]
    optimal_value: float
    execution_time_ms: float
    circuit_depth: int

@app.post("/api/v1/quantum/solve", response_model=OptimizationResponse)
async def solve_portfolio_qaoa(req: OptimizationRequest):
    start_time = time.time()
    n = len(req.tickers)
    
    if n > 10:
        raise HTTPException(status_code=400, detail="Qubit limit exceeded for local simulation (Max 10 assets).")
        
    try:
        # 1. Formulate Quadratic Unconstrained Binary Optimization (QUBO)
        qp = QuadraticProgram(name="PortfolioOptimization")
        for ticker in req.tickers:
            qp.binary_var(name=ticker)
            
        # Objective: minimize (q * x^T * COV * x - mu^T * x)
        mu = np.array(req.returns)
        sigma = np.array(req.covariance_matrix)
        
        linear_terms = {req.tickers[i]: -mu[i] for i in range(n)}
        quadratic_terms = {
            (req.tickers[i], req.tickers[j]): req.risk_factor * sigma[i][j]
            for i in range(n) for j in range(n)
        }
        
        qp.minimize(linear=linear_terms, quadratic=quadratic_terms)
        
        # 2. Setup QAOA Quantum Circuit Solver
        backend = AerSimulator()
        sampler = Sampler()
        optimizer = COBYLA(maxiter=50)
        
        qaoa = QAOA(sampler=sampler, optimizer=optimizer, reps=1)
        optimizer_solver = MinimumEigenOptimizer(qaoa)
        
        # 3. Solve QUBO via QAOA
        result = optimizer_solver.solve(qp)
        
        exec_time = (time.time() - start_time) * 1000
        
        # Extract selections
        selected = [req.tickers[i] for i in range(n) if result.x[i] == 1]
        bitstring = "".join(str(int(x)) for x in result.x)
        
        return OptimizationResponse(
            job_id=req.job_id,
            optimal_bitstring=bitstring,
            selected_tickers=selected,
            optimal_value=float(result.fval),
            execution_time_ms=round(exec_time, 2),
            circuit_depth=12 # Approximate depth for reps=1 QAOA ansatz
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Quantum Solver Exception: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
