package com.alekseyld.checkbot.model

data class QrCodeData(
    // ФН
    val fn: String,
    // Дата и время
    val t: String,
    // Итог
    val s: String,
    // ФД (Фискальный документ)
    val i: String,
    // ФП
    val fp: String,
    val n: String,
    val source: String,
) {

    companion object {
        fun fromQrString(data: String): QrCodeData {
            val values = data.split("&").associate { item ->
                item.split("=").let { it.first() to it.last() }
            }

            return QrCodeData(
                fn = values.getOrDefault("fn", ""),
                t = values.getOrDefault("t", ""),
                s = values.getOrDefault("s", ""),
                i = values.getOrDefault("i", ""),
                fp = values.getOrDefault("fp", ""),
                n = values.getOrDefault("n", ""),
                source = data
            )
        }
    }
}