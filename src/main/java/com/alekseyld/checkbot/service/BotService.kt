package com.alekseyld.checkbot.service;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class BotService {

    public BotApiMethod<?> onUpdateReceived(DefaultAbsSender sender, Update update) {

        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            PhotoSize photoSize = getPhoto(update);
            String filePath = getFilePath(sender, photoSize);
            java.io.File file = downloadPhotoByFilePath(sender, filePath, photoSize.getFileUniqueId());

            try {
                BufferedImage bufferedImage = ImageIO.read(file);

                LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                Reader reader = new QRCodeMultiReader();

                Result decodeResult = reader.decode(bitmap);

                System.out.println("qr = ");
                System.out.println(decodeResult);
                System.out.println(decodeResult.getText());

                SendMessage response = new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(decodeResult.getText());
                return response;

            } catch (Exception e) {
                e.printStackTrace();

                SendMessage response = new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText("Не получилось декодировать QR код, повторите еще раз");

                return response;
            } finally {
//                file.delete();
            }

        } else if (update.hasMessage() &&
                update.getMessage().hasText() &&
                update.getMessage().getText().equals("ping")) {

            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText("pong");

            return response;
        }

        return null;
    }


    @Nullable
    public PhotoSize getPhoto(@NotNull Update update) {
        // Check that the update contains a message and the message has a photo
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            // When receiving a photo, you usually get different sizes of it
            List<PhotoSize> photos = update.getMessage().getPhoto();

            // We fetch the bigger photo
            return photos.stream()
                    .max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null);
        }

        // Return null if not found
        return null;
    }

    @Nullable
    public String getFilePath(@NotNull AbsSender sender, @NotNull PhotoSize photo) {
        Objects.requireNonNull(photo);

        if (photo.getFilePath() != null) { // If the file_path is already present, we are done!
            return photo.getFilePath();
        } else { // If not, let find it
            // We create a GetFile method and set the file_id from the photo
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(photo.getFileId());
            try {
                // We execute the method using AbsSender::execute method.
                File file = sender.execute(getFileMethod);
                // We now have the file_path
                return file.getFilePath();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        return null; // Just in case
    }

    @Nullable
    public java.io.File downloadPhotoByFilePath(
            @NotNull DefaultAbsSender sender,
            @NotNull String filePath,
            @NotNull String fileUniqueId
    ) {
        try {
            java.io.File outputFile = new java.io.File("cache", fileUniqueId);

            // Download the file calling AbsSender::downloadFile method
            return sender.downloadFile(filePath, outputFile);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        return null;
    }

}
