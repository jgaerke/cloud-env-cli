package com.jlg.env.cli.digital.ocean.exception;

public class DropletCreationException extends RuntimeException {
  private static final long serialVersionUID = 9184798734073976778L;

  public DropletCreationException(String message) {
    super(message);
  }

  public DropletCreationException(String message, Throwable cause) {
    super(message, cause);
  }

  public DropletCreationException(Throwable cause) {
    super(cause);
  }
}
