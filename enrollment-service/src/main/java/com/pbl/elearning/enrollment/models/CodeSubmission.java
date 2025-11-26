package com.pbl.elearning.enrollment.models;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "code_submissions")
@Data
public class CodeSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "code_submission_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID codeSubmissionId;

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UUID userId;

    @Column(name = "exercise_id", columnDefinition = "uuid", nullable = false)
    private UUID exerciseId;

    @Column(name = "source_code", columnDefinition = "text", nullable = false)
    private String sourceCode;

    @Column(name = "language_id", nullable = false)
    private Integer languageId;

    @Column(name = "status", length = 255, nullable = false)
    private String status;

    @Column(name = "execution_time")
    private Double executionTime;

    @Column(name = "memory_usage")
    private Integer memoryUsage;

    @Column(name = "stdout", columnDefinition = "text")
    private String stdout;

    @Column(name = "stderr", columnDefinition = "text")
    private String stderr;

    @Column(name = "gemini_feedback", columnDefinition = "jsonb")
    private String geminiFeedback;

    @Column(name = "points_achieved")
    private Integer pointsAchieved = 0;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();
}