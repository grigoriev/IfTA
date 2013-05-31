package com.ifta.candidate.test.grigoriev;

import com.ifta.candidate.test.interfaces.Signal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Author: Sergey Grigoriev
 * Created: 27.03.13 17:51
 */
public class BinaryDataFileTest {
    private static final int ACTUAL_NUMBER_OF_SIGNALS = 3;
    private static final long ACTUAL_NUMBER_OF_DATASETS = 18336;

    private static final String TIME = "Time";
    private static final String TEMPERATURE = "Temperature";
    private static final String HUMIDITY = "Humidity";
    private static final long TIME_LAST_VALUE = 1353024354602L;
    private static final String TIME_LAST_VALUE_STRING = "Fri Nov 16 03:05:54 FET 2012";
    private static final double TEMPERATURE_LAST_VALUE = 297.53;
    private static final double HUMIDITY_LAST_VALUE = 23.07;

    private BinaryDataFile binaryDataFile;

    @Before
    public void setUp() throws Exception {
        binaryDataFile = new BinaryDataFile();
        binaryDataFile.open(new File(BinaryDataFile.DEFAULT_FILE_NAME));
    }

    @After
    public void tearDown() throws Exception {
        binaryDataFile.close();
    }

    @Test(expected = FileNotFoundException.class)
    public void testOpenAndNotFound() throws Exception {
        BinaryDataFile nonValidName = new BinaryDataFile();
        nonValidName.open(new File("file_not_exists"));
    }

    @Test
    public void testOpenAndClose() throws Exception {
        BinaryDataFile binaryDataFile = new BinaryDataFile();
        binaryDataFile.open(new File(BinaryDataFile.DEFAULT_FILE_NAME));
        binaryDataFile.close();
    }

    @Test
    public void testGetSignals() throws Exception {
        List<Signal> signals = binaryDataFile.getSignals();

        assertEquals(ACTUAL_NUMBER_OF_SIGNALS, signals.size());
        assertEquals(TIME, signals.get(0).getName());
        assertEquals(TEMPERATURE, signals.get(1).getName());
        assertEquals(HUMIDITY, signals.get(2).getName());
    }

    @Test
    public void testGetDataCount() throws Exception {
        long countRecords = binaryDataFile.getDataCount();

        assertEquals(ACTUAL_NUMBER_OF_DATASETS, countRecords);
    }

    @Test
    public void testReadTime() throws Exception {
        List<Signal> signals = binaryDataFile.getSignals();
        long lastValue = 0L;

        for (Double d : binaryDataFile.read(signals.get(0))) {
            lastValue = d.longValue();
        }

        String lastValueDate = new Date(lastValue).toString();

        assertEquals(TIME_LAST_VALUE, lastValue);
        assertEquals(TIME_LAST_VALUE_STRING, lastValueDate);
    }

    @Test
    public void testReadTemperature() throws Exception {
        List<Signal> signals = binaryDataFile.getSignals();
        double lastValue = 0.0;

        for (Double d : binaryDataFile.read(signals.get(1))) {
            lastValue = d;
        }

        assertEquals(TEMPERATURE_LAST_VALUE, lastValue);
    }

    @Test
    public void testReadHumidity() throws Exception {
        List<Signal> signals = binaryDataFile.getSignals();
        double lastValue = 0.0;

        for (Double d : binaryDataFile.read(signals.get(2))) {
            lastValue = d;
        }

        assertEquals(HUMIDITY_LAST_VALUE, lastValue);
    }
}
