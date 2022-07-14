package it.unipi.sam.volleyballmovementtracker.util;

import java.io.Serializable;

public class SensorData implements Serializable {
    private int sensorData;

    public SensorData(int sensorData) {
        this.sensorData = sensorData;
    }

    public int getSensorData() {
        return sensorData;
    }
    public void setSensorData(int sensorData) {
        this.sensorData = sensorData;
    }
}
