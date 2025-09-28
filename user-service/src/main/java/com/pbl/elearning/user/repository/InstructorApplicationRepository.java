package com.pbl.elearning.user.repository;

import com.pbl.elearning.user.domain.InstructorApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstructorApplicationRepository extends JpaRepository<InstructorApplication, UUID> {
    Optional<InstructorApplication> findByIdAndUserId(UUID applicationId, UUID userId);

    @Query("SELECT a, ui FROM InstructorApplication a JOIN UserInfo ui ON a.userId = ui.userId")
    Page<Object[]> findAllWithUserInfo(Pageable pageable);
}