package com.hevi.binatron;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ForkJoinPool;

@Component
public class BinanceListener {

    final
    Broker broker;

    final
    BinanceApiWebSocketClient binanceApiWebSocketClient;

    final
    ApplicationState applicationState;

    final BinanceApiRestClient binanceApiRestClient;

    public BinanceListener(Broker broker, BinanceApiWebSocketClient binanceApiWebSocketClient, ApplicationState applicationState, BinanceApiRestClient binanceApiRestClient) {
        this.broker = broker;
        this.binanceApiWebSocketClient = binanceApiWebSocketClient;
        this.applicationState = applicationState;
        this.binanceApiRestClient = binanceApiRestClient;
    }

    @PostConstruct
    void init() {
        start();
    }


    void start() {
        ForkJoinPool.commonPool().execute(() -> {
            while (true) {
                try {
                    broker.process(binanceApiRestClient.getPrice("BNBUSDT").getPrice());
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
/*        binanceApiWebSocketClient.onAggTradeEvent("bnbusdt".toLowerCase(), new BinanceApiCallback<AggTradeEvent>() {
            @Override
            public void onResponse(final AggTradeEvent response) {
                if (applicationState.isRunning()) {
                    broker.process(response.getPrice());
                }
            }

            @Override
            public void onFailure(final Throwable cause) {
                System.err.println("Web socket failed");
                cause.printStackTrace(System.err);
            }
        });*/

    }
}
