package com.alekseyld.checkbot.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "bot")
data class BotProperties(
    val token: String,
    val url: String,
    val username : String,
)