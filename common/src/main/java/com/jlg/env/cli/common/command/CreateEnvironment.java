package com.jlg.env.cli.common.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.jlg.env.cli.common.EnvironmentConfiguration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Parameters(commandNames = CreateEnvironment.NAME, commandDescription = "Create environment")
@Component
@Getter
public class CreateEnvironment implements Command {
  static final String NAME = "create";
  private EnvironmentConfiguration environmentConfiguration;

  @Parameter(names = {"-f", "--file"}, description = "Environment configuration file", required = true)
  private String file;

  @Autowired
  public CreateEnvironment(
      EnvironmentConfiguration environmentConfiguration) {
    this.environmentConfiguration = environmentConfiguration;
  }

  @Override
  public boolean isNamed(String name) {
    return NAME.equals(name);
  }

  @Override
  public void execute() {
    environmentConfiguration.save(environmentConfiguration.load(file).reset().create(), file);
  }

}
