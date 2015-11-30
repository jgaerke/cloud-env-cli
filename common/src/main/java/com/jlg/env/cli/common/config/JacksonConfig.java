package com.jlg.env.cli.common.config;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlg.env.cli.common.service.CloudService;
import com.jlg.env.cli.common.service.ScriptService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.PropertyResolver;

import static com.fasterxml.jackson.databind.SerializationFeature.*;

@Configuration
public class JacksonConfig {
  @Bean
  @Primary
  public ObjectMapper objectMapper(
      CloudService cloudService,
      PropertyResolver propertyResolver,
      AnnotationConfigApplicationContext resourceLoader,
      ScriptService scriptService) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(INDENT_OUTPUT);
    InjectableValues injectables = new InjectableValues.Std()
        .addValue("cloudService", cloudService)
        .addValue("propertyResolver", propertyResolver)
        .addValue("resourceLoader", resourceLoader)
        .addValue("scriptManager", scriptService);
    objectMapper.setInjectableValues(injectables);
    return objectMapper;
  }
}
