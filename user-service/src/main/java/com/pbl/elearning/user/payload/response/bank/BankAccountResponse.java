package com.pbl.elearning.user.payload.response.bank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pbl.elearning.user.domain.UserBankAccount;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankAccountResponse {

    private BankItem activeBank;   // đang sử dụng
    private BankItem pendingBank;  // chờ xác thực, có thể null

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class BankItem {
        private UUID bankAccountId;
        private String bankName;
        private String accountNumberMasked;
        private String accountHolderName;
        private Instant expiredAt; // chỉ dùng cho pending

        public static BankItem from(UserBankAccount e) {
            return BankItem.builder()
                    .bankAccountId(e.getId())
                    .bankName(e.getBankName())
                    .accountNumberMasked(mask(e.getAccountNumber()))
                    .accountHolderName(e.getAccountHolderName())
                    .expiredAt(e.getTokenExpiredAt())
                    .build();
        }
        public static BankItem fromNotMask(UserBankAccount e) {
            return BankItem.builder()
                    .bankAccountId(e.getId())
                    .bankName(e.getBankName())
                    .accountNumberMasked(e.getAccountNumber()) // FULL
                    .accountHolderName(e.getAccountHolderName())
                    .expiredAt(e.getTokenExpiredAt())
                    .build();
        }
        private static String mask(String acc) {
            if (acc == null || acc.length() < 4) return "****";
            return "****" + acc.substring(acc.length() - 4);
        }
    }
}