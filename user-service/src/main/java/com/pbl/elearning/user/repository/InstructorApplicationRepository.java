package com.pbl.elearning.user.repository;

import com.pbl.elearning.user.domain.InstructorApplication;
import com.pbl.elearning.user.domain.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstructorApplicationRepository extends JpaRepository<InstructorApplication, UUID> {
    Optional<InstructorApplication> findByIdAndUserId(UUID applicationId, UUID userId);

    @Query("SELECT a, ui FROM InstructorApplication a JOIN UserInfo ui ON a.userId = ui.userId " +
            "WHERE (:status IS NULL OR a.status = :status) " +
            "AND (:excludeStatus IS NULL OR a.status != :excludeStatus)")
    Page<Object[]> findAllWithUserInfo(
            Pageable pageable,
            @Param("status") ApplicationStatus status,
            @Param("excludeStatus") ApplicationStatus excludeStatus
    );

    Optional<InstructorApplication> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
}