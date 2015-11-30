package com.jlg.env.cli.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class EnvironmentConfiguration {

  private final ObjectMapper objectMapper;

  @Autowired
  public EnvironmentConfiguration(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Environment load(String file) {
    try {
      return objectMapper.readValue(getFile(file), Environment.class);
    } catch (IOException e) {
      e.printStackTrace();
      //TODO:: throw exception
      return null;
    }
  }

  public void save(Environment environment, String file) {
    try {
      objectMapper.writeValue(getFile(file), environment);
    } catch (IOException e) {
      e.printStackTrace();
      //TODO:: throw exception
    }
  }

  private File getFile(String file) {
    return new File(file);
  }
}
