package com.pbl.elearning.user.service;

import com.pbl.elearning.user.payload.response.career.CareerPlanRequest;
import com.pbl.elearning.user.payload.response.career.CareerPlanResponse;

import java.util.UUID;

public interface CareerPlanService {
    CareerPlanResponse getCareerPlanByUserId(String userId);
    CareerPlanResponse saveCareerPlan(CareerPlanRequest careerPlanRequest, UUID userId);
}