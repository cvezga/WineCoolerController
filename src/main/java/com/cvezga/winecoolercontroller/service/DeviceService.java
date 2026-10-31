package com.cvezga.winecoolercontroller.service;

import com.cvezga.winecoolercontroller.dto.Device;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;

public class DeviceService {

    private Map<String, Device> deviceMap;

    public DeviceService() {
        this.deviceMap = new HashMap<>();
        this.deviceMap.put("UpperArea", new Device());
        this.deviceMap.put("LowerArea", new Device());
    }

    public Device getDevice(String deviceId) {
        return deviceMap.get(deviceId);
    }

    @Scheduled(fixedRate = 250)
    private void refreshDeviceStates() {
        for (Device device : deviceMap.values()) {
            checkTemperature(device);
            updateLightState(device);
            updateDeviceState(device);
            updatePeltierState(device);
        }
    }

    private void updateLightState(Device device) {
    }

    private void updateDeviceState(Device device) {
    }

    private void updatePeltierState(Device device) {
    }

    private void checkTemperature(Device device) {
        if (device.getDeviceStatus() == Device.Status.ON) {
            float temperature = device.getTemperature();
            if (temperature >= device.getMaxTemperature()) {
                device.setPeltierStatus(Device.Status.ON);
            } else if (temperature <= device.getMinTemperature()) {
                device.setPeltierStatus(Device.Status.OFF);
            }
        }
    }
}
