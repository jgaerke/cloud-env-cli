package com.jlg.env.cli.common.service;

import com.google.common.collect.ImmutableMap;
import com.jlg.env.cli.common.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.beust.jcommander.internal.Lists.newArrayList;
import static java.lang.System.getProperty;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;

@Component
public class ScriptService {
  public static final String WORKING_DIR = getProperty("java.io.tmpdir");
  public static final String NEW_LINE_CHAR = lineSeparator();

  private final AnnotationConfigApplicationContext resourceLoader;
  private final DirectoryService directoryService;
  private final FileService fileService;

  @Autowired
  public ScriptService(
      AnnotationConfigApplicationContext resourceLoader,
      DirectoryService directoryService,
      FileService fileService) {
    this.resourceLoader = resourceLoader;
    this.directoryService = directoryService;
    this.fileService = fileService;
  }

  public ScriptService environment(Server server, ImmutableMap<String, String> environmentVariables) {
    if (environmentVariables.size() > 0) {
      StringBuilder builder = new StringBuilder();
      builder.append("#!/bin/bash" + NEW_LINE_CHAR);
      builder.append("ssh-keygen -R " + server.getIpAddress() + NEW_LINE_CHAR);
      builder.append("ssh -oStrictHostKeyChecking=no root@" + server.getIpAddress() + " '");
      environmentVariables.forEach((k, v) -> {
        builder.append("echo " + k.toUpperCase() + "=" + v + " >> /etc/environment; ");
      });
      builder.append("' source /etc/environment");
      fileService.write(getServerScopedWorkingDir(server), new ByteArrayInputStream(builder.toString().getBytes()),
          "environment.sh");
      execute(server, "environment");
    }
    return this;
  }

  public ScriptService extract(Server server, String classPath) {
    Resource[] resources = null;
    try {
      resources = resourceLoader.getResources("classpath:" + classPath);
      asList(resources).forEach(r -> {
        try {
          fileService.write(getServerScopedWorkingDir(server), r.getInputStream(), r.getFilename());
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  public ScriptService execute(Server server, String script, String... args) {
    Process process = null;
    try {
      List<String> scriptArgs = newArrayList("./" + script + ".sh");
      scriptArgs.addAll(asList(args));
      ProcessBuilder processBuilder = new ProcessBuilder(scriptArgs.toArray(new String[scriptArgs.size()]));
      processBuilder.redirectErrorStream(true);
      processBuilder.directory(new File(getServerScopedWorkingDir(server)));
      processBuilder.inheritIO();
      process = processBuilder.start();
      process.waitFor();
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      if (process != null) {
        process.destroy();
      }
    }
    return this;
  }

  public ScriptService cleanUp(Server server) {
    directoryService.delete(getServerScopedWorkingDir(server));
    return this;
  }

  private String getServerScopedWorkingDir(Server server) {
    String dir = WORKING_DIR + "/" + server.getId();
    directoryService.createIfAbsent(dir);
    return dir;
  }
}
