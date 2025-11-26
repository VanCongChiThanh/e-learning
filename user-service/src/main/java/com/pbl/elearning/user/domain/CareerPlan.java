package com.pbl.elearning.user.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import com.pbl.elearning.user.payload.dto.CareerPlanSectionDTO;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "career_plans",
        uniqueConstraints = @UniqueConstraint(columnNames = "user_id")
)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class CareerPlan extends AbstractEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    private String role;

    private String goal;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<CareerPlanSectionDTO> sections;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> answers;

}