package com.alekseyld.checkbot;

import com.alekseyld.checkbot.properties.BotProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(BotProperties.class)
public class CheckBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheckBotApplication.class, args);
	}
}
