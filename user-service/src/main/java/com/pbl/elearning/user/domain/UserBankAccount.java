package com.pbl.elearning.user.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import com.pbl.elearning.user.domain.enums.BankAccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_bank_accounts")
public class UserBankAccount extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column( nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountHolderName;

    @Column( nullable = false)
    @Enumerated(EnumType.STRING)
    private BankAccountStatus status;

    private String pendingToken;

    private Instant tokenExpiredAt;

}