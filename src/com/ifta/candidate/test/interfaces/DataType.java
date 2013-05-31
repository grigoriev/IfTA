package com.ifta.candidate.test.interfaces;

public enum DataType {
  /** Time as a Java long value as given by System.currentTimeMillis(). */
  TIME(8),
  /** A double value. */
  DOUBLE(8);

  public final int sizeInBytes;

  private DataType(int sizeInBytes) {
    this.sizeInBytes = sizeInBytes;
  }
}
