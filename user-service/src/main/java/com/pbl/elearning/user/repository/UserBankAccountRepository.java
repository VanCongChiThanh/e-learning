package com.pbl.elearning.user.repository;

import com.pbl.elearning.user.domain.UserBankAccount;
import com.pbl.elearning.user.domain.enums.BankAccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserBankAccountRepository extends JpaRepository<UserBankAccount, UUID> {
    Optional<UserBankAccount> findFirstByUserIdAndStatus(UUID userId, BankAccountStatus status);

    void deleteByUserIdAndIdNotAndStatus(UUID userId, UUID id, BankAccountStatus bankAccountStatus);

    void deleteByUserIdAndStatus(UUID userId, BankAccountStatus bankAccountStatus);
}