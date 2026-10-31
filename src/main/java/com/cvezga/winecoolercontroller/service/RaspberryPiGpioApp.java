package com.cvezga.winecoolercontroller.service;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

import java.util.concurrent.CountDownLatch;

public class RaspberryPiGpioApp {

    private static final int RELAY_GPIO = 17;
    private static final int BUTTON_GPIO = 22;

    public static void main(String[] args) throws Exception {
        var pi4j = Pi4J.newAutoContext();

        try {
            var relayConfig = DigitalOutput.newConfigBuilder(pi4j)
                    .id("relay")
                    .name("Relay output")
                    .bcm(17)
                    .initial(DigitalState.LOW)
                    .shutdown(DigitalState.LOW);

            DigitalOutput relay = pi4j.create(relayConfig);

            var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
                    .id("button")
                    .name("Button input")
                    .bcm(22)
                    .pull(PullResistance.PULL_DOWN)
                    .debounce(20_000L);

            DigitalInput button = pi4j.create(buttonConfig);

            button.addListener(event -> {
                if (event.state() == DigitalState.HIGH) {
                    relay.high();
                    System.out.println("Button pressed - relay ON");
                } else {
                    relay.low();
                    System.out.println("Button released - relay OFF");
                }
            });

            System.out.println("GPIO application running.");

            // Keep the application alive until interrupted.
            new CountDownLatch(1).await();

        } finally {
            pi4j.shutdown();
        }
    }
}