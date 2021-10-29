package utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ihaveawebsitetk.attendanceapp.R;

public class SettingsManager {

    // As settings, for this app at least, only exist on-device, this class just holds and writes information when it's changed.
    // It also will read information on app open using static {} and hold common information here, too.

    // Declare settings.
        // Columns
    public static String ID_COLUMN = "Sis Number";
    public static String NAME_COLUMN = "Student Name";
    public static String PERIOD_COLUMN = "Period Begin";
    public static String TEACHER_COLUMN = "Teacher Name";

    // Attendance, in strings.
    public static final String ATTENDANCE_TRUE = "On time.";
    public static final String ATTENDANCE_FALSE = "Absent.";
    public static final String ATTENDANCE_TARDY = "Tardy.";

    // Attendance, in icons. These settings purely exist to facilitate ease-of-access code-side.
    public static Bitmap ATTENDANCE_ICON_FALSE;
    public static Bitmap ATTENDANCE_ICON_TRUE;
}
