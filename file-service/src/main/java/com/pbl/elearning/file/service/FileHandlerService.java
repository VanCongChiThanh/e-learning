package com.pbl.elearning.file.service;

import java.io.File;

public interface FileHandlerService {
  String uploadFile(File file,String fileName, String folder);
}