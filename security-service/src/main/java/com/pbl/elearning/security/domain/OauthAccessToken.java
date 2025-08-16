package com.pbl.elearning.security.domain;


import com.pbl.elearning.common.domain.AbstractEntity;
import com.pbl.elearning.security.domain.enums.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "oauth_access_tokens")
public class OauthAccessToken extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "refresh_token")
    private UUID refreshToken;

    @Column(name = "revoked_at")
    private Timestamp revokedAt;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;
}