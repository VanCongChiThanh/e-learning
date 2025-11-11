package com.pbl.elearning.commerce.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.pbl.elearning.enrollment.models.Enrollment;
import com.pbl.elearning.enrollment.payload.request.EnrollmentRequest;

import reactor.core.publisher.Mono;

@Service
public class EnrollmentClient {
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api-coursevo-dev.id.vn/api/v1/enrollments")
            // .baseUrl("https://0c54e222adbf.ngrok-free.app/api/v1/enrollments")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public Mono<Enrollment> grantAccessToCourse(EnrollmentRequest request) {
        return webClient.post()
                .body(Mono.just(request), EnrollmentRequest.class)
                .retrieve()
                .bodyToMono(Enrollment.class);
    }
}