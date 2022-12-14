package com.alekseyld.checkbot.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "fns")
data class FnsProperties(
    val sessionId: String
)
