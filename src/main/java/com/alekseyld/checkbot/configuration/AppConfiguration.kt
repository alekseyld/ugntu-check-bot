package com.alekseyld.checkbot.configuration

import com.alekseyld.checkbot.utils.ReceiptToPdfProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfiguration {

    @Bean
    fun provideReceiptToPdfProcessor() : ReceiptToPdfProcessor {
        return ReceiptToPdfProcessor()
    }
}