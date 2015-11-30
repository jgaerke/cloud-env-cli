package com.jlg.env.cli.common.service;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class DirectoryService {

  public void delete(String dir) {
    try {
      FileUtils.deleteDirectory(new File(dir));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void createIfAbsent(String dir) {
    try {
      if (!Files.exists(Paths.get(dir))) {
        Files.createDirectory(Paths.get(dir));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
