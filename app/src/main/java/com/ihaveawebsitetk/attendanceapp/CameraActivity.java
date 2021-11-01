package com.ihaveawebsitetk.attendanceapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Size;

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
import androidx.fragment.app.FragmentTransaction;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import utils.AttendanceManager;
import utils.CSVReader;
import utils.QRReader;

public class CameraActivity extends AppCompatActivity {
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

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
            boolean bitmapAvailable = false;
            // Sleep until the camera turns on.
            do {
                try {
                    bitmapAvailable = previewView.getBitmap() != null;
                } catch (IllegalArgumentException e) { ; } // Do nothing.
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // Do nothing.
                }
            } while (!bitmapAvailable);

            // Scan until the camera's off, and wait 10ms between scans.
            int successfulScanNumber = 0;
            do {
                System.out.println("Attempting to scan.");
                try {
                    Bitmap b = previewView.getBitmap();
                    String output = QRReader.decodeQRCode(b);

                    // Code to run if a QR code is scanned:
                    if (output != null && !output.equals(" ")) {
                        System.out.printf("Scanned text: %s, Scan number: %s%n", output, successfulScanNumber);

                        int alreadyScanned;
                        try {
                            // Mark the student as attending in the AttendanceManager.
                            try {
                                alreadyScanned = MainActivity.manager.markAttending(output);
                            } catch (NumberFormatException e) {
                                // TODO: Write code to tell the user that the code scanned was not a valid StudentID.
                                alreadyScanned = 1;
                            }
                        } catch (NullPointerException e) {
                            // If the manager is null, create a blank CSV and use that to set it.
                            alreadyScanned = 2;
                        }

                        // If the student wasn't already scanned, create a card and show it on screen.
                        if (alreadyScanned == 0) {
                            // Create the scanning card:
                            ScanningCard c = new ScanningCard(output);

                            // Place the scanning card onto the UI.
                            FragmentTransaction fm3t = getSupportFragmentManager().beginTransaction();
                            fm3t.add(R.id.scanningCardScrollView, c, "Scanning_Fragment_" + successfulScanNumber);
                            fm3t.commit();
                            successfulScanNumber++;
                        }

                        // If the student wasn't found, add them to the manager's list and mark them as scanned.
                        if (alreadyScanned == 2) {
                                // Code to run if this is the first student scanned, and there's no manager loaded.
                            if (MainActivity.manager == null) {
                                String blankCSV = "\"Year\",\"Organization\",\"Period Begin\",\"Student Name\",\"Sis Number\",\"Section ID\",\"Grade\",\"Teacher Name\"\n"
                                                + "\"\", \"\", \"0\",\"" + output + "\", \"" + output +"\", \"\", \"\", \"\"";
                                MainActivity.manager = new AttendanceManager(new CSVReader.CVS(blankCSV));
                            }

                            // If this is a new Student, but it's not the first, create it and add it to the list.
                            AttendanceManager.Student newStudent = new AttendanceManager.Student(output);

                            // Set values so that the app doesn't crash.
                            newStudent.studentName = "Scanned Student #" + output;
                            newStudent.periodNumber = 0;
                            newStudent.setManager(MainActivity.manager);

                            MainActivity.manager.addStudent(newStudent);
                            MainActivity.manager.markAttending(output);

                            // Do the same stuff as in a normal scan.
                            ScanningCard c = new ScanningCard(output);
                            FragmentTransaction fm3t = getSupportFragmentManager().beginTransaction();
                            fm3t.add(R.id.scanningCardScrollView, c, "Scanning_Fragment_" + successfulScanNumber);
                            fm3t.commit();
                            successfulScanNumber++;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // Do nothing.
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
