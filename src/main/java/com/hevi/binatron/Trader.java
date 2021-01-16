package com.hevi.binatron;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.account.*;
import com.binance.api.client.domain.market.TickerStatistics;
import com.binance.api.client.exception.BinanceApiException;
import com.hevi.binatron.configuration.TradingSymbols;
import com.hevi.binatron.event.*;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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

    final
    TradingSymbols tradingSymbols;

    Account account;

    final BinanceApiRestClient binanceApiRestClient;

    final BinanceApiWebSocketClient binanceApiWebSocketClient;

    final ApplicationEventMulticaster simpleApplicationEventMulticaster;


    public Trader(BinanceApiWebSocketClient binanceApiWebSocketClient, ApplicationEventMulticaster simpleApplicationEventMulticaster, BinanceApiRestClient binanceApiRestClient, TradingSymbols tradingSymbols) {
        this.binanceApiWebSocketClient = binanceApiWebSocketClient;
        this.simpleApplicationEventMulticaster = simpleApplicationEventMulticaster;
        this.binanceApiRestClient = binanceApiRestClient;
        this.tradingSymbols = tradingSymbols;
    }


    @PostConstruct
    void init() {
        updateAccount();
        System.out.println(account.getAssetBalance(tradingSymbols.from().name()));
        multicastAccountInfo(tradingSymbols.from());
        multicastAccountInfo(tradingSymbols.to());

    }

    private void multicastAccountInfo(Asset asset) {
        simpleApplicationEventMulticaster.multicastEvent(new AccountInfoEvent(this, asset, getAssetBalance(asset).getFree()));
    }

    private AssetBalance getAssetBalance(Asset asset) {
        return account.getAssetBalance(asset.name());
    }

    private void updateAccount() {
        account = binanceApiRestClient.getAccount();
    }

    String getJustFree(Asset asset) {
        double aDouble = Double.parseDouble(getAssetBalance(asset).getFree()) * 0.995;
        return String.format("%.2f", aDouble);
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
//        return new OrderResult(0, 0);
        return processResponse(binanceApiRestClient.newOrder(newOrder));
    }

    private void marketBuyAll(TradeOrderEvent tradeOrderEvent) {
        final Asset freeAsset = tradingSymbols.to();
        try {
            final String justFree = getJustFree(freeAsset);
            multicastInformativeMessage("Trying to buy your " + justFree + " " + freeAsset + " price of " + tradeOrderEvent.getPrice());
            OrderResult orderResult = order(NewOrder.marketBuy(tradingSymbols.compound(), justFree));

            OrderCompletedEvent orderCompletedEvent = new OrderCompletedEvent(this, tradingSymbols.compound(), orderResult.quantity.toString(), orderResult.sum.toString(), "BUY");
            simpleApplicationEventMulticaster.multicastEvent(orderCompletedEvent);
            updateAccount();

        } catch (Exception e) {
            multicastInformativeMessage("Couldn't buy: " + e.getMessage());
            e.printStackTrace();
        } finally {
            multicastAccountInfo(freeAsset);
            multicastAccountInfo(tradingSymbols.to());
        }
    }

    private void multicastInformativeMessage(String s) {
        simpleApplicationEventMulticaster.multicastEvent(new InformativeMessageEvent(this, s));
    }

    private void marketSellAll(TradeOrderEvent tradeOrderEvent) {
        final Asset freeAsset = tradingSymbols.from();
        try {
            final String justFree = getJustFree(freeAsset);
            multicastInformativeMessage("Trying to sell your " + justFree + " " + freeAsset + " price of " + tradeOrderEvent.getPrice());
            OrderResult orderResult = order(NewOrder.marketSell(tradingSymbols.compound(), justFree));

            OrderCompletedEvent orderCompletedEvent = new OrderCompletedEvent(this, tradingSymbols.compound(), orderResult.quantity.toString(), orderResult.sum.toString(), "SELL");
            simpleApplicationEventMulticaster.multicastEvent(orderCompletedEvent);
            updateAccount();

        } catch (Exception e) {
            multicastInformativeMessage("Couldn't sell: " + e.getMessage());
            e.printStackTrace();
        } finally {
            multicastAccountInfo(freeAsset);
            multicastAccountInfo(tradingSymbols.from());
        }
    }


    @EventListener
    public void listen(TradeOrderEvent tradeOrderEvent) {
        final TradeOrderEvent.Action action = tradeOrderEvent.getAction();
        if (action == TradeOrderEvent.Action.BOUGHT) {
            marketBuyAll(tradeOrderEvent);
        } else if (action == TradeOrderEvent.Action.SOLD_UPPER || action == TradeOrderEvent.Action.SOLD_STOP_LOSS) {
            marketSellAll(tradeOrderEvent);
        }
    }


    @EventListener
    public void listenBalance(BalanceRequestEvent balanceRequestEvent) {
        final AssetBalance assetBalance = getAssetBalance(balanceRequestEvent.getSymbol());
        simpleApplicationEventMulticaster.multicastEvent(new AccountInfoEvent(this, balanceRequestEvent.getSymbol(), "free=" + assetBalance.getFree() + " locked=" + assetBalance.getLocked()));

    }


    @EventListener
    public void listenAssetInfoRequest(AssetInfoRequestEvent assetInfoRequestEvent) {
        final Asset asset = assetInfoRequestEvent.getAsset();
        final TickerStatistics stats = binanceApiRestClient.get24HrPriceStatistics(asset.name());
        simpleApplicationEventMulticaster.multicastEvent(new AssetInfoResponseEvent(this,
                asset,
                stats.getLastPrice(),
                stats.getVolume(),
                stats.getLowPrice(),
                stats.getHighPrice(),
                stats.getWeightedAvgPrice()
        ));
    }

}
