package com.pbl.elearning.enrollment.services;

import com.pbl.elearning.enrollment.payload.request.InstructorRevenueRequest;
import com.pbl.elearning.enrollment.payload.response.InstructorRevenueResponse;

import java.util.List;
import java.util.UUID;

public interface RevenueService {
    List<InstructorRevenueResponse> getAllInstructorRevenue(InstructorRevenueRequest request);
    InstructorRevenueResponse getInstructorRevenueReport(UUID instructorId, InstructorRevenueRequest request);
}