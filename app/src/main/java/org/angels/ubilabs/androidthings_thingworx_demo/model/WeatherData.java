package org.angels.ubilabs.androidthings_thingworx_demo.model;


import java.util.ArrayList;
import java.util.List;

public class WeatherData {

    private int maxNumber;
    private List<Integer> temperatures;
    private List<Integer> humidities;

    public WeatherData(int maxNumber) {
        this.maxNumber = maxNumber;
        temperatures = new ArrayList<>();
        humidities = new ArrayList<>();
    }

    public List<Integer> getTemperatures() {
        return temperatures;
    }

    public List<Integer> getHumidities() {
        return humidities;
    }

    public void addTemperature(int temperature) {
        if (temperatures.size() == maxNumber) {
            temperatures.remove(0);
        }
        temperatures.add(temperature);
    }

    public void addHumidity(int humidity) {
        if (humidities.size() == maxNumber) {
            humidities.remove(0);
        }
        humidities.add(humidity);
    }

    public int getCurrentTemperature() {
        return temperatures.get(temperatures.size() - 1);
    }

    public int getCurrentHumidity() {
        return humidities.get(humidities.size() - 1);
    }
}
