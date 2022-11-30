package com.alekseyld.checkbot.service

import com.alekseyld.checkbot.model.QrCodeData
import com.alekseyld.checkbot.repository.FnsTicketRepository
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.Reader
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.multi.qrcode.QRCodeMultiReader
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.PhotoSize
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.io.File
import java.util.*
import javax.imageio.ImageIO

@Service
class BotService(
    private val fnsTicketRepository: FnsTicketRepository
) {

    fun onUpdateReceived(sender: DefaultAbsSender, update: Update): BotApiMethod<out BotApiObject?>? {

        if (update.hasMessage() && update.message.hasPhoto()) {
            return processQrCodeMessage(sender, update)
        } else if (update.message?.text == "ping") {
            return update.sendMessageBack("pong")
        }

        return null
    }

    private fun processQrCodeMessage(sender: DefaultAbsSender, update: Update): BotApiMethod<out BotApiObject?> {
        val photoSize = getPhoto(update)!!
        val filePath = getFilePath(sender, photoSize)!!
        val file = downloadPhotoByFilePath(sender, filePath, photoSize.fileUniqueId)

        val qrCodeData = file?.let { tryDecodeQR(file) }
        file?.delete()

        return when {
            qrCodeData != null -> {

                val ticket = runCatching {
                    val ticketId = fnsTicketRepository.findTicketBy(qrCodeData)
                    fnsTicketRepository.getTicket(ticketId.id)
                }.getOrNull()

                val message = ticket?.toString() ?: qrCodeData.source

                update.sendMessageBack(message)
            }
            else -> update.sendMessageBack("Не получилось декодировать QR код, повторите еще раз")
        }
    }

    private fun Update.sendMessageBack(text: String): SendMessage {
        return SendMessage().also {
            it.chatId = message.chatId.toString()
            it.text = text
        }
    }

    private fun tryDecodeQR(file: File): QrCodeData? {
        val bufferedImage = ImageIO.read(file)
        val source: LuminanceSource = BufferedImageLuminanceSource(bufferedImage)
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        val reader: Reader = QRCodeMultiReader()

        val hints = HashMap<DecodeHintType, Any?>()

        hints[DecodeHintType.TRY_HARDER] = true
        hints[DecodeHintType.ALSO_INVERTED] = true

        val decodeResult = runCatching {
            reader.decode(bitmap, hints)
        }.getOrNull()

        return decodeResult?.let { QrCodeData.fromQrString(it.text) }
    }

    private fun getPhoto(update: Update): PhotoSize? {
        // Check that the update contains a message and the message has a photo
        if (update.hasMessage() && update.message.hasPhoto()) {
            // When receiving a photo, you usually get different sizes of it
            val photos = update.message.photo

            // We fetch the bigger photo
            return photos.stream()
                .max(Comparator.comparing { obj: PhotoSize? -> obj!!.fileSize }).orElse(null)
        }

        // Return null if not found
        return null
    }

    private fun getFilePath(sender: AbsSender, photo: PhotoSize): String? {
        Objects.requireNonNull(photo)
        if (photo.filePath != null) { // If the file_path is already present, we are done!
            return photo.filePath
        } else { // If not, let find it
            // We create a GetFile method and set the file_id from the photo
            val getFileMethod = GetFile()
            getFileMethod.fileId = photo.fileId
            try {
                // We execute the method using AbsSender::execute method.
                val file = sender.execute(getFileMethod)
                // We now have the file_path
                return file.filePath
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }
        return null // Just in case
    }

    private fun downloadPhotoByFilePath(
        sender: DefaultAbsSender,
        filePath: String,
        fileUniqueId: String
    ): File? {
        try {
            val outputFile = File("cache", fileUniqueId)

            // Download the file calling AbsSender::downloadFile method
            return sender.downloadFile(filePath, outputFile)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
        return null
    }
}