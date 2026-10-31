package com.cvezga.winecoolercontroller.dto;

import lombok.Data;

@Data
public class Device {
    public enum Status {ON, OFF}

    private String deviceId;
    private String deviceName;
    private float temperature;
    private float minTemperature;
    private float maxTemperature;
    private String deviceType;
    private Status peltierStatus;
    private Status lightStatus;
    private Status deviceStatus;
    private int lightGPIO;
    private int peltierGPIO;
    private int temperatureGPIO;
    
}
