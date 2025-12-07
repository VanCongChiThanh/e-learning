package com.pbl.elearning.web.endpoint.user;

import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.pbl.elearning.user.payload.request.bank.BankAccountRequest;
import com.pbl.elearning.user.service.UserBankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1")
@RestController
@RequiredArgsConstructor
public class UserBankAccountController {
    private final UserBankAccountService userBankAccountService;
    @GetMapping("/user/bank-accounts")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ResponseDataAPI> getMyBankAccount(
            @CurrentUser UserPrincipal userPrincipal
            ) {
        return ResponseEntity.ok(
                ResponseDataAPI.successWithoutMeta(
                        userBankAccountService.getUserBankAccount(userPrincipal.getId())
                )
        );
    }
    @PostMapping("/user/bank-accounts")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ResponseDataAPI> createMyBankAccount(
            @RequestBody BankAccountRequest request,
            @CurrentUser UserPrincipal userPrincipal
            ) {
        return ResponseEntity.ok(
                ResponseDataAPI.successWithoutMeta(
                        userBankAccountService.createUserBankAccount(userPrincipal.getId(), userPrincipal.getEmail(),request
                ))
        );
    }
    @PatchMapping("/user/bank-accounts")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ResponseDataAPI> updateMyBankAccount(
            @RequestBody BankAccountRequest request,
            @CurrentUser UserPrincipal userPrincipal
            ) {
        return ResponseEntity.ok(
                ResponseDataAPI.successWithoutMeta(
                        userBankAccountService.updateUserBankAccount(userPrincipal.getId(), userPrincipal.getEmail(), request
                ))
        );
    }
    @PatchMapping("/user/bank-accounts/confirm")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ResponseDataAPI> confirmMyBankAccount(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam String token
            ) {
        userBankAccountService.confirmUserBankAccount(userPrincipal.getId(), token);
        return ResponseEntity.ok(
                ResponseDataAPI.successWithoutMetaAndData()
        );
    }
    @GetMapping("/user/{userId}/bank-accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDataAPI> getUserBankAccountByAdmin(
            @PathVariable("userId") UUID userId
    ) {
        return ResponseEntity.ok(
                ResponseDataAPI.successWithoutMeta(
                        userBankAccountService.getUserBankAccountByAdmin(userId)
                )
        );
    }

}