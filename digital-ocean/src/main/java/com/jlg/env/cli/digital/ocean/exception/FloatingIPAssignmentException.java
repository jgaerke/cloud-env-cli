package com.jlg.env.cli.digital.ocean.exception;

public class FloatingIPAssignmentException extends RuntimeException {
  private static final long serialVersionUID = -8903429460830549575L;

  public FloatingIPAssignmentException(String message) {
    super(message);
  }

  public FloatingIPAssignmentException(String message, Throwable cause) {
    super(message, cause);
  }
}
