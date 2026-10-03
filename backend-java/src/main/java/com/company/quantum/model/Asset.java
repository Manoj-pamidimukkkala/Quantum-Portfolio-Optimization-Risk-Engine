package com.company.quantum.model;

import jakarta.persistence.*;

@Entity
@Table(name = "assets")
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticker;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "expected_return", nullable = false)
    private Double expectedReturn;

    @Column(nullable = false)
    private Double volatility;

    @Column(nullable = false)
    private String sector;

    // Getters and Setters
    public Long getId() { return id; }
    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }
    public String getCompanyName() { return companyName; }
    public Double getExpectedReturn() { return expectedReturn; }
    public void setExpectedReturn(Double expectedReturn) { this.expectedReturn = expectedReturn; }
    public Double getVolatility() { return volatility; }
    public void setVolatility(Double volatility) { this.volatility = volatility; }
    public String getSector() { return sector; }
}
