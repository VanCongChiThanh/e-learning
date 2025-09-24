package com.pbl.elearning.file.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.pbl.elearning.file.payload.response.PresignedURLResponse;
import com.pbl.elearning.file.service.VideoHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VideoHandlerServiceImpl implements VideoHandlerService {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3;

    public PresignedURLResponse generateVideoPresignedUrl(String fileName, HttpMethod httpMethod, int expiryMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, expiryMinutes);

        URL url = s3.generatePresignedUrl(bucketName, fileName, calendar.getTime(), httpMethod);

        return PresignedURLResponse.builder()
                .key(fileName)
                .url(url.toString())
                .build();
    }

}
