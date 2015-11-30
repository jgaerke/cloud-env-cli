package com.jlg.cloud.env.cli.digital.ocean.spring.boot.spa.starter;

import com.google.common.base.Strings;
import com.jlg.env.cli.common.Environment;
import com.jlg.env.cli.common.Server;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Operations {
  private static final String DB_BACKUP_SCRIPT_NAME = "exec-backup";
  private static final String DB_RESTORE_SCRIPT_NAME = "exec-restore";
  private static final String DB_RESTORE_LATEST_SCRIPT_NAME = "exec-restore-latest";
  private static final String APP_DEPLOY_SCRIPT_NAME = "deploy";

  private final Environment environment;

  public Operations(Environment environment) {
    this.environment = environment;
  }

  public Operations backupDatabase(String serverName) {
    Server server = environment
        .getServers()
        .stream()
        .filter(s -> s.getName().equals(serverName))
        .findFirst()
        .get();
    server.execute(DB_BACKUP_SCRIPT_NAME, server.getIpAddress());
    return this;
  }

  public Operations deployApp(String serverType, String repoUrl, String repoName, String jarDir, String jar) {
    List<Server> servers = environment
        .getServers()
        .stream()
        .filter(s -> s.getType().equals(serverType))
        .collect(toList());

    //TODO:: throw exception if any of the servers are not in a "PROVISIONED" status.

    List<CompletableFuture<Server>> deployments = servers
        .stream()
        .map(s -> CompletableFuture.supplyAsync(() -> {
          s.execute(APP_DEPLOY_SCRIPT_NAME, s.getIpAddress(), repoUrl, repoName, jarDir, jar);
          return s;
        }))
        .collect(Collectors.toList());

    CompletableFuture.allOf(deployments.toArray(new CompletableFuture[deployments.size()])).join();

    return this;
  }

  public Operations restoreDatabaseBackup(String serverName, String backupFileName) {
    Server server = environment
        .getServers()
        .stream()
        .filter(s -> s.getName().equals(serverName))
        .findFirst()
        .get();
    if(Strings.isNullOrEmpty(backupFileName)) {
      server.execute(DB_RESTORE_LATEST_SCRIPT_NAME, server.getIpAddress());
    } else {
      server.execute(DB_RESTORE_SCRIPT_NAME, server.getIpAddress(), backupFileName);
    }
    return this;
  }


  public static Operations from(Environment environment) {
    return new Operations(environment);
  }
}
