package com.pbl.elearning.web.endpoint.user;

import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.pbl.elearning.user.payload.request.bank.BankAccountRequest;
import com.pbl.elearning.user.service.UserBankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/user/bank-accounts")
@RestController
@RequiredArgsConstructor
public class UserBankAccountController {
    private final UserBankAccountService userBankAccountService;
    @GetMapping
    public ResponseEntity<ResponseDataAPI> getMyBankAccount(
            @CurrentUser UserPrincipal userPrincipal
            ) {
        return ResponseEntity.ok(
                ResponseDataAPI.successWithoutMeta(
                        userBankAccountService.getUserBankAccount(userPrincipal.getId())
                )
        );
    }
    @PostMapping
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
    @PatchMapping
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
    @PatchMapping("/confirm")
    public ResponseEntity<ResponseDataAPI> confirmMyBankAccount(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam String token
            ) {
        userBankAccountService.confirmUserBankAccount(userPrincipal.getId(), token);
        return ResponseEntity.ok(
                ResponseDataAPI.successWithoutMetaAndData()
        );
    }

}