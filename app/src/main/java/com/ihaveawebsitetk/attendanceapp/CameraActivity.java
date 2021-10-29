package com.ihaveawebsitetk.attendanceapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import utils.QRReader;

public class CameraActivity extends AppCompatActivity {
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        textView = findViewById(R.id.dataOut);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindImageAnalysis(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        Thread t = new Thread(() -> {
            // Sleep until the camera turns on.
            do {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    textView.setText(e.getMessage());
                }
            } while (previewView.getBitmap() == null);

            // Scan until the camera's off.
            do {
                System.out.println("Attempting to scan.");
                try {
                    Bitmap b = previewView.getBitmap();
                    String output = QRReader.decodeQRCode(b);
                    runOnUiThread(() -> {
                        if (output != null) textView.setText(output);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    textView.setText(e.getMessage());
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    textView.setText(e.getMessage());
                }

                // Stop scanning if the view has transitioned back to MainActivity.
            } while (previewView.getBitmap() != null);
        });
        t.start();

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), ImageProxy::close);

        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.createSurfaceProvider());
        cameraProvider.bindToLifecycle(this, cameraSelector,
                imageAnalysis, preview);
    }
}
