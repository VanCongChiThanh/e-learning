package com.pbl.elearning.web.endpoint.user;

import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.pbl.elearning.user.payload.response.career.CareerPlanRequest;
import com.pbl.elearning.user.service.CareerPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/career-plans")
@RestController
@RequiredArgsConstructor
public class CareerPlanController {
    private final CareerPlanService careerPlanService;

    @GetMapping("/me")
    public ResponseEntity<ResponseDataAPI> getMyCareerPlan(
            @CurrentUser UserPrincipal userPrincipal
            ) {
        return ResponseEntity.ok(
                ResponseDataAPI.successWithoutMeta(
                        careerPlanService.getCareerPlanByUserId(userPrincipal.getId().toString())
                )
        );
    }
    @PostMapping("/me")
    public ResponseEntity<ResponseDataAPI> saveMyCareerPlan(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody CareerPlanRequest careerPlanRequest
            ) {
        return ResponseEntity.ok(
                ResponseDataAPI.successWithoutMeta(
                        careerPlanService.saveCareerPlan(careerPlanRequest, userPrincipal.getId()
                )));
    }

}