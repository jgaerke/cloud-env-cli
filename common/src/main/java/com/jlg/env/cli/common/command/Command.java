package com.jlg.env.cli.common.command;

public interface Command {
  boolean isNamed(String name);
  void execute();
}
