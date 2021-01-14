package com.hevi.binatron.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:apikeys.properties")
@ConfigurationProperties(prefix = "binance")
@ConstructorBinding
public class BinanceConfigurationProperties {
    private String secret;
    private String apiKey;

    public BinanceConfigurationProperties(String secret, String apiKey) {
        this.secret = secret;
        this.apiKey = apiKey;
    }

    public String getSecret() {
        return secret;
    }

    public String getApiKey() {
        return apiKey;
    }
}
