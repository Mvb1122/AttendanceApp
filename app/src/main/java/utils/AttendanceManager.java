package utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AttendanceManager {
    Student[] list;
    String teacher;

    public AttendanceManager(Student[] list) {
        this.list = list;
    }

    /**
     * Marks a student as present.
     * @param id The Student-ID of the selected student.
     * @return True if the student was found, false if they were not found or if they
     * were already marked as attending.
     */
    public boolean markAttending(String id) {
        // Loop through the list of students and find the one who matches the id.
        for (Student student : list) {
            if (student.id.equals(id)) {
                // If that student was already marked as attending, return false.
                if (student.attending) {
                    return false;
                }

                // Else, set the student to be attending and return true.
                student.setAttending(true);
                return true;
            }
        }
        return false;
    }


    /**
     * Parses an inputted String CSV to the list of Students, before then using that
     * to call the other constructor.
     * @param c A CVS list pulled from a Synergy classlist.
     */
    public AttendanceManager(CSVReader.CVS c) {
        // Create Student objects from the CSV data.
        String[][] data = c.data;
        list = new Student[data.length];
        // The first row usually contains list information, so I set the start for the list to be 1 and the length to be equal to the data's length.
        // TODO: Correct this memory inference.
        for (int i = 1; i != data.length; i++) {
            list[i] = new Student(c.get(SettingsManager.ID_COLUMN, i));
            list[i].manager = this;

            // Add each Student's actual name and period number.
            list[i].studentName = c.get(SettingsManager.NAME_COLUMN, i);

            list[i].periodNumber = Integer.parseInt(c.get(SettingsManager.PERIOD_COLUMN, i));
        }

        // Bind teacher to class.
        teacher = c.get(SettingsManager.TEACHER_COLUMN, 2);

        // Validate data.
        for (int i = 1; i < list.length; i++) {
            if (list[i] == null) {
                System.out.printf("Student #%s is null.", i);
            }
        }
    }

    public Student[] getList() {return list;}

    /**
     * Takes in a file, containing CSV data and then parses it to a list of Students,
     * before then using that to call its constructor.
     * @param f A file, containing CSV data.
     * @throws FileNotFoundException if the specified CSV file is not found.
     */
    public AttendanceManager(File f) throws FileNotFoundException {
        this(new Scanner(f));
    }

    /**
     * This private constructor facilitates code re-usage by allowing me to just create Scanners on
     * the other two constructors before calling this one.
     * @param s A scanner to be converted to string before calling the other constructor.
     */
    private AttendanceManager(Scanner s) {
        this(scannerToCVS(s));
    }

    private static CSVReader.CVS scannerToCVS(Scanner s) {
        return new CSVReader.CVS(scannerToString(s));
    }

    private static String scannerToString(Scanner s) {
        String output = "";
        while (s.hasNextLine()) {
            output += s.nextLine();
        }
        return output;
    }

    public String getTeacher() {
        return teacher;
    }

    public static class Student {
        /*
        name: The Student's ID.
        attending: If the student was present in class; true if they were, false if they weren't.
        timestamp: A timestamp representing the time that the student was signed in at.
        tardy: Whether the student arrived after the start time of the class or not.
        periodNumber: The number period of which the student is attending.
        manager: The spawning AttendanceManager.
         */
        public String id;
        public boolean attending, tardy;
        public int periodNumber; // Should be -1 if there is no period.
        public String timestamp;
        protected AttendanceManager manager;

        // Obvious Student information.
        public String studentName; // Human name, eg; Micah Bushman, instead of 98000XXXX


        public Student(String id) {
            this.id = id;
        }

        public void setAttending(boolean attending) {
            this.attending = attending;

            // Generate the timestamp.
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            timestamp = dtf.format(now);
        }

        public void setTardy(boolean tardy) {
            this.tardy = tardy;
        }

        public String getAttendance() {
            if (tardy) {
                return SettingsManager.ATTENDANCE_TARDY;
            }

            if (attending) {
                return SettingsManager.ATTENDANCE_TRUE;
            } else {
                return SettingsManager.ATTENDANCE_FALSE;
            }
        }

        public AttendanceManager getManager() {return manager; }

        public Bitmap getAttendanceIcon() {
            if (tardy || attending) {
                return SettingsManager.ATTENDANCE_ICON_TRUE;
            } else {
                return SettingsManager.ATTENDANCE_ICON_FALSE;
            }
        }
    }
}
