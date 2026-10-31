package com.cvezga.winecoolercontroller.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class W1Service {


    @Value(value = "${w1.path:/sys/bus/w1/devices}")
    private String w1Path;

    @Value(value = "${w1.device.prefix:28-}")
    private String w1PathPrefix;

    @Value(value = "${w1.filename:w1_slave}")
    private String w1File;


    public Map<String, String> readTemperature() throws IOException {
        Map<String, String> tempMap = new HashMap<>();

        try (var paths = Files.list(Path.of(w1Path))) {

            paths.filter(p -> p.getFileName().toString().startsWith(w1PathPrefix))
                    .forEach(sensor -> {
                        log.info("Found sensor: {}", sensor.getFileName());
                        Path temperatureFile = sensor.resolve(w1File);
                        try {
                            String reading = Files.readString(temperatureFile);
                            tempMap.put(sensor.getFileName().toString(), reading);
                        } catch (IOException e) {
                            log.error("Error reading temperature file: {}", sensor.getFileName(), e);
                            throw new RuntimeException(e);
                        }
                    });
        }

        return tempMap;
    }

}

