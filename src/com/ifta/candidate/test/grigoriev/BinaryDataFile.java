package com.ifta.candidate.test.grigoriev;

import com.ifta.candidate.test.interfaces.DataFile;
import com.ifta.candidate.test.interfaces.DataType;
import com.ifta.candidate.test.interfaces.Signal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Sergey Grigoriev
 * Created: 27.03.13 17:03
 */
public class BinaryDataFile implements DataFile {
    public static final String DEFAULT_FILE_NAME = "data/test.bin";
    public static final int INTEGER_LENGTH = 4;
    public static final int DOUBLE_LENGTH = 8;
    public static final int SIGNAL_NAME_LENGTH = 256;

    public static final int TYPE_TIME = 0;
    public static final int TYPE_DOUBLE = 1;


    private RandomAccessFile randomAccessFile;
    private List<Signal> signals;
    private File binaryfile;

    @Override
    public void open(File file) throws FileNotFoundException, IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }

        binaryfile = file;
        randomAccessFile = new RandomAccessFile(file, "r");
        signals = readSignals();
    }

    @Override
    public void close() throws IOException {
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
        signals.clear();
        signals = null;
        binaryfile = null;
    }

    @Override
    public List<Signal> getSignals() {
        return signals;
    }

    @Override
    public long getDataCount() throws IOException {
        return ((getFileSize() - getHeaderSize()) / getDataSetSize());
    }

    @Override
    public Iterable<Double> read(Signal signal) {
        try {
            setPositionAfterHeader();
            return readSignalValues(signal);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // implementation

    private List<Signal> readSignals() throws IOException {
        ArrayList<Signal> result = new ArrayList<Signal>();

        int countOfSignals = readCountOfSignals();
        for (int i = 0; i < countOfSignals; i++) {
            Signal signal = readSignal();
            result.add(signal);
        }

        return result;
    }

    private int readCountOfSignals() throws IOException {
        setPositionBeforeHeader();
        return readLittleEndianInt();
    }

    private Signal readSignal() throws IOException {
        String signalName = readSignalName();
        String signalUnit = readSignalUnit();
        DataType signalDataType = readSignalDataType();
        return new BinarySignal(signalName, signalUnit, signalDataType);
    }

    private DataType readSignalDataType() throws IOException {
        int type = readLittleEndianInt();
        if (type == TYPE_TIME) {
            return DataType.TIME;
        } else if (type == TYPE_DOUBLE) {
            return DataType.DOUBLE;
        }
        return null;
    }

    private int readLittleEndianInt() throws IOException {
        byte buffer[] = new byte[INTEGER_LENGTH];
        randomAccessFile.readFully(buffer);

        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private String readSignalUnit() throws IOException {
        return readANSIString();
    }

    private String readSignalName() throws IOException {
        return readANSIString();
    }

    private String readANSIString() throws IOException {
        byte buffer[] = new byte[SIGNAL_NAME_LENGTH];

        boolean nullTerminatorFound = false;
        int i = 0;
        do {
            byte ch = randomAccessFile.readByte();
            if (ch == 0) {
                buffer[i] = '\0';
                nullTerminatorFound = true;
            } else {
                buffer[i] = ch;
                i++;
            }
        } while (!nullTerminatorFound);

        return new String(buffer, 0, i);
    }

    private long getDataSetSize() {
        int result = 0;
        for (Signal signal : signals) {
            result += signal.getDataType().sizeInBytes;
        }
        return result;
    }

    private long getHeaderSize() {
        int result = INTEGER_LENGTH; // number of signals
        for (Signal signal : getSignals()) {
            result += (signal.getName().length() + 1) + (signal.getUnit().length() + 1) + INTEGER_LENGTH; // name + unit + data type
        }
        return result;
    }

    private long getFileSize() {
        return binaryfile.length();
    }

    private void setPositionBeforeHeader() throws IOException {
        randomAccessFile.seek(0);
    }

    private void setPositionAfterHeader() throws IOException {
        randomAccessFile.seek(getHeaderSize());
    }

    private void setPositionToDataSet(long datasetPointer) throws IOException {
        long position = getHeaderSize() + (datasetPointer*getDataSetSize());
        randomAccessFile.seek(position);
    }

    // read

    private Iterable<Double> readSignalValues(Signal signal) throws IOException {
        ArrayList<Double> result = new ArrayList<Double>();
        long countOfDatasets = getDataCount();

        for (long l = 0; l < countOfDatasets; l++) {
            Double value = readSignalValue(signal);
            result.add(value);
        }

        return result;
    }

    private byte[] readDataSet(long positionOfDataset) throws IOException {
        setPositionToDataSet(positionOfDataset);

        byte buffer[] = new byte[(int) getDataSetSize()];
        randomAccessFile.readFully(buffer);
        return buffer;
    }

    private byte[] readDataSet() throws IOException {
        byte buffer[] = new byte[(int) getDataSetSize()];
        randomAccessFile.readFully(buffer);
        return buffer;
    }

    private Double readSignalValue(Signal signal) throws IOException {
        byte buffer[] = readDataSet();
        byte signalValue[] = new byte[DOUBLE_LENGTH];
        for (int i = 0; i < DOUBLE_LENGTH; i++) {
            signalValue[i] = buffer[(getSignalOrder(signal) * DOUBLE_LENGTH) + i];
        }

        if (signal.getDataType().equals(DataType.TIME)) {
            return Double.valueOf(ByteBuffer.wrap(signalValue).order(ByteOrder.LITTLE_ENDIAN).getLong());
        } else if (signal.getDataType().equals(DataType.DOUBLE)) {
            return ByteBuffer.wrap(signalValue).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        }

        return null;
    }

    private int getSignalOrder(Signal signal) {
        return signals.indexOf(signal);
    }

    //

}
