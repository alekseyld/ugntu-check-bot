package com.alekseyld.checkbot.bot;

import com.alekseyld.checkbot.configuration.properties.BotProperties;
import com.alekseyld.checkbot.service.BotService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
        var method = service.onUpdateReceived(this, update);

        if (method instanceof BotApiMethod) {
            return (BotApiMethod<?>) method;
        } else if (method instanceof SendDocument) {
            try {
                execute((SendDocument) method);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

}
