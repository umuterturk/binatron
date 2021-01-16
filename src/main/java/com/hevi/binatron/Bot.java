package com.hevi.binatron;

import com.hevi.binatron.configuration.TelegramConfigurationProperties;
import com.hevi.binatron.event.*;
import com.hevi.binatron.toolbar.PitchForkInstance;
import com.hevi.binatron.toolbar.TradePoint;
import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@EnableConfigurationProperties({TelegramConfigurationProperties.class})
public class Bot implements UpdatesListener, ExceptionHandler {

    final TelegramBot bot;

    final TelegramConfigurationProperties telegramConfigurationProperties;

    Long chatId;

    List<TradePoint> tradePoints = null;

    enum State {
        NONE,
        STARTED_BUILDING_PITCHFORK,
        ENDED_BUILDING_PITCHFORK,

    }

    State state = State.NONE;


    @Autowired
    PitchForkManager pitchForkManager;

    public Bot(TelegramBot telegramBot, ApplicationEventMulticaster simpleApplicationEventMulticaster, TelegramConfigurationProperties telegramConfigurationProperties) {
        this.simpleApplicationEventMulticaster = simpleApplicationEventMulticaster;
        telegramBot.setUpdatesListener(this, this);
        bot = telegramBot;
        this.telegramConfigurationProperties = telegramConfigurationProperties;
        this.chatId = telegramConfigurationProperties.getChatId();
    }

    final ApplicationEventMulticaster simpleApplicationEventMulticaster;

    @PostConstruct
    public void hello() {
        sendMessage("Hello I am alive again!");
    }

    @EventListener
    public void handleTradeOrderEvent(TradeOrderEvent tradeOrderEvent) {
        sendMessage(tradeOrderEvent.toString());
    }

    @EventListener
    public void handleOrderCompletedEvent(OrderCompletedEvent orderCompletedEvent) {
        sendMessage(orderCompletedEvent.toString());
    }

    @EventListener
    public void handleTradeOrderEvent(AccountInfoEvent accountInfoEvent) {
        sendMessage(accountInfoEvent.toString());
    }


    public void sendMessage(String message) {
        if (chatId == null) {
            System.err.println("CHAT IS NOT INITIATED");
        }
        System.out.println(bot.execute(new SendMessage(chatId, message)));
    }

    String readMessage(String message) {
        message = message.trim().strip();
        if("pf?".equals(message)){
            final PitchForkInstance pitchForkInstance = pitchForkManager.getPitchForkInstance();
            if(pitchForkInstance == null) return "There is no pitchfork set, type 'pf' to set one.";
            return pitchForkInstance.toString();
        }
        else if (message.startsWith("? ")) {
            final Asset asset = new Asset(message.split(" ")[1]);
            assetInfoRequest(asset);
            return "let me check 24h  stats for " + asset;
        } else if (StringUtils.startsWithIgnoreCase(message, "balance")) {
            final Asset asset = new Asset(message.split(" ")[1]);
            simpleApplicationEventMulticaster.multicastEvent(new BalanceRequestEvent(this, asset));
            return "let me check your balance for " + asset;
        } else if ("start".equalsIgnoreCase(message)) {
            startApplication();
        } else if ("stop".equalsIgnoreCase(message)) {
            stopApplication();
        } else if ("q".equalsIgnoreCase(message)) {
            if (state == State.ENDED_BUILDING_PITCHFORK) {
                return "quit building your pitchfork";
            }
            state = State.NONE;
            tradePoints = null;

        } else if ("pf".equalsIgnoreCase(message)) {
            tradePoints = new ArrayList<>();
            state = State.STARTED_BUILDING_PITCHFORK;
            return "now you need to set 3 points, one at a time something like '2007-12-03T10:15:30 33.95'";
        } else {
            if (state == State.STARTED_BUILDING_PITCHFORK) {
                if ("end".equalsIgnoreCase(message)) {
                    if (tradePoints.size() != 3) return "you need exactly 3 trade points";
                    setPitchFork();
                    state = State.NONE;
                    tradePoints = null;
                    return "trying to set new trade points, I will disable trades, you need to 'start' if you want to get it done!";
                } else {

                    if (tradePoints.size() == 3) {
                        return "you can enter exactly 3 trade points for a pitch fork, you can 'end' to confirm current points or 'q' to cancel";
                    }
                    String[] s = message.split(" ");
                    if (s.length != 2) {
                        return "you must have a space only between TIME and PRICE";
                    }
                    try {
                        TradePoint tp = new TradePoint(s[0], s[1]);
                        tradePoints.add(tp);
                        return tradePoints.size() < 3 ? "OK got it, waiting for the next" : "OK you can 'end' now";
                    } catch (DateTimeParseException dateTimeParseException) {
                        return "your date must be something like 2007-12-03T10:15:30 but was " + s[0];
                    } catch (NumberFormatException numberFormatException) {
                        return "your price must be something like 29.91 but was " + s[1];
                    }
                }
            }
        }
        return null;
    }

    private void assetInfoRequest(Asset asset) {
        simpleApplicationEventMulticaster.multicastEvent(new AssetInfoRequestEvent(this, asset));
    }

    private void stopApplication() {
        simpleApplicationEventMulticaster.multicastEvent(new TradeLifecycleEvent(this, TradeLifecycleEvent.LifeCycleType.STOP));
    }

    private void startApplication() {
        simpleApplicationEventMulticaster.multicastEvent(new TradeLifecycleEvent(this, TradeLifecycleEvent.LifeCycleType.START));
    }

    private void setPitchFork() {
        simpleApplicationEventMulticaster.multicastEvent(new PitchForkEvent(this, new ArrayList<>(tradePoints)));
    }

    @Override
    public int process(List<Update> updates) {
        // ... process updates
        // return id of last processed update or confirm them all
        if (this.chatId == null) {
            this.chatId = updates.get(0).message().chat().id();
        }
        for (Update update : updates) {
            if (update == null || update.message() == null || update.message().text() == null) continue;
            final String response = readMessage(update.message().text());
            if (response != null) {
                sendMessage(response);
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Override
    public void onException(TelegramException e) {
        e.printStackTrace(System.err);
    }


    @EventListener
    public void handleTradeLifecycleEvent(TradeLifecycleEvent lifecycleEvent) {
        sendMessage("Application state is now " + lifecycleEvent.getLifeCycleType().name());
    }

    @EventListener
    public void handleInformativeMessageEvent(AssetInfoResponseEvent assetInfoResponseEvent) {
        sendMessage(assetInfoResponseEvent.toString());
    }

    @EventListener
    public void handleInformativeMessageEvent(InformativeMessageEvent informativeMessageEvent) {
        sendMessage(informativeMessageEvent.getMessage());
    }
}
