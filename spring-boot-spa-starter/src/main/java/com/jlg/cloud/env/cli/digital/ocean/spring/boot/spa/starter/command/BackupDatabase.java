package com.jlg.cloud.env.cli.digital.ocean.spring.boot.spa.starter.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.jlg.cloud.env.cli.digital.ocean.spring.boot.spa.starter.Operations;
import com.jlg.env.cli.common.EnvironmentConfiguration;
import com.jlg.env.cli.common.command.Command;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Parameters(commandNames = BackupDatabase.NAME, commandDescription = "Backup database")
@Component
@Getter
public class BackupDatabase implements Command {
  static final String NAME = "backup-db";
  private final EnvironmentConfiguration environmentConfiguration;

  @Parameter(names = {"-f", "--file"}, description = "Environment configuration file", required = true)
  private String file;

  @Parameter(names = {"-s", "--server"}, description = "Server name", required = true)
  private String serverName;

  @Autowired
  public BackupDatabase(EnvironmentConfiguration environmentConfiguration) {
    this.environmentConfiguration = environmentConfiguration;
  }

  @Override
  public boolean isNamed(String name) {
    return NAME.equals(name);
  }

  @Override
  public void execute() {
    Operations.from(environmentConfiguration.load(file)).backupDatabase(serverName);
  }

}
