package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.common.constant.MessageConstant;
import com.pbl.elearning.common.exception.NotFoundException;
import com.pbl.elearning.enrollment.repository.RevenueRepository;
import com.pbl.elearning.enrollment.services.RevenueService;
import com.pbl.elearning.enrollment.payload.request.InstructorRevenueRequest;
import com.pbl.elearning.user.payload.response.UserInfoResponse;
import com.pbl.elearning.enrollment.payload.response.InstructorRevenueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements RevenueService {
    private final RevenueRepository revenueRepository;

    @Override
    public InstructorRevenueResponse getInstructorRevenueReport(UUID instructorId, InstructorRevenueRequest request) {

        List<Object[]> rows = revenueRepository.getInstructorRevenueRaw(
                instructorId,
                request.getStartInstant(),
                request.getEndInstant()
        );

        if (rows == null || rows.isEmpty()) {
            throw new NotFoundException(MessageConstant.NOT_FOUND);
        }

        Object[] row = rows.get(0);

        UUID userId          = UUID.fromString(row[0].toString());
        String name          = (String) row[1];
        String avatar        = (String) row[2];
        Integer totalCourses = ((Number) row[3]).intValue();
        Double totalRevenue  = ((Number) row[4]).doubleValue();
        Double commission    = ((Number) row[5]).doubleValue();
        Double netEarnings   = ((Number) row[6]).doubleValue();

        return InstructorRevenueResponse.builder()
                .instructor(
                        UserInfoResponse.builder()
                                .userId(userId)
                                .name(name)
                                .avatar(avatar)
                                .build()
                )
                .totalCourses(totalCourses)
                .totalRevenue(totalRevenue)
                .commissionPercentage(commission)
                .netEarnings(netEarnings)
                .build();
    }

    @Override
    public List<InstructorRevenueResponse> getAllInstructorRevenue(InstructorRevenueRequest request) {
        List<Object[]> rows = revenueRepository.getAllInstructorRevenueRaw(request.getStartInstant(),request.getEndInstant());
        List<InstructorRevenueResponse> results = new ArrayList<>();

        for (Object[] row : rows) {
            UUID userId          = UUID.fromString(row[0].toString());
            String name          = (String) row[1];
            String avatar        = (String) row[2];
            Integer totalCourses = ((Number) row[3]).intValue();
            Double totalRevenue  = ((Number) row[4]).doubleValue();
            Double commission    = ((Number) row[5]).doubleValue();
            Double netEarnings   = ((Number) row[6]).doubleValue();

            results.add(
                    InstructorRevenueResponse.builder()
                            .instructor(
                                    UserInfoResponse.builder()
                                            .userId(userId)
                                            .name(name)
                                            .avatar(avatar)
                                            .build()
                            )
                            .totalCourses(totalCourses)
                            .totalRevenue(totalRevenue)
                            .commissionPercentage(commission)
                            .netEarnings(netEarnings)
                            .build()
            );
        }

        return results;
    }
}