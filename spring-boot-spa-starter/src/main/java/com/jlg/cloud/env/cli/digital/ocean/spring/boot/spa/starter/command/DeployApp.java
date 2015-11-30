package com.jlg.cloud.env.cli.digital.ocean.spring.boot.spa.starter.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.jlg.cloud.env.cli.digital.ocean.spring.boot.spa.starter.Operations;
import com.jlg.env.cli.common.EnvironmentConfiguration;
import com.jlg.env.cli.common.command.Command;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Parameters(commandNames = DeployApp.NAME, commandDescription = "Deploy application")
@Component
@Getter
public class DeployApp implements Command {
  static final String NAME = "deploy-app";
  private final EnvironmentConfiguration environmentConfiguration;

  @Parameter(names = {"-f", "--file"}, description = "Environment configuration file", required = true)
  private String file;

  @Parameter(names = {"-t", "--type"}, description = "Server type", required = true)
  private String serverType;

  @Parameter(names = {"-ru", "--repo-url"}, description = "Repo url", required = true)
  private String repoUrl;

  @Parameter(names = {"-rn", "--repo-name"}, description = "Repo name", required = true)
  private String repoName;

  @Parameter(names = {"-j", "--jar"}, description = "JAR name", required = true)
  private String jar;

  @Parameter(names = {"-jd", "--jar-dir"}, description = "JAR name", required = true)
  private String jarDir;

  @Autowired
  public DeployApp(EnvironmentConfiguration environmentConfiguration) {
    this.environmentConfiguration = environmentConfiguration;
  }

  @Override
  public boolean isNamed(String name) {
    return NAME.equals(name);
  }

  @Override
  public void execute() {
    Operations.from(environmentConfiguration.load(file)).deployApp(serverType, repoUrl, repoName, jarDir, jar);
  }

}
