package com.pbl.elearning.user.payload.request.bank;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BankAccountRequest {
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
}