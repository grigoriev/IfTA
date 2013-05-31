package com.ifta.candidate.test.grigoriev;

import com.ifta.candidate.test.interfaces.DataType;
import com.ifta.candidate.test.interfaces.Signal;

/**
 * Author: Sergey Grigoriev
 * Created: 27.03.13 17:06
 */
public class BinarySignal implements Signal {
    private String name;
    private String unit;
    private DataType dataType;

    public BinarySignal(String name, String unit, DataType dataType) {
        this.name = name;
        this.unit = unit;
        this.dataType = dataType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }
}
