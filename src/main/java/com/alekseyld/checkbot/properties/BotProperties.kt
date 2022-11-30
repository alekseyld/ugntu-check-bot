package com.alekseyld.checkbot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bot")
public class BotProperties {

    private String token;
    private String url;
    private String username;

}


