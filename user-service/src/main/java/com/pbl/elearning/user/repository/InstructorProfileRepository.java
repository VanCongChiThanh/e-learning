package com.pbl.elearning.user.repository;

import com.pbl.elearning.user.domain.InstructorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstructorProfileRepository extends JpaRepository<InstructorProfile, UUID> {
    Optional<InstructorProfile> findByUserId(UUID userId);
}