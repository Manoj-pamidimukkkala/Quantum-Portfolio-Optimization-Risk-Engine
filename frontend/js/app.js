const JAVA_API = 'http://localhost:8080/api/v1/portfolio';

document.addEventListener('DOMContentLoaded', () => {
    lucide.createIcons();
    fetchAssets();
});

function updateRiskVal(val) {
    document.getElementById('riskVal').textContent = val;
}

async function fetchAssets() {
    try {
        const res = await fetch(`${JAVA_API}/assets`);
        const assets = await res.json();
        
        document.getElementById('assetBody').innerHTML = assets.map(a => `
            <tr>
                <td><strong>${a.ticker}</strong></td>
                <td>${a.companyName}</td>
                <td>${(a.expectedReturn * 100).toFixed(1)}%</td>
                <td>${(a.volatility * 100).toFixed(1)}%</td>
            </tr>
        `).join('');
    } catch (e) {
        document.getElementById('assetBody').innerHTML = `<tr><td colspan="4">Java Backend offline (Port 8080)</td></tr>`;
    }
}

async function runOptimization() {
    const riskFactor = parseFloat(document.getElementById('riskRange').value);
    const logEl = document.getElementById('outputLog');
    
    logEl.textContent = "Dispatching QUBO formulation to Java Orchestrator -> Qiskit QAOA Simulator...";

    try {
        const res = await fetch(`${JAVA_API}/optimize`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ riskFactor: riskFactor, tickers: [] })
        });
        const job = await res.json();
        logEl.textContent = JSON.stringify(job, null, 2);
    } catch (e) {
        logEl.textContent = "Error executing job. Ensure both Java (8080) and Python (8000) are running.";
    }
}
