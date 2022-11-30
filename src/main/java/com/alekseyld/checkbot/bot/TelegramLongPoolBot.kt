package com.alekseyld.checkbot.bot;

import com.alekseyld.checkbot.properties.BotProperties;
import com.alekseyld.checkbot.service.BotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class TelegramLongPoolBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;

    private final BotService service;

    public TelegramLongPoolBot(
            BotProperties botProperties,
            BotService botService
    ) {
        this.botProperties = botProperties;
        this.service = botService;
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug(update.toString());

        var method = service.onUpdateReceived(this, update);
        if (method != null) {
            try {
                execute(method);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
