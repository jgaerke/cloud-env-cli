package com.jlg.env.cli.common.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.jlg.env.cli.common.EnvironmentConfiguration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Parameters(commandNames = ResetEnvironment.NAME, commandDescription = "Reset environment")
@Component
@Getter
public class ResetEnvironment implements Command {
  static final String NAME = "reset";
  private EnvironmentConfiguration environmentConfiguration;

  @Parameter(names = {"-f", "--file"}, description = "Environment configuration file", required = true)
  private String file;

  @Autowired
  public ResetEnvironment(
      EnvironmentConfiguration environmentConfiguration) {
    this.environmentConfiguration = environmentConfiguration;
  }

  @Override
  public boolean isNamed(String name) {
    return NAME.equals(name);
  }

  @Override
  public void execute() {
    environmentConfiguration.save(environmentConfiguration.load(file).reset(), file);
  }

}
