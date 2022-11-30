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
//    val code: Int,
//    val fiscalDocumentFormatVer: Int,
    val creditSum: Int,
    val fiscalDocumentNumber: Int,
    val fiscalDriveNumber: String,
    val fiscalSign: String,
    val fnsUrl: String,
    val items : List<ReceiptItem>,
    val buyerPhoneOrAddress: String,
    val kktRegId : String,
    val machineNumber: String,
    val nds0: Int,
    val ndsNo: Int,
    val operationType: Int,
    val prepaidSum: Int,
    val provisionSum: Int,
    val requestNumber: Int,
    val retailPlace: String,
    val retailPlaceAddress: String,
    val shiftNumber: Int,
    val taxationType: Int,
    val appliedTaxationType: Int,
    val totalSum: Int,
    // Продавец
    val user: String,
    val userInn: String,
)
data class ReceiptItem(
    val name: String,
    val price: Int,
    val quantity: Int,
    val sum: Int,
)