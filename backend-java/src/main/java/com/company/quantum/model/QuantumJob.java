package com.company.quantum.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quantum_jobs")
public class QuantumJob {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Double riskFactor;
    private Integer numQubits;

    @Column(columnDefinition = "JSON")
    private String selectedAssets;

    private String bitstringResult;
    private Double executionTimeMs;
    private Double optimalValue;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status { QUEUED, EXECUTING_QAOA, COMPLETED, FAILED }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Double getRiskFactor() { return riskFactor; }
    public void setRiskFactor(Double riskFactor) { this.riskFactor = riskFactor; }
    public Integer getNumQubits() { return numQubits; }
    public void setNumQubits(Integer numQubits) { this.numQubits = numQubits; }
    public String getSelectedAssets() { return selectedAssets; }
    public void setSelectedAssets(String selectedAssets) { this.selectedAssets = selectedAssets; }
    public String getBitstringResult() { return bitstringResult; }
    public void setBitstringResult(String bitstringResult) { this.bitstringResult = bitstringResult; }
    public Double getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(Double executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    public Double getOptimalValue() { return optimalValue; }
    public void setOptimalValue(Double optimalValue) { this.optimalValue = optimalValue; }
}
