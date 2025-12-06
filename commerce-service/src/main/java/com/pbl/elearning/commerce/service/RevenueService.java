package com.pbl.elearning.commerce.service;

import com.pbl.elearning.commerce.payload.response.CourseTransactionResponse;
import com.pbl.elearning.commerce.payload.response.InstructorCourseRevenueResponse;
import com.pbl.elearning.common.constant.MessageConstant;
import com.pbl.elearning.common.exception.ForbiddenException;
import com.pbl.elearning.common.exception.NotFoundException;
import com.pbl.elearning.commerce.repository.RevenueRepository;
import com.pbl.elearning.commerce.payload.request.InstructorRevenueRequest;
import com.pbl.elearning.course.payload.response.CourseResponse;
import com.pbl.elearning.course.service.CourseService;
import com.pbl.elearning.user.payload.response.UserInfoResponse;
import com.pbl.elearning.commerce.payload.response.InstructorRevenueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RevenueService {
    private final RevenueRepository revenueRepository;
    private final CourseService courseService;

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
    public List<InstructorCourseRevenueResponse> getCourseRevenueByInstructor(
            UUID instructorId,
            InstructorRevenueRequest request
    ) {

        List<Object[]> rows = revenueRepository.getInstructorCourseRevenue(
                instructorId,
                request.getStartInstant(),
                request.getEndInstant()
        );

        List<InstructorCourseRevenueResponse> results = new ArrayList<>();

        for (Object[] row : rows) {

            UUID courseId = UUID.fromString((String) row[0]);
            String title          = (String) row[1];
            BigDecimal price      = (BigDecimal) row[2];
            Long totalSales       = ((Number) row[3]).longValue();
            BigDecimal gross      = (BigDecimal) row[4];
            BigDecimal net        = (BigDecimal) row[5];

            results.add(
                    InstructorCourseRevenueResponse.builder()
                            .courseId(courseId)
                            .title(title)
                            .price(price)
                            .totalSales(totalSales)
                            .grossRevenue(gross)
                            .netEarnings(net)
                            .build()
            );
        }
        return results;
    }
    public List<CourseTransactionResponse> getCourseTransactions(UUID courseId,UUID instructorId,Instant startDate,Instant endDate) {
        CourseResponse course = courseService.getCourseById(courseId);
        if(!course.getInstructorId().equals(instructorId)){
            throw new ForbiddenException(MessageConstant.FORBIDDEN_ERROR);
        }
        List<Object[]> rows = revenueRepository.getCourseTransactions(courseId, startDate, endDate);

        List<CourseTransactionResponse> results = new ArrayList<>();

        for (Object[] row : rows) {

            UUID orderId         = UUID.fromString(row[0].toString());
            String email         = (String) row[1];
            BigDecimal gross     = (BigDecimal) row[2];
            BigDecimal net       = (BigDecimal) row[3];
            Instant createdAt    = ((Timestamp) row[4]).toInstant();

            results.add(
                    CourseTransactionResponse.builder()
                            .orderId(orderId)
                            .studentEmail(email)
                            .grossAmount(gross)
                            .netAmount(net)
                            .createdAt(createdAt)
                            .build()
            );
        }
        return results;
    }
}