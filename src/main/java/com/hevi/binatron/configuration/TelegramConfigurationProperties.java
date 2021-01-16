package com.hevi.binatron.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:application-local.properties")
@ConfigurationProperties(prefix = "telegram")
@ConstructorBinding
public class TelegramConfigurationProperties {
    private String botToken;
    private Long chatId;

    public TelegramConfigurationProperties(String botToken, Long chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
    }

    public String getBotToken() {
        return botToken;
    }

    public Long getChatId() {
        return chatId;
    }
}
