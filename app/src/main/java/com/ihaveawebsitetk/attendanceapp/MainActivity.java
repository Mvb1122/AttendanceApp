package com.ihaveawebsitetk.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import utils.AttendanceManager;
import utils.CSVReader;
import utils.SettingsManager;

public class MainActivity extends AppCompatActivity {
    static File path;
    static AttendanceManager manager;
    String lastRunIntentsGoal;

    public static File getPath() {
        return path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        path = getApplicationContext().getFilesDir();

        // Load attending icons.
        SettingsManager.ATTENDANCE_ICON_TRUE = BitmapFactory.decodeResource(getResources(), R.drawable.check_mark);

        SettingsManager.ATTENDANCE_ICON_FALSE = BitmapFactory.decodeResource(getResources(), R.drawable.x_mark);

        // Set the on-click listener for the "Load CSV" button.
        Button loadCSVButton = findViewById(R.id.loadButton);
        loadCSVButton.setOnClickListener(view -> {
            Thread opener = new Thread(() -> {
                lastRunIntentsGoal = "load";
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

        // Set the onClickListener for the enable camera button.
        Button enableCamera = findViewById(R.id.enableCamera);
        enableCamera.setOnClickListener(v -> {
            if (hasCameraPermission()) {
                enableCamera();
            } else {
                requestPermission();
            }
        });

        // Set the onClickListener for the save csv button.
        Button saveCSVButton = findViewById(R.id.saveButton);
        saveCSVButton.setOnClickListener(view -> {
            Thread saver = new Thread(() -> {
                lastRunIntentsGoal = "save";
                Intent i = choosePlaceToSave(getReferrer(), "text/csv");
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
            }, "CVS Saver");
            saver.start();
        });

        // Update the scrollview when the app is loaded.
        updateScrollView();
    }

    private void updateScrollView() {
        if (manager != null) {
            // Make a copy of the student list.
            AttendanceManager.Student[] list = new AttendanceManager.Student[manager.getList().length];
            System.arraycopy(manager.getList(), 0, list, 0, manager.getList().length);

            // Generate StudentCard objects.
            StudentCard[] students = new StudentCard[list.length];
            for (int i = 1; i < students.length; i++) {
                students[i] = new StudentCard(list[i]);
            }

            // Add cards to display.
            runOnUiThread(() -> {
                // Clear the linearlayout, thereby deleting the reminder that tells the user to load CSV data.
                LinearLayout l = findViewById(R.id.StudentScrollView);
                l.removeAllViews();
            });

            // Add the views to the scroller from another thread.
            Thread scroller = new Thread(() -> {
                // Add each view to the scrollview.
                for (int i = 1; i != students.length; i++) {
                    FragmentTransaction fm3t = getSupportFragmentManager().beginTransaction();
                    fm3t.add(R.id.StudentScrollView, students[i], "fragment_" + i);
                    fm3t.commit();
                }
            });
            scroller.start();
        }
    }

    private Intent choosePlaceToSave(Uri basis, String mime) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mime);

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, basis);

        startActivityForResult(intent, Intent.FILL_IN_DATA);
        return intent;
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
            /**
             * Based on the last run intent's goal, either load or save to that path.
             */
            if (lastRunIntentsGoal.equals("load")) {
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
            } else if (lastRunIntentsGoal.equals("save")) {
                // Dump the AttendanceManager's data to the selected file.
                    // uses stolen Google:tm: code.
                Uri uri = null;
                if (resultData != null) {
                    uri = resultData.getData();
                    try {
                        ParcelFileDescriptor pfd = getContentResolver().
                                openFileDescriptor(uri, "w");
                        FileOutputStream fileOutputStream =
                                new FileOutputStream(pfd.getFileDescriptor());
                        fileOutputStream.write((manager.dump()).getBytes());
                        // Let the document provider know you're done by closing the stream.
                        fileOutputStream.close();
                        pfd.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * When MainActivity is resumed; eg, when we leave the camera, update the scrollview.
     */
    @Override
    public void onResume() {
        super.onResume();
        updateScrollView();
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