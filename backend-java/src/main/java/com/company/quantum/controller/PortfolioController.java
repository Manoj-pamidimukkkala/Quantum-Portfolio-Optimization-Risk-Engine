package com.company.quantum.controller;

import com.company.quantum.dto.OptimizationRequestDTO;
import com.company.quantum.model.Asset;
import com.company.quantum.model.QuantumJob;
import com.company.quantum.service.QuantumOptimizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolio")
@CrossOrigin(origins = "*")
public class PortfolioController {

    private final QuantumOptimizationService optimizationService;

    public PortfolioController(QuantumOptimizationService optimizationService) {
        this.optimizationService = optimizationService;
    }

    @GetMapping("/assets")
    public ResponseEntity<List<Asset>> getAssets() {
        return ResponseEntity.ok(optimizationService.getAllAssets());
    }

    @PostMapping("/optimize")
    public ResponseEntity<QuantumJob> runQuantumOptimization(@RequestBody OptimizationRequestDTO requestDTO) {
        return ResponseEntity.ok(optimizationService.executeOptimization(requestDTO));
    }
}
