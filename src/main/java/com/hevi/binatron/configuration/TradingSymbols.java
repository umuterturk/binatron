package com.hevi.binatron.configuration;

import com.hevi.binatron.Asset;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "trading", ignoreInvalidFields = true)
public class TradingSymbols {
    private Asset from;
    private Asset to;

    private String compound = null;

    public void setFrom(Asset from) {
        this.from = from;
        compound = null;
    }

    public void setTo(Asset to) {
        this.to = to;
        compound = null;
    }

    public Asset from() {
        return from;
    }

    public Asset to() {
        return to;
    }

    public String compound() {
        if (compound == null) {
            compound = from().name() + to().name();
        }
        return compound;
    }
}
