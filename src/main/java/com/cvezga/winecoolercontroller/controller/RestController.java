package com.cvezga.winecoolercontroller.controller;

import com.cvezga.winecoolercontroller.service.TemperatureReader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api/v1")
public class RestController {

    private final TemperatureReader temperatureReader;

    public RestController(TemperatureReader temperatureReader) {
        this.temperatureReader = temperatureReader;
    }

    @GetMapping("/temperature")
    public List<Double> temperature() {
        List<Double> list = new ArrayList<>();

        list.add(temperatureReader.getTemperature("upper"));
        list.add(temperatureReader.getTemperature("lower"));

        return list;
    }
}
