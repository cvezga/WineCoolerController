package com.cvezga.winecoolercontroller.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.stream.Stream;

public class TemperatureReader {

    public static void main(String[] args) throws Exception {
        Path sensor = findSensor();

        while (true) {
            try {
                String data = Files.readString(sensor);
                String[] lines = data.split("\\R");

                if (lines.length >= 2 && lines[0].endsWith("YES")) {
                    int index = lines[1].lastIndexOf("t=");
                    if (index >= 0) {
                        double celsius = Long.parseLong(
                                lines[1].substring(index + 2).trim()
                        ) / 1000.0;

                        System.out.printf(
                                "Temperature: %.2f °C (%.2f °F)%n",
                                celsius, celsius * 9 / 5 + 32
                        );
                    }
                } else {
                    System.err.println("Sensor reading failed CRC validation");
                }
            } catch (IOException e) {
                System.err.println("Unable to read temperature: " + e.getMessage());
            }

            Thread.sleep(5000);
        }
    }

    private static Path findSensor() throws IOException {
        try (Stream<Path> paths = Files.list(
                Path.of("/sys/bus/w1/devices"))) {
            return paths
                    .filter(p -> p.getFileName().toString().startsWith("28-"))
                    .findFirst()
                    .map(p -> p.resolve("w1_slave"))
                    .orElseThrow(() ->
                            new IOException("No DS18B20 sensor detected"));
        }
    }
}