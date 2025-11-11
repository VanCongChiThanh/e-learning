package com.pbl.elearning.commerce.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CourseClient {
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api-coursevo-dev.id.vn/api/v1/courses")
            // .baseUrl("https://0c54e222adbf.ngrok-free.app/api/v1/courses")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    // kiểm tra course ứng với course id có tồn tại không
    public Boolean isCourseExist(String courseId) {
        try {
            CourseResponse response = webClient.get()
                    .uri("/{courseId}", courseId)
                    .retrieve()
                    .bodyToMono(CourseResponse.class)
                    .block();
            return response != null && "success".equalsIgnoreCase(response.getStatus());
        } catch (Exception e) {
            return false;
        }

    }

    public CourseResponse getCourseDetails(String courseId) {
        try {
            CourseResponse response = webClient.get()
                    .uri("/{courseId}", courseId)
                    .retrieve()
                    .bodyToMono(CourseResponse.class)
                    .block();
            return response;
        } catch (Exception e) {
            return null;
        }
    }

    public static class CourseResponse {
        private String status;
        private Object data;
        private Object error;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public Object getError() {
            return error;
        }

        public void setError(Object error) {
            this.error = error;
        }
    }
}
