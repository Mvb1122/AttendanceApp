package com.ihaveawebsitetk.attendanceapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ihaveawebsitetk.attendanceapp.databinding.FragmentScanNotificationBinding;

public class ScanningCard extends Fragment {
    private final String scanText;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentScanNotificationBinding b = FragmentScanNotificationBinding.inflate(inflater, container, false);
        View root = b.getRoot();

        TextView scannedTextDisplay = b.scanText;

        // Apply text.
        String text = "Scanned:\n" + scanText;
        scannedTextDisplay.setText(text);

        return root;
    }

    public ScanningCard(String scanText) {
        super(R.layout.fragment_student_panel);
        this.scanText = scanText;
    }
}
