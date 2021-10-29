package com.ihaveawebsitetk.attendanceapp;

import static android.view.View.GONE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.IOException;

import utils.AttendanceManager;
import utils.CSVReader;
import utils.QRReader;
import utils.SettingsManager;

public class MainActivity extends AppCompatActivity {
    static File path;
    static AttendanceManager manager;

    public static File getPath() {
        return path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        path = getApplicationContext().getFilesDir();

        // Load attending icons.
        SettingsManager.ATTENDANCE_ICON_TRUE = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.check_mark);
        SettingsManager.ATTENDANCE_ICON_TRUE = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.x_mark);

        // Set the on-click listener for the "Load CSV" button.
        Button loadCSVButton = findViewById(R.id.loadButton);
        loadCSVButton.setOnClickListener(view -> {
            Thread opener = new Thread(() -> {
                Intent i = openFile(getReferrer(), "text/csv");
                // Wait until the user has returned to the app.
                try {
                    Thread.sleep(100);
                    System.out.println("Sleeping.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (!hasWindowFocus()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }, "CVS Opener");
            opener.start();
        });

        try {
            // File file = new File(getApplicationContext().getFilesDir() + "/src/main/res/drawable/qrtest.png");
            String decodedText = QRReader.decodeQRCode(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.qrtest));
            if (decodedText == null) {
                System.out.println("No QR Code found in the image");
            } else {
                System.out.println("Decoded text = " + decodedText);
            }
        } catch (IOException e) {
            System.out.println("Could not decode QR Code, " + e.getMessage());
        }

        Button enableCamera = findViewById(R.id.enableCamera);
        enableCamera.setOnClickListener(v -> {
            if (hasCameraPermission()) {
                enableCamera();
            } else {
                requestPermission();
            }
        });
    }

    private void updateScrollView() {
        // Generate StudentCard objects.
        StudentCard[] students = new StudentCard[manager.getList().length];
        for (int i = 0; i < students.length; i++) {
            students[i] = new StudentCard(manager.getList()[i]);
        }

        // Add cards to display.
        runOnUiThread(() -> {
            // First, delete the reminder that tells the user to load CSV data.
            TextView loadingReminder = findViewById(R.id.LoadingReminder);
            loadingReminder.setVisibility(GONE);

            // Add each view to the scrollview.
            LinearLayout l = findViewById(R.id.StudentScrollView);

            int i = 0;
            for (StudentCard c : students) {
                if (manager.getList()[i] != null) {
                    FragmentTransaction fm3t = getSupportFragmentManager().beginTransaction();
                    fm3t.add(R.id.StudentScrollView, c, "fragment_" + i);
                    fm3t.commit();
                    i++;
                }
            }
        });
    }

    private Intent openFile(Uri basis, String mime) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mime);

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, basis);

        startActivityForResult(intent, Intent.FILL_IN_DATA);
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        System.out.printf("Ran onActivityResult(), requestCode == %s, resultCode == %s%n", requestCode, resultCode);
        if (requestCode == 2
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                System.out.println("HERE: " + uri.getPath());

                try {
                    CSVReader.CVS c = getCVSFromUri(uri);
                    manager = new AttendanceManager(c);
                    updateScrollView();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("resultData URI was null.");
            }
        }
    }

    private CSVReader.CVS getCVSFromUri(Uri uri) throws IOException {
        // Read file in from the URI using some stolen code.
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

        String output = "";
        try (FileReader f = new FileReader(fileDescriptor)) {
            int content = 0;
            while ((content = f.read()) != -1) {
                output += (char) content;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        // Create a CVS reader from it and return it.
        return new CSVReader.CVS(output);
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                10
        );
    }

    private void enableCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
}