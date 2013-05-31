package com.ifta.candidate.test.interfaces;

/**
 * Signal meta data.
 */
public interface Signal {
  /**
   * @return The name of the signal.
   */
  public String getName();

  /**
   * @return The unit of the signal.
   */
  public String getUnit();

  /**
   * @return The data type of the signal.
   */
  public DataType getDataType();
}
