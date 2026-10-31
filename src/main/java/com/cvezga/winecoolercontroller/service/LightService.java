package com.cvezga.winecoolercontroller.service;

import com.cvezga.winecoolercontroller.dto.Device;

public class LightService {

    private final DeviceService deviceService;

    public LightService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    public void turnLightOn(String deviceId) {
        Device device = deviceService.getDevice(deviceId);
        device.setPeltierStatus(Device.Status.ON);
    }

    public void turnLightOff(String deviceId) {
        Device device = deviceService.getDevice(deviceId);
        device.setPeltierStatus(Device.Status.OFF);
    }

}
