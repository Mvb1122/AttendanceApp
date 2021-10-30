package com.ihaveawebsitetk.attendanceapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ihaveawebsitetk.attendanceapp.databinding.FragmentStudentPanelBinding;

import java.util.Locale;

import utils.AttendanceManager;

public class StudentCard extends Fragment {
    private final AttendanceManager.Student s;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentStudentPanelBinding b = FragmentStudentPanelBinding.inflate(inflater, container, false);
        View root = b.getRoot();

        TextView name = b.NameDisplay;
        TextView periodNumber = b.PeriodDisplay;
        TextView attendance = b.AttendanceDisplay;
        TextView id = b.IDDisplay;
        TextView teacherName = b.TeacherDisplay;
        ImageView attendanceIcon = b.AttendanceImageDisplay;

        // Apply text.
        name.setText(s.studentName);

        String attendanceText = numberToPronunciation(s.periodNumber) + " period";
        // Capitalize first letter.
        attendanceText = ("" + attendanceText.charAt(0)).toUpperCase(Locale.ROOT) + attendanceText.substring(1);
        periodNumber.setText(attendanceText);


        attendance.setText(s.getAttendance());

        id.setText(String.valueOf(s.id));

        {
            String input = "Teacher: " + s.getManager().getTeacher();
            teacherName.setText(input);
        }

        attendanceIcon.setImageBitmap(s.getAttendanceIcon());

        return root;
    }

    public StudentCard(AttendanceManager.Student s) {
        super(R.layout.fragment_student_panel);
        this.s = s;
    }

    /**
     * Takes in a number and converts it to a String.
     * @param number The number you want to input. <strong>Only supports numbers 0-10!</strong>
     * @return A string representing it, like "first", "second" or "third".
     * Null if the number is not found.
     */
    private String numberToPronunciation(int number) {
        switch (number) {
            case 0:
                return "zeroth";
            case 1:
                return "first";
            case 2:
                return "second";
            case 3:
                return "third";
            case 4:
                return "fourth";
            case 5:
                return "fifth";
            case 6:
                return "sixth";
            case 7:
                return "seventh";
            case 8:
                return "eighth";
            case 9:
                return "ninth";
            case 10:
                return "tenth";
        }
        return null;
    }
}
