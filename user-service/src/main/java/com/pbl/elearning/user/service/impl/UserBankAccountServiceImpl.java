package com.pbl.elearning.user.service.impl;

import com.pbl.elearning.common.constant.MessageConstant;
import com.pbl.elearning.common.exception.NotFoundException;
import com.pbl.elearning.email.service.EmailService;
import com.pbl.elearning.user.domain.UserBankAccount;
import com.pbl.elearning.user.domain.enums.BankAccountStatus;
import com.pbl.elearning.user.payload.request.bank.BankAccountRequest;
import com.pbl.elearning.user.payload.response.bank.BankAccountResponse;
import com.pbl.elearning.user.repository.UserBankAccountRepository;
import com.pbl.elearning.user.service.UserBankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserBankAccountServiceImpl implements UserBankAccountService {
    private final UserBankAccountRepository userBankAccountRepository;
    private final EmailService emailService;
    @Override
    public BankAccountResponse getUserBankAccount(UUID userId) {
        Optional<UserBankAccount> active =
                userBankAccountRepository.findFirstByUserIdAndStatus(userId, BankAccountStatus.VERIFIED);

        Optional<UserBankAccount> pending =
                userBankAccountRepository.findFirstByUserIdAndStatus(userId, BankAccountStatus.PENDING);

        return BankAccountResponse.builder()
                .activeBank(active.map(BankAccountResponse.BankItem::from).orElse(null))
                .pendingBank(pending.map(BankAccountResponse.BankItem::from).orElse(null))
                .build();
    }

    @Override
    public BankAccountResponse createUserBankAccount(UUID userId, String email, BankAccountRequest request) {

        // Always clear all old pending (resend behavior)
        userBankAccountRepository.deleteByUserIdAndStatus(userId, BankAccountStatus.PENDING);

        String token=generateOtp();

        UserBankAccount newAccount = new UserBankAccount();
        newAccount.setUserId(userId);
        newAccount.setBankName(request.getBankName());
        newAccount.setAccountNumber(request.getAccountNumber());
        newAccount.setAccountHolderName(request.getAccountHolderName());
        newAccount.setStatus(BankAccountStatus.PENDING);
        newAccount.setPendingToken(token);
        newAccount.setTokenExpiredAt(Instant.now().plusSeconds(15 * 60));

        UserBankAccount saved = userBankAccountRepository.save(newAccount);

        emailService.sendMailConfirmBankAccount(
                saved.getBankName(),
                saved.getAccountNumber(),
                saved.getAccountHolderName(),
                token,
                email,
                "vi"
        );

        return BankAccountResponse.builder()
                .pendingBank(BankAccountResponse.BankItem.from(saved))
                .build();
    }

    @Override
    public BankAccountResponse updateUserBankAccount(UUID userId, String email, BankAccountRequest request) {

        // 1. Lấy pending hiện tại (nếu có)
        UserBankAccount pending = userBankAccountRepository
                .findFirstByUserIdAndStatus(userId, BankAccountStatus.PENDING)
                .orElse(null);

        // 2. Nếu chưa có pending → kiểm tra phải có verified trước
        if (pending == null) {
            userBankAccountRepository.findFirstByUserIdAndStatus(userId, BankAccountStatus.VERIFIED)
                    .orElseThrow(() -> new NotFoundException(MessageConstant.NOT_FOUND));

            pending = new UserBankAccount();
            pending.setUserId(userId);
            pending.setStatus(BankAccountStatus.PENDING);
        }

        // 3. Cập nhật dữ liệu vào pending
        String token=generateOtp();;
        pending.setBankName(request.getBankName());
        pending.setAccountNumber(request.getAccountNumber());
        pending.setAccountHolderName(request.getAccountHolderName());
        pending.setPendingToken(token);
        pending.setTokenExpiredAt(Instant.now().plusSeconds(15 * 60));

        userBankAccountRepository.save(pending);
        // gửi mail xác nhận
        emailService.sendMailConfirmBankAccount(
                pending.getBankName(),
                pending.getAccountNumber(),
                pending.getAccountHolderName(),
                token,
                email,
                "vi"
        );
        // 5. Build response
        return BankAccountResponse.builder()
                .pendingBank(BankAccountResponse.BankItem.from(pending))
                .build();
    }

    @Override
    public void confirmUserBankAccount(UUID userId, String token) {
        UserBankAccount pending = userBankAccountRepository
                .findFirstByUserIdAndStatus(userId, BankAccountStatus.PENDING)
                .orElseThrow(() -> new NotFoundException(MessageConstant.NOT_FOUND));

        if (!pending.getPendingToken().equals(token) || pending.getTokenExpiredAt().isBefore(Instant.now())) {
            throw new NotFoundException(MessageConstant.BAD_REQUEST);
        }

        // Xác thực thành công
        // 1. Chuyển trạng thái pending thành verified
        pending.setStatus(BankAccountStatus.VERIFIED);
        pending.setPendingToken(null);
        pending.setTokenExpiredAt(null);
        userBankAccountRepository.save(pending);

        //2: xóa tất cả verified khác (giữ lại 1 verified duy nhất)
        userBankAccountRepository.deleteByUserIdAndIdNotAndStatus(
                userId,
                pending.getId(),
                BankAccountStatus.VERIFIED
        );

        //3: xóa tất cả pending cũ nếu còn sót (đề phòng)
        userBankAccountRepository.deleteByUserIdAndStatus(
                userId,
                BankAccountStatus.PENDING
        );
    }
    private String generateOtp() {
        return String.format("%06d", (int) (Math.random() * 999999));
    }

}