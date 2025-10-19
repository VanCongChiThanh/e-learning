package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.CodeTestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CodeTestCaseRepository extends JpaRepository<CodeTestCase, UUID> {
}
