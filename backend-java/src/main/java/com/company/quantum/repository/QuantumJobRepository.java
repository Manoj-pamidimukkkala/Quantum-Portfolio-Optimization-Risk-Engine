package com.company.quantum.repository;

import com.company.quantum.model.QuantumJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuantumJobRepository extends JpaRepository<QuantumJob, String> {}
