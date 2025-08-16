package com.pbl.elearning.security.domain;

import com.pbl.elearning.common.constant.CommonConstant;
import com.pbl.elearning.common.domain.AbstractEntity;
import com.pbl.elearning.common.domain.enums.Role;
import com.pbl.elearning.security.domain.enums.ActiveStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends AbstractEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Email @Column private String emailRestore;

    @Column(nullable = false)
    private Boolean isEnabled = false;

    @Column(name = "encrypted_password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Set<UserProvider> providers = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private ActiveStatus activeStatus;

    @Column(columnDefinition = "text")
    private String reasonRefuse;

    private UUID confirmationToken;

    private Timestamp confirmedAt;

    private UUID resetPasswordToken;

    private Timestamp resetPasswordSentAt;

    private Timestamp requestDeleteAt;

    private String requestDeleteCode;

    public String getSubRole() {
        return role.toString().replace(CommonConstant.ROLE_PREFIX, "");
    }
}