package com.hevi.binatron;

import com.hevi.binatron.toolbar.PitchFork;
import com.hevi.binatron.toolbar.TradePoint;
import org.junit.jupiter.api.Test;

class DemoApplicationTests {

    @Test
    void contextLoads() {
        TradePoint tp1 = new TradePoint("2021-01-11T16:00:00", 37.5);
        TradePoint tp2 = new TradePoint("2021-01-12T10:00:00", 40.2);
        TradePoint tp3 = new TradePoint("2021-01-12T17:00:00", 37.0);
        final PitchFork pitchFork = new PitchFork(tp1, tp2, tp3);

    }

}
