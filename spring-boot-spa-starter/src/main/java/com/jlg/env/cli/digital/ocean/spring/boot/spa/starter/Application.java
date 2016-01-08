package com.jlg.env.cli.digital.ocean.spring.boot.spa.starter;

import com.google.common.collect.Lists;
import com.jlg.env.cli.common.command.CommandExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;

@SpringBootApplication
@ComponentScan(basePackages = "com.jlg.env.cli")
public class Application {
  public static void main(String[] args) {
    if(!isEnvironmentConfigured()) {
      return;
    }
    ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
    CommandExecutor commandExecutor = ctx.getBean(CommandExecutor.class);
    commandExecutor.run(args);
  }

  private static boolean isEnvironmentConfigured() {
    List<String> missingProperties = getMissingEnvironment();
    if (!missingProperties.isEmpty()) {
      System.out.println("The following are required properties and must be set:");
      missingProperties.forEach(System.out::println);
      return false;
    }
    return true;
  }

  private static List<String> getMissingEnvironment() {
    Map<String, String> environment = System.getenv();
    List<String> requiredVars = getRequiredVars();
    return requiredVars.stream()
        .filter(v -> !environment.containsKey(v) || isNullOrEmpty(environment.get(v)))
        .collect(toList());
  }

  private static List<String> getRequiredVars() {
    return Lists.newArrayList(
        "AWS_ACCESS_KEY_ID",
        "AWS_SECRET_ACCESS_KEY",
        "AWS_S3_DB_BACKUP_BUCKET_NAME",
        "DB_NAME",
        "DB_ADMIN_USER_NAME",
        "DB_ADMIN_USER_PASSWORD",
        "DB_USER_NAME",
        "DB_USER_PASSWORD",
        "DIGITAL_OCEAN_API_BASE_URL",
        "DIGITAL_OCEAN_API_TOKEN",
        "DIGITAL_OCEAN_DROPLET_CREATION_WAIT_TIME_IN_SECONDS",
        "DIGITAL_OCEAN_SSH_KEY",
        "MAILGUN_API_TOKEN",
        "MAILGUN_API_BASE_URL",
        "MAILGUN_DOMAIN",
        "MAILGUN_FROM_ACCOUNT"
    );
  }
}
