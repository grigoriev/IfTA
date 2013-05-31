package com.ifta.candidate.test.grigoriev;

import com.ifta.candidate.test.interfaces.Signal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Sergey Grigoriev
 * Created: 27.03.13 17:58
 */
public class CsvExport {

    public static final String TIME = "time";
    public static final String TEMPERATURE = "temperature";
    public static final String HUMIDITY = "humidity";
    public static final String END_OF_LINE = ",\n";

    public static final String TIME_OUTPUT_FILE_NAME = "data/" + TIME + ".csv";
    public static final String TEMPERATURE_OUTPUT_FILE_NAME = "data/" + TEMPERATURE + ".csv";
    public static final String HUMIDITY_OUTPUT_FILE_NAME = "data/" + HUMIDITY + ".csv";

    public static final String PLOT_OUTPUT_FILE_NAME = "data/plot.csv";

    public static void main(String[] args) {
        try {
            BinaryDataFile binaryDataFile = new BinaryDataFile();
            binaryDataFile.open(new File(BinaryDataFile.DEFAULT_FILE_NAME));
            List<Signal> signals = binaryDataFile.getSignals();

            File timeFile = new File(TIME_OUTPUT_FILE_NAME);
            if (timeFile.exists()) {
                timeFile.delete();
            }
            File temperatureFile = new File(TEMPERATURE_OUTPUT_FILE_NAME);
            if (temperatureFile.exists()) {
                temperatureFile.delete();
            }
            File humidityFile = new File(HUMIDITY_OUTPUT_FILE_NAME);
            if (humidityFile.exists()) {
                humidityFile.delete();
            }
            File plotFile = new File(PLOT_OUTPUT_FILE_NAME);
            if (plotFile.exists()) {
                plotFile.delete();
            }

            FileWriter timeWriter = new FileWriter(timeFile);
            FileWriter temperatureWriter = new FileWriter(temperatureFile);
            FileWriter humidityWriter = new FileWriter(humidityFile);

            Iterable<Double> time = new ArrayList<Double>();
            Iterable<Double> temperature = new ArrayList<Double>();
            Iterable<Double> humidity = new ArrayList<Double>();

            for (Signal signal: signals) {
                if (signal.getName().equalsIgnoreCase(TIME)) {
                    time = binaryDataFile.read(signal);
                    for (Double d: time) {
                        timeWriter.append(d.toString() + END_OF_LINE);
                    }
                }
                if (signal.getName().equalsIgnoreCase(TEMPERATURE)) {
                    temperature = binaryDataFile.read(signal);
                    for (Double d: temperature) {
                        temperatureWriter.append(d.toString() + END_OF_LINE);
                    }
                }
                if (signal.getName().equalsIgnoreCase(HUMIDITY)) {
                    humidity = binaryDataFile.read(signal);
                    for (Double d: humidity) {
                        humidityWriter.append(d.toString() + END_OF_LINE);
                    }
                }
            }

            timeWriter.flush();
            timeWriter.close();

            temperatureWriter.flush();
            temperatureWriter.close();

            humidityWriter.flush();
            humidityWriter.close();

            FileWriter plotWriter = new FileWriter(plotFile);

            long dataCount = binaryDataFile.getDataCount();
            plotWriter.append(", Temperature, Humidity" + END_OF_LINE);
            Iterator iteratorTime = time.iterator();
            Iterator iteratorTemperature = temperature.iterator();
            Iterator iteratorHumidity = humidity.iterator();
            for (long l = 0; l < dataCount; l++) {
                Double timeValue = (Double) iteratorTime.next();
                Double temperatureValue = (Double) iteratorTemperature.next();
                Double humidityValue = (Double) iteratorHumidity.next();
                plotWriter.append(new Date(timeValue.longValue()).toString() + ", " + temperatureValue + ", " + humidityValue + END_OF_LINE);
            }

            plotWriter.flush();
            plotWriter.close();

            binaryDataFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
