package com.pbl.elearning.file.service;

import com.amazonaws.HttpMethod;
import com.pbl.elearning.file.payload.response.PresignedURLResponse;

import java.io.File;

public interface FileHandlerService {
  PresignedURLResponse generatePresignedUrl(String fileName, HttpMethod httpMethod);

}