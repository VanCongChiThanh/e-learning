package com.pbl.elearning.user.service.impl;

import com.pbl.elearning.common.constant.MessageConstant;
import com.pbl.elearning.common.exception.BadRequestException;
import com.pbl.elearning.common.exception.ForbiddenException;
import com.pbl.elearning.common.util.BeanUtilsHelper;
import com.pbl.elearning.user.domain.CareerPlan;
import com.pbl.elearning.user.payload.response.career.CareerPlanRequest;
import com.pbl.elearning.user.payload.response.career.CareerPlanResponse;
import com.pbl.elearning.user.repository.CareerPlanRepository;
import com.pbl.elearning.user.service.CareerPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CareerPlanServiceImpl  implements CareerPlanService {
    private  final CareerPlanRepository careerPlanRepository;

    @Override
    public CareerPlanResponse getCareerPlanByUserId(String userId) {
        Optional<CareerPlan> careerPlanOptional = careerPlanRepository.findByUserId(UUID.fromString(userId));
        if (careerPlanOptional.isPresent()) {
            CareerPlan careerPlan = careerPlanOptional.get();
            return CareerPlanResponse.toResponse(careerPlan, null);
        } else {
            return null;
        }
    }


    @Override
    public CareerPlanResponse saveCareerPlan(CareerPlanRequest req, UUID userId) {
        CareerPlan plan = careerPlanRepository.findByUserId(userId)
                .orElseGet(() -> CareerPlan.builder().userId(userId).build());

        if (req.getAnswers() == null || req.getAnswers().isEmpty() || req.getSections() == null || req.getSections().isEmpty()) {
            throw new BadRequestException(MessageConstant.BAD_REQUEST);
        }
        BeanUtilsHelper.copyNonNullProperties(req, plan);

        CareerPlan savedPlan = careerPlanRepository.save(plan);
        return CareerPlanResponse.toResponse(savedPlan, null);
    }

}