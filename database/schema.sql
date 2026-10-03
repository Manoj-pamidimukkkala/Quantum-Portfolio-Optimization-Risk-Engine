CREATE DATABASE IF NOT EXISTS quantum_portfolio_db
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE quantum_portfolio_db;

-- 1. Assets Table
CREATE TABLE IF NOT EXISTS assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticker VARCHAR(10) UNIQUE NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    expected_return DOUBLE NOT NULL,
    volatility DOUBLE NOT NULL,
    sector VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Quantum Optimization Jobs Table
CREATE TABLE IF NOT EXISTS quantum_jobs (
    id VARCHAR(64) PRIMARY KEY,
    status ENUM('QUEUED', 'EXECUTING_QAOA', 'COMPLETED', 'FAILED') NOT NULL,
    risk_factor DOUBLE NOT NULL,
    num_qubits INT NOT NULL,
    selected_assets JSON,
    bitstring_result VARCHAR(64),
    execution_time_ms DOUBLE,
    optimal_value DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Seed Sample Financial Assets
INSERT INTO assets (ticker, company_name, expected_return, volatility, sector) VALUES
('AAPL', 'Apple Inc.', 0.142, 0.22, 'Technology'),
('MSFT', 'Microsoft Corp.', 0.128, 0.19, 'Technology'),
('JPM', 'JPMorgan Chase & Co.', 0.095, 0.16, 'Finance'),
('JNJ', 'Johnson & Johnson', 0.065, 0.11, 'Healthcare'),
('XOM', 'Exxon Mobil Corp.', 0.110, 0.25, 'Energy');
