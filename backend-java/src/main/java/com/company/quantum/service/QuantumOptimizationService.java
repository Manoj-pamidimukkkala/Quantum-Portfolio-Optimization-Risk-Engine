package com.company.quantum.service;

import com.company.quantum.dto.OptimizationRequestDTO;
import com.company.quantum.model.Asset;
import com.company.quantum.model.QuantumJob;
import com.company.quantum.repository.AssetRepository;
import com.company.quantum.repository.QuantumJobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class QuantumOptimizationService {

    private final AssetRepository assetRepository;
    private final QuantumJobRepository jobRepository;
    private final RestTemplate restTemplate;

    @Value("${python.quantum.service.url}")
    private String pythonServiceUrl;

    public QuantumOptimizationService(AssetRepository assetRepository, QuantumJobRepository jobRepository) {
        this.assetRepository = assetRepository;
        this.jobRepository = jobRepository;
        this.restTemplate = new RestTemplate();
    }

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public QuantumJob executeOptimization(OptimizationRequestDTO requestDTO) {
        String jobId = "QJOB-" + UUID.randomUUID().toString().substring(0, 8);
        List<Asset> assets = assetRepository.findAll();

        QuantumJob job = new QuantumJob();
        job.setId(jobId);
        job.setStatus(QuantumJob.Status.EXECUTING_QAOA);
        job.setRiskFactor(requestDTO.getRiskFactor());
        job.setNumQubits(assets.size());
        jobRepository.save(job);

        // Prepare matrices for Python Qiskit
        List<String> tickers = assets.stream().map(Asset::getTicker).toList();
        List<Double> returns = assets.stream().map(Asset::getExpectedReturn).toList();
        
        // Mock Covariance Matrix generation for Qiskit optimization
        List<List<Double>> covMatrix = generateCovarianceMatrix(assets);

        Map<String, Object> pythonPayload = new HashMap<>();
        pythonPayload.put("job_id", jobId);
        pythonPayload.put("tickers", tickers);
        pythonPayload.put("returns", returns);
        pythonPayload.put("covariance_matrix", covMatrix);
        pythonPayload.put("risk_factor", requestDTO.getRiskFactor());

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(pythonServiceUrl, pythonPayload, Map.class);
            Map body = response.getBody();

            job.setStatus(QuantumJob.Status.COMPLETED);
            job.setBitstringResult((String) body.get("optimal_bitstring"));
            job.setSelectedAssets(body.get("selected_tickers").toString());
            job.setExecutionTimeMs(((Number) body.get("execution_time_ms")).doubleValue());
            job.setOptimalValue(((Number) body.get("optimal_value")).doubleValue());

        } catch (Exception e) {
            job.setStatus(QuantumJob.Status.FAILED);
        }

        return jobRepository.save(job);
    }

    private List<List<Double>> generateCovarianceMatrix(List<Asset> assets) {
        int n = assets.size();
        List<List<Double>> matrix = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    row.add(Math.pow(assets.get(i).getVolatility(), 2));
                } else {
                    row.add(0.005); // Simplified covariance
                }
            }
            matrix.add(row);
        }
        return matrix;
    }
}
