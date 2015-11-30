package com.jlg.env.cli.common.service;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import static java.nio.file.Files.setPosixFilePermissions;
import static java.nio.file.attribute.PosixFilePermission.*;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;

@Component
public class FileService {
  public void write(String dir, InputStream inputStream, String fileName) {
    try {
      File file = new File(dir + "/" + fileName);
      file.deleteOnExit();
      FileOutputStream outputStream = new FileOutputStream(file);
      copy(inputStream, outputStream);
      closeQuietly(inputStream);
      closeQuietly(outputStream);
      Set<PosixFilePermission> perms = new HashSet<>();
      perms.add(OWNER_READ);
      perms.add(OWNER_WRITE);
      perms.add(OWNER_EXECUTE);
      setPosixFilePermissions(file.toPath(), perms);
    } catch (IOException e) {
      //TODO:: throw an appropriate exception
    }
  }

  public void delete(String dir, String fileName) {
    try {
      Files.delete(Paths.get(dir, fileName));
    } catch (IOException e) {
      //TODO:: throw an appropriate exception
    }
  }
}
