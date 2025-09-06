package com.pbl.elearning.file.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.pbl.elearning.file.payload.response.PresignedURLResponse;
import com.pbl.elearning.file.service.FileHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Calendar;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileHandlerServiceImpl implements FileHandlerService {

  private final AmazonS3 s3;

  @Value("${aws.s3.bucket}")
  private String bucketName;

  public PresignedURLResponse generatePresignedUrl(String fileName, HttpMethod httpMethod) {
      Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add(Calendar.MINUTE,10 );
      URL url = s3.generatePresignedUrl(bucketName, fileName, calendar.getTime(), httpMethod);
        return PresignedURLResponse.builder()
                .key(fileName)
                .url(url.toString())
                .build();
  }
}