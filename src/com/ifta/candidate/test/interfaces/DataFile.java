package com.ifta.candidate.test.interfaces;

import java.io.*;
import java.util.*;

/**
 * Interface for a class providing data from a binary file.
 * 
 * <p>
 * The binary file is in little Endian and is structured as follows:
 * 
 * <ul>
 * <li>1 integer: containing the number of signals in the file = <b>N</b>.
 * <li><b>N</b> signal descriptions, each consisting of:
 * <ul>
 * <li>0 (zero) terminated ANSI ASCII byte string which is the signal name.
 * <li>0 (zero) terminated ANSI ASCII byte string which is the signal unit.
 * <li>1 integer designating the data type <b>T</b> according to the ordinal of the enums
 * in DataType.
 * </ul>
 * <li><b>M</b> Data sets: One value for each signal according to its data type <b>T</b>,
 * i.e. <b>N</b> values per data set. Data sets repeat until the end of the file.
 * </ul>
 * 
 * 
 * 
 * <p>
 * The data access should be realized by an iterator.
 * 
 * @see DataType
 * @see Signal
 */
public interface DataFile {
  /**
   * Open the given file.
   * 
   * @param file
   *          to open.
   */
  public void open(File file) throws FileNotFoundException, IOException;

  /** Close currently opened file. */
  public void close() throws IOException;

  /** Return list of signal in the currently opened file. */
  public List<Signal> getSignals();

  /** Return the number (<b>M</b>) of data sets stored in the currently opened file. */
  public long getDataCount() throws IOException;

  /**
   * Return Iterable for the given signal to access its data. It is o.k. to return Long
   * values as an Double.
   * 
   * Example usage:
   * 
   * <pre>
   * for (Double d : dataFile.read(signal)) {
   *   System.out.println(d);
   * }
   * </pre>
   * 
   * @param signal
   *          to read data from.
   */
  public Iterable<Double> read(final Signal signal);
}
