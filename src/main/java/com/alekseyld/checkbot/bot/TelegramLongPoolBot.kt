package com.alekseyld.checkbot.bot

import com.alekseyld.checkbot.properties.BotProperties
import com.alekseyld.checkbot.service.BotService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update
import java.io.Serializable

@Component
class TelegramLongPoolBot(
    private val botProperties: BotProperties,
    private val service: BotService
) : TelegramLongPollingBot() {

    override fun getBotUsername() = botProperties.username

    override fun getBotToken() = botProperties.token

    override fun onUpdateReceived(update: Update) {
        log.debug(update.toString())
        service.onUpdateReceived(this, update)?.let { method ->
            execute(method)
        }
    }

    override fun <T : Serializable, Method : BotApiMethod<T>> execute(method: Method): T =
        super.execute(method)

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}