package utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.io.File;
import java.io.IOException;

public class QRReader {
    public static String decodeQRCode(Bitmap b) throws IOException {
        // Ensure that the input is not null.
        if (b != null) {
            // Convert the inputted bitmap to a BinaryBitmap so that it can be decoded into a String.
            int[] intArray = new int[b.getWidth() * b.getHeight()];
            //copy pixel data from the Bitmap into the 'intArray' array
            b.getPixels(intArray, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
            LuminanceSource source = new RGBLuminanceSource(b.getWidth(), b.getHeight(), intArray);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            // Try to read the image.
            try {
                Result result = new MultiFormatReader().decode(bitmap);
                return result.getText();
            } catch (NotFoundException e) {
                System.out.println("There is no QR code in the image");
                return null;
            }
        } else return null;
    }
}
