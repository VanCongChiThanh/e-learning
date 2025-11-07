package com.pbl.elearning.enrollment.models;

import java.util.UUID;

public class EnrollmentCompletedEvent {
    private final UUID enrollmentId;

    public EnrollmentCompletedEvent(UUID enrollmentId) {
        this.enrollmentId = enrollmentId;
    }
    public UUID getEnrollmentId() {
        return enrollmentId;
    }
}
