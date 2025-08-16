package com.pbl.elearning.file.util;


import com.pbl.elearning.common.constant.MessageConstant;
import com.pbl.elearning.common.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.Objects;

@Slf4j
public final class FileUtils {

  private FileUtils() {}

  /**
   * Delete file
   *
   * @param file {@link File}
   */
  public static void deleteFile(File file) {
    if (file.delete()) {
      return;
    }
    throw new BadRequestException(MessageConstant.FILE_IS_DELETED_FAILED);
  }

  public static String getMineType(File file) throws IOException {
    Tika tika = new Tika();
    return tika.detect(file);
  }

  public static long getFileSize(File file) throws IOException {
    return Files.size(file.toPath());
  }

  public static String getFileName(MultipartFile multipartFile) {
    return multipartFile.getOriginalFilename();
  }

  public static File convertMultiPartToFile(MultipartFile file) {
    File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
    try (FileOutputStream fos = new FileOutputStream(convFile)) {
      fos.write(file.getBytes());
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return convFile;
  }

  public static void checkSizeFile(File file) {
    long size;
    try {
      size = Files.size(file.toPath());
      if (size > 10485760) {
        FileUtils.deleteFile(file);
        throw new BadRequestException(MessageConstant.MAXIMUM_UPLOAD_SIZE_EXCEEDED);
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  public static String generateFileName(String fileName) {
    return new Date().getTime() + "-" + fileName.replace(" ", "_");
  }
}