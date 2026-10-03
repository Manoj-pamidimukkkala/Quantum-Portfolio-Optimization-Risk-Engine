package com.company.quantum.dto;

import java.util.List;

public class OptimizationRequestDTO {
    private Double riskFactor;
    private List<String> tickers;

    public Double getRiskFactor() { return riskFactor; }
    public void setRiskFactor(Double riskFactor) { this.riskFactor = riskFactor; }
    public List<String> getTickers() { return tickers; }
    public void setTickers(List<String> tickers) { this.tickers = tickers; }
}
