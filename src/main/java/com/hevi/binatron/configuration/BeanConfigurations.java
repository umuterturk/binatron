package com.hevi.binatron.configuration;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.pengrad.telegrambot.TelegramBot;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({BinanceConfigurationProperties.class, TelegramConfigurationProperties.class})
public class BeanConfigurations {


    final
    TelegramConfigurationProperties telegramConfigurationProperties;

    final
    BinanceConfigurationProperties binanceConfigurationProperties;

    public BeanConfigurations(BinanceConfigurationProperties binanceConfigurationProperties, TelegramConfigurationProperties telegramConfigurationProperties) {
        this.binanceConfigurationProperties = binanceConfigurationProperties;
        this.telegramConfigurationProperties = telegramConfigurationProperties;
    }

    @Bean
    public BinanceApiWebSocketClient binanceApiWebSocketClient() {
        return BinanceApiClientFactory.newInstance().newWebSocketClient();
    }

    @Bean
    BinanceApiRestClient binanceApiRestClient() {
        return BinanceApiClientFactory.newInstance(
                binanceConfigurationProperties.getApiKey(),
                binanceConfigurationProperties.getSecret()
        ).newRestClient();
    }

    @Bean
    TelegramBot telegramBot() {
        return new TelegramBot(telegramConfigurationProperties.getBotToken());
    }

}
