package com.jlg.env.cli.common.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.jlg.env.cli.common.EnvironmentConfiguration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Parameters(commandNames = DeactivateEnvironment.NAME, commandDescription = "Deactivate an environment")
@Component
@Getter
public class DeactivateEnvironment implements Command {
  static final String NAME = "deactivate";
  private EnvironmentConfiguration environmentConfiguration;

  @Parameter(names = {"-f", "--file"}, description = "Environment configuration file", required = true)
  private String file;

  @Autowired
  public DeactivateEnvironment(EnvironmentConfiguration environmentConfiguration) {
    this.environmentConfiguration = environmentConfiguration;
  }

  @Override
  public boolean isNamed(String name) {
    return NAME.equals(name);
  }

  @Override
  public void execute() {
    environmentConfiguration.save(environmentConfiguration.load(file).deactivate(), file);
  }

}
