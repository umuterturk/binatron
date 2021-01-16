package com.hevi.binatron.toolbar;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class PitchForkInstance {
    final PitchFork pitchFork;
    final Double now;

    public PitchForkInstance(PitchFork pitchFork) {
        this.pitchFork = pitchFork;
        this.now = now();

    }

    strictfp double now() {
        return Long.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)).doubleValue();
    }

    public double at(Double p) {
        double mid = mid();
        double high = high();
        return mid + p * (high - mid);
    }

    public double high() {
        return pitchFork.at(pitchFork.tradePoint1, now);
    }

    public double mid() {
        return pitchFork.at(pitchFork.tradePoint0, now);

    }

    public double low() {
        return pitchFork.at(pitchFork.tradePoint2, now);

    }

    @Override
    public String toString() {
        return "highest=" + high() + " high=" + at(0.25) + " mid=" + mid() + " low=" + at(-0.25) + " lowest=" + low();
    }
}
