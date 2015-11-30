package com.jlg.env.cli.common.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

@Component
public class CommandExecutor {
  private final JCommander jc;
  private final List<Command> commands;

  @Autowired
  public CommandExecutor(List<Command> commands) {
    this.commands = commands;
    this.jc = new JCommander();
  }

  public void run(String... args) {
    if (!parsedCommandIsValid(args)) {
      return;
    }
    executeCommandOrPrintUsage(jc.getParsedCommand(), (c) -> {
      c.execute();
    });
  }

  private void executeCommandOrPrintUsage(String name, Consumer<Command> consumer) {
    Optional<Command> match = commands.stream().filter(c -> c.isNamed(ofNullable(jc.getParsedCommand()).orElse("")
        .toLowerCase())).findFirst();
    if (match.isPresent()) {
      consumer.accept(match.get());
      return;
    }
    jc.usage();
  }

  private boolean parsedCommandIsValid(String[] args) {
    try {
      jc.parse(args);
    } catch (ParameterException e) {
      System.out.println(e.getMessage());
      return false;
    }
    return true;
  }

  @PostConstruct
  public void postConstruct() {
    commands.forEach(jc::addCommand);
  }
}
