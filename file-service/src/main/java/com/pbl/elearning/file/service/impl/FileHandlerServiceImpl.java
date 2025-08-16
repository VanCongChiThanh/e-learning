package com.pbl.elearning.file.service.impl;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import com.pbl.elearning.common.constant.MessageConstant;
import com.pbl.elearning.common.exception.BadRequestException;
import com.pbl.elearning.file.service.FileHandlerService;
import com.pbl.elearning.file.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileHandlerServiceImpl implements FileHandlerService {

  private final Cloudinary cloudinary;

  @Override
  public String uploadFile(File file, String fileName,String folder) {
    String fileUrl = "";
    try {
      String mimeType = FileUtils.getMineType(file);
      if (this.checkMimeType(mimeType)) {
        fileName = FileUtils.generateFileName(fileName);
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "public_id", fileName,
                "folder", folder,
                "resource_type", "auto"
        );
        Map uploadResult = cloudinary.uploader().upload(file, uploadParams);
        fileUrl = (String) uploadResult.get("secure_url");
      } else {
        throw new BadRequestException(MessageConstant.FILE_NOT_FORMAT);
      }
    } catch (BadRequestException ex) {
      throw ex;
    } catch (Exception e) {
      log.error("Unexpected error during Cloudinary upload: {}", e.getMessage());
    }
    return fileUrl;
  }

  private boolean checkMimeType(String mimeType) {
    return mimeType.startsWith("application")
        || mimeType.equals("application/msword")
        || mimeType.startsWith("image")
        || mimeType.startsWith("video")
        || mimeType.startsWith("audio")
        || mimeType.equals("text/plain")
        || mimeType.equals("text/html")
        || mimeType.equals("text/csv");
  }

}