package com.jlg.env.cli.digital.ocean.exception;

import lombok.Getter;

@Getter
public class DropletCreationTimeoutException extends RuntimeException {
  private static final long serialVersionUID = -7319410941492276913L;
  private int id;

  public DropletCreationTimeoutException(int id, String message) {
    super(message);
    this.id = id;
  }
}
