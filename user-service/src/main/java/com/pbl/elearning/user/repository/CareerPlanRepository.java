package com.pbl.elearning.user.repository;

import com.pbl.elearning.user.domain.CareerPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CareerPlanRepository extends JpaRepository<CareerPlan, UUID> {
    Optional<CareerPlan> findByUserId(UUID uuid);
}