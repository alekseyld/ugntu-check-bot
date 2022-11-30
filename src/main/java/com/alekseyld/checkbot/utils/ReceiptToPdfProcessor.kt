package com.alekseyld.checkbot.utils

import com.alekseyld.checkbot.model.Receipt
import com.itextpdf.text.*
import com.itextpdf.text.List
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ReceiptToPdfProcessor {

    fun process(receipt: Receipt): File {

        val pdfFile = File("cache", getFileName())

        val document = Document(PageSize.A4)
        PdfWriter.getInstance(document, FileOutputStream(pdfFile))
        document.open()

        val fontPath = "font/Roboto-Regular.ttf"
        val baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
        val font = Font(baseFont, 16F, Font.NORMAL)

        document.newPage()

        document.add(Paragraph(receipt.user, font))
        document.add(Paragraph(receipt.userInn, font))
        document.add(Paragraph(receipt.retailPlace, font))
        document.add(Paragraph(receipt.retailPlaceAddress, font))

        document.add(Paragraph(Chunk.NEWLINE))

        document.add(Paragraph("Смена №${receipt.shiftNumber}", font))
        document.add(Paragraph("Чек №${receipt.requestNumber}", font))

        val formattedDate = Calendar.getInstance().apply {
            time = Date(receipt.dateTime * 1000L)
            timeZone = TimeZone.getTimeZone("Europe/Moscow")
        }.let { dateFormatter.format(it.time) }

        document.add(Paragraph(formattedDate, font))

        document.add(Paragraph(Chunk.NEWLINE))

        document.add(Paragraph("Наименование, Кол-во        Сумма", font))

        val itemList = List(true)

        receipt.items.forEach { item ->
            itemList.add(
                ListItem("${item.name}, ${item.quantity}        ${item.sum/100}", font)
            )
        }

        document.add(itemList)

        document.add(Paragraph(Chunk.NEWLINE))
        document.add(Paragraph("ИТОГО: ${receipt.totalSum/100}", font))

        document.add(Paragraph(Chunk.NEWLINE))

        document.add(Paragraph("РН ККТ: ${receipt.kktRegId}", font))
        document.add(Paragraph("№ ФД: ${receipt.fiscalDocumentNumber}", font))
        document.add(Paragraph("№ ФН: ${receipt.fiscalDriveNumber}", font))
        document.add(Paragraph("ФПД: ${receipt.fiscalSign}", font))

        document.close()

        return pdfFile
    }

    private fun getFileName(): String {
        return "${UUID.randomUUID()}.pdf"
    }

    companion object {
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
    }

}