package com.alekseyld.checkbot.model

data class FnsTicketResponse(
    val ticket: Ticket
)

data class Ticket(
    val document: TicketDocument
)

data class TicketDocument(
    val receipt: Receipt
)

data class Receipt(
    val dateTime: Int,
    val ecashTotalSum: Int,
    val cashTotalSum: Int,
    val creditSum: Int,
    val items : List<ReceiptItem>,
    // Продавец
    val user: String
)
data class ReceiptItem(
    val name: String,
    val price: Int,
    val quantity: Int,
    val sum: Int,
)