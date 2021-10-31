package com.ihaveawebsitetk.attendanceapp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.IOException;

import utils.QRReader;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // TODO: Remove QR Scanning testing code and asset.
        try {
            String decodedText = QRReader.decodeQRCode(BitmapFactory.decodeResource(appContext.getResources(), R.drawable.qrtest));
            if (decodedText == null) {
                System.out.println("No QR Code found in the image");
            } else {
                System.out.println("Decoded text = " + decodedText);
            }

            assertEquals("HELLO", decodedText);
        } catch (IOException e) {
            System.out.println("Could not decode QR Code, " + e.getMessage());
        }


    }
}