package com.jlg.env.cli.common.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.jlg.env.cli.common.EnvironmentConfiguration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Parameters(commandNames = ActivateEnvironment.NAME, commandDescription = "Activate an environment")
@Component
@Getter
public class ActivateEnvironment implements Command {
  static final String NAME = "activate";
  private EnvironmentConfiguration environmentConfiguration;

  @Parameter(names = {"-f", "--file"}, description = "Environment configuration file", required = true)
  private String file;

  @Parameter(names = {"-s", "--server"}, description = "Server name", required = true)
  private String serverName;

  @Autowired
  public ActivateEnvironment(EnvironmentConfiguration environmentConfiguration) {
    this.environmentConfiguration = environmentConfiguration;
  }

  @Override
  public boolean isNamed(String name) {
    return NAME.equals(name);
  }

  @Override
  public void execute() {
    environmentConfiguration.save(environmentConfiguration.load(file).activate(serverName), file);
  }

}
