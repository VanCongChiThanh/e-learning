package com.pbl.elearning.user.service;

import com.pbl.elearning.user.payload.request.bank.BankAccountRequest;
import com.pbl.elearning.user.payload.response.bank.BankAccountResponse;

import java.util.UUID;

public interface UserBankAccountService {
    BankAccountResponse getUserBankAccount(UUID userId);
    BankAccountResponse createUserBankAccount(UUID userId, String email,BankAccountRequest request);
    BankAccountResponse updateUserBankAccount(UUID userId,String email, BankAccountRequest request);
    void confirmUserBankAccount(UUID userId, String token);
}