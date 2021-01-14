package com.hevi.binatron;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.account.*;
import com.hevi.binatron.event.AccountInfoEvent;
import com.hevi.binatron.event.BalanceRequestEvent;
import com.hevi.binatron.event.OrderCompletedEvent;
import com.hevi.binatron.event.TradeOrderEvent;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;

@Component
public class Trader {
    class OrderResult {
        Double quantity;
        Double sum;

        public OrderResult(double quantity, double sum) {
            this.quantity = quantity;
            this.sum = sum;
        }
    }

    final String fromSymbol = "BNB";
    final String toSymbol = "USDT";
    final String compoundSymbol = (fromSymbol + toSymbol).toLowerCase(Locale.ENGLISH);


    Account account;

    final BinanceApiRestClient binanceApiRestClient;

    final BinanceApiWebSocketClient binanceApiWebSocketClient;

    final ApplicationEventMulticaster simpleApplicationEventMulticaster;


    public Trader(BinanceApiWebSocketClient binanceApiWebSocketClient, ApplicationEventMulticaster simpleApplicationEventMulticaster, BinanceApiRestClient binanceApiRestClient) {
        this.binanceApiWebSocketClient = binanceApiWebSocketClient;
        this.simpleApplicationEventMulticaster = simpleApplicationEventMulticaster;
        this.binanceApiRestClient = binanceApiRestClient;
    }


    @PostConstruct
    void init() {
        updateAccount();
        System.out.println(account.getAssetBalance(fromSymbol));
        multicastAccountInfo(fromSymbol);
        multicastAccountInfo(toSymbol);

    }

    private void multicastAccountInfo(String symbol) {
        simpleApplicationEventMulticaster.multicastEvent(new AccountInfoEvent(this, symbol, account.getAssetBalance(symbol).getFree()));
    }

    private void updateAccount() {
        account = binanceApiRestClient.getAccount();
    }

    String getJustFree(String symbol) {
        double aDouble = Double.parseDouble(account.getAssetBalance(symbol).getFree()) * 0.995;
        return Double.toString(aDouble);
    }

    private OrderResult processResponse(NewOrderResponse orderResponse) {
        double totalQty = 0.0;
        double totalSum = 0.0;

        for (Trade trade : orderResponse.getFills()) {
            double qty = Double.parseDouble(trade.getQty());
            double price = Double.parseDouble(trade.getPrice());
            totalQty += qty;
            totalSum += price * qty;
        }
        return new OrderResult(totalQty, totalSum);
    }

    private OrderResult order(NewOrder newOrder) {
        return new OrderResult(0, 0);
//        return processResponse(binanceApiRestClient.newOrder(newOrder));
    }

    private void marketBuyAll() {

        OrderResult orderResult = order(NewOrder.marketBuy(compoundSymbol, getJustFree(fromSymbol)));

        OrderCompletedEvent orderCompletedEvent = new OrderCompletedEvent(this, compoundSymbol, orderResult.quantity.toString(), orderResult.sum.toString(), "BUY");
        simpleApplicationEventMulticaster.multicastEvent(orderCompletedEvent);
        updateAccount();
        multicastAccountInfo(fromSymbol);
        multicastAccountInfo(toSymbol);
    }

    private void marketSellAll() {
        OrderResult orderResult = order(NewOrder.marketSell(compoundSymbol, getJustFree(fromSymbol)));

        OrderCompletedEvent orderCompletedEvent = new OrderCompletedEvent(this, compoundSymbol, orderResult.quantity.toString(), orderResult.sum.toString(), "SELL");
        simpleApplicationEventMulticaster.multicastEvent(orderCompletedEvent);
        updateAccount();
        multicastAccountInfo(fromSymbol);
        multicastAccountInfo(toSymbol);
    }


    @EventListener
    public void listen(TradeOrderEvent tradeOrderEvent) {
        final TradeOrderEvent.Action action = tradeOrderEvent.getAction();
        if (action == TradeOrderEvent.Action.BOUGHT) {
            marketBuyAll();
        } else if (action == TradeOrderEvent.Action.SOLD_UPPER || action == TradeOrderEvent.Action.SOLD_STOP_LOSS) {
            marketSellAll();
        }
    }


    @EventListener
    public void listenBalance(BalanceRequestEvent balanceRequestEvent) {
        final AssetBalance assetBalance = account.getAssetBalance(balanceRequestEvent.getSymbol());
        simpleApplicationEventMulticaster.multicastEvent(new AccountInfoEvent(this, balanceRequestEvent.getSymbol().toUpperCase(Locale.ROOT), "free=" + assetBalance.getFree() + " locked=" + assetBalance.getLocked()));

    }

}
