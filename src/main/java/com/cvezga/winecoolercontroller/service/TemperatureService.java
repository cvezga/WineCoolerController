package com.cvezga.winecoolercontroller.service;

import com.cvezga.winecoolercontroller.dto.Device;

public class TemperatureService {
    
    private final DeviceService deviceService;

    public TemperatureService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    public void turnTemperatureUp(String deviceId) {
        Device device = deviceService.getDevice(deviceId);
        device.setTemperature(device.getTemperature() + 1);
    }

    public void turnTemperatureDown(String deviceId) {
        Device device = deviceService.getDevice(deviceId);
        device.setTemperature(device.getTemperature() - 1);
    }

    public void turnCoolerOn(String deviceId) {
        Device device = deviceService.getDevice(deviceId);
        device.setPeltierStatus(Device.Status.ON);
    }

    public void turnCoolerOff(String deviceId) {
        Device device = deviceService.getDevice(deviceId);
        device.setPeltierStatus(Device.Status.OFF);
    }

    public float getTemperature(String deviceId) {
        Device device = deviceService.getDevice(deviceId);
        return device.getTemperature();
    }

    public float getMaxTemperature(String deviceId) {
        Device device = deviceService.getDevice(deviceId);
        return device.getMaxTemperature();
    }

    public float getMinTemperature(String deviceId) {
        Device device = deviceService.getDevice(deviceId);
        return device.getMinTemperature();
    }
    
}
