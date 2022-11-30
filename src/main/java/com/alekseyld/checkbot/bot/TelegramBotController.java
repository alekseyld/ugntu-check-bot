package com.alekseyld.checkbot.bot;

import com.alekseyld.checkbot.properties.BotProperties;
import com.alekseyld.checkbot.service.BotService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

//@Component
@Slf4j
public class TelegramBotController extends TelegramWebhookBot {

    private final BotProperties botProperties;

    private final BotService service;

    public TelegramBotController(
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
    public String getBotPath() {
        return botProperties.getUsername();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        log.debug(update.toString());
        return service.onUpdateReceived(this, update);
    }

}
