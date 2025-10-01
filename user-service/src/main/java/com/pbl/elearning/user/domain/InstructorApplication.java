package com.pbl.elearning.user.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.pbl.elearning.common.domain.AbstractEntity;
import com.pbl.elearning.user.domain.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "instructor_applications")
public class InstructorApplication extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(length = 255)
    private String cvUrl;

    @Column(length = 255)
    private String portfolioLink;

    @Column(columnDefinition = "text")
    private String motivation;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private JsonNode extraInfo;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(name = "reviewed_by", nullable = true)
    private UUID reviewedBy; // User ID of the reviewer
}