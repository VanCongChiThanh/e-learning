package com.pbl.elearning.file.service;

import com.amazonaws.HttpMethod;
import com.pbl.elearning.file.payload.response.PresignedURLResponse;

public interface VideoHandlerService {
    PresignedURLResponse generateVideoPresignedUrl(String fileName, HttpMethod httpMethod, int expiryMinutes);
}
