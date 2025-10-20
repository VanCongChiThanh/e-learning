package com.pbl.elearning.enrollment.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pbl.elearning.enrollment.models.CodeTestCase;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeTestCaseResponse {
    private UUID id;
    private String inputData;
    private String expectedOutput;
    private Integer points;
    private Boolean isHidden;
    private Integer sortOrder;
    public static CodeTestCaseResponse fromEntity(CodeTestCase testCase) {
        return CodeTestCaseResponse.builder()
                .id(testCase.getCodeTestCaseId())
                .inputData(testCase.getInputData())
                .expectedOutput(testCase.getExpectedOutput())
                .points(testCase.getPoints())
                .isHidden(testCase.getIsHidden())
                .sortOrder(testCase.getSortOrder())
                .build();
    }
}
