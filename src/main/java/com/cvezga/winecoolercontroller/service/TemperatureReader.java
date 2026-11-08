package com.cvezga.winecoolercontroller.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
public class TemperatureReader {

    @Value("${temp.upper.sensor.id:28-000000232521}")
    private String upperSensorId;
    @Value("${temp.upper.sensor.offset:0}")
    private double upperSensorOffset;
    @Value("${temp.lower.sensor.id:28-000000227c74}")
    private String lowerSensorId;
    @Value("${temp.lower.sensor.offset:0.625}")
    private double lowerSensorOffset;

    private List<Path> sensors;

    private Map<String, Sensor> temperatureMap;

    public TemperatureReader() {
        try {
            sensors = findSensors();
        } catch (IOException e) {
            sensors = null;
            log.warn("No Sensor device reading detected");
        }
    }

    private Map<String, Sensor> initTemperatureMap() {
        Map<String, Sensor> temperatureMap = new HashMap<>();

        //upper sensor
        temperatureMap.put(upperSensorId, new Sensor(upperSensorId, upperSensorOffset, 0));
        //lower sensor
        temperatureMap.put(lowerSensorId, new Sensor(lowerSensorId, lowerSensorOffset, 0));

        return temperatureMap;
    }

    @Scheduled(fixedRate = 10_000)
    public void readTemperature() throws Exception {

        if (temperatureMap == null) {
            temperatureMap = initTemperatureMap();
        }

        if (sensors != null && !sensors.isEmpty()) {
            for (Path sensor : sensors) {
                try {
                    String data = Files.readString(sensor);
                    String[] lines = data.split("\\R");

                    if (lines.length >= 2 && lines[0].endsWith("YES")) {
                        int index = lines[1].lastIndexOf("t=");
                        if (index >= 0) {
                            double celsius = Long.parseLong(
                                    lines[1].substring(index + 2).trim()
                            ) / 1000.0;

                            String sensorId = sensor.toString().split("/")[5].trim();
                            log.info("Temperature path: {} sensor_id:{} -> {} °C ({} °F)", sensor.toAbsolutePath(), sensorId, celsius, celsius * 9 / 5 + 32);
                            Sensor sensorObj = temperatureMap.get(sensorId);
                            //System.out.println(temperatureMap.keySet());
                            if (sensorObj != null) {
                                sensorObj.temperature = celsius;
                                log.info("Temperature {}: {} °C ({} °F)", sensorId, sensorObj.getTemperature(), sensorObj.getTemperature() * 9 / 5 + 32);
                            } else {
                                log.error("Sensor Id {} not found int sensor temperature map.", sensorId);
                            }
                        }
                    } else {
                        System.err.println("Sensor reading failed CRC validation");
                    }
                } catch (IOException e) {
                    System.err.println("Unable to read temperature: " + e.getMessage());
                }
            }

        } else {
            log.warn("No Sensor device reading detected");
        }

        double temp1 = getRawTemperature("upper");
        double temp2 = getRawTemperature("lower");
        double tempAdj1 = getTemperature("upper");
        double tempAdj2 = getTemperature("lower");
        log.info("Temp Diff Raw:{} , Adjusted:{}", (temp1 - temp2), (tempAdj1 - tempAdj2));
    }

    private List<Path> findSensors() throws IOException {
        try (Stream<Path> paths = Files.list(
                Path.of("/sys/bus/w1/devices"))) {
            return paths.filter(p -> p.getFileName().toString().startsWith("28-"))
                    .map(p -> p.resolve("w1_slave")).toList();
        }
    }

    public Double getTemperature(String sensorLocation) {
        String sensorId = sensorLocation.equals("upper") ? upperSensorId : lowerSensorId;
        return temperatureMap.get(sensorId).getTemperature();
    }

    public Double getRawTemperature(String sensorLocation) {
        String sensorId = sensorLocation.equals("upper") ? upperSensorId : lowerSensorId;
        return temperatureMap.get(sensorId).temperature;
    }

    private static class Sensor {
        private final String sensorId;
        private final double offset;
        private double temperature;

        public Sensor(String sensorId, double offset, double temperature) {
            this.sensorId = sensorId;
            this.offset = offset;
            this.temperature = temperature;
        }

        double getTemperature() {
            return temperature + offset;
        }
    }
}