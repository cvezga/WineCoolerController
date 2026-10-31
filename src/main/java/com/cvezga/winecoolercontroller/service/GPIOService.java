package com.cvezga.winecoolercontroller.service;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalState;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.PullResistance;


public class GPIOService {

    public enum GpioState {LOW, HIGH}

    private static final int RELAY_GPIO = 17;

    private final Context context;

    public GPIOService() {
        context = Pi4J.newAutoContext();
    }

    public boolean setGPIO(int gpioNumber, GpioState state) {
        DigitalOutputConfig config = DigitalOutput.newConfigBuilder(context)
                .id("relay")
                .name("Relay Output")
                .address(gpioNumber)  // BCM GPIO 17, physical pin 11
                .initial(DigitalState.LOW)
                .shutdown(DigitalState.LOW)
                .build();

        DigitalOutput digitalOutput = context.create(config);
        if (state == GpioState.HIGH) {
            digitalOutput.high();
        } else if (state == GpioState.LOW) {
            digitalOutput.low();
        }
        return false;
    }


    public static void main(String[] args) throws Exception {

        var pi4j = Pi4J.newAutoContext();

        var config = DigitalOutput.newConfigBuilder(pi4j)
                .id("relay")
                .name("Relay Output")
                .address(17)  // BCM GPIO 17, physical pin 11
                .initial(DigitalState.LOW)
                .shutdown(DigitalState.LOW)
                .build();

        var relay = pi4j.create(config);

        try {
            System.out.println("Relay OFF");
            relay.low();

            Thread.sleep(2000);

            System.out.println("Relay ON");
            relay.high();

            Thread.sleep(3000);

            System.out.println("Relay OFF");
            relay.low();

        } finally {
            pi4j.shutdown();
        }
    }


    public class GpioReadExample {

        public static void main(String[] args) throws Exception {
            var pi4j = Pi4J.newAutoContext();

            var config = DigitalInput.newConfigBuilder(pi4j)
                    .id("gpio-input")
                    .name("GPIO Input")
                    .address(22)
                    .pull(PullResistance.PULL_DOWN)
                    .build();

            var input = pi4j.create(config);

            input.addListener(event -> {
                if (event.state() == DigitalState.HIGH) {
                    System.out.println("Input activated");
                } else {
                    System.out.println("Input deactivated");
                }
            });

            Thread.currentThread().join();

            try {
                while (true) {
                    DigitalState state = input.state();

                    if (state == DigitalState.HIGH) {
                        System.out.println("GPIO is HIGH");
                    } else {
                        System.out.println("GPIO is LOW");
                    }

                    Thread.sleep(500);
                }
            } finally {
                pi4j.shutdown();
            }


        }
    }
}

