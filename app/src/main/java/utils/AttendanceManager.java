package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AttendanceManager {
    Student[] list;

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
            if (student.name.equals(id)) {
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
     * @param csv A comma-seperated-value list of students, in String form.
     */
    public AttendanceManager(String csv) {
        // Create Student objects from the CSV data.
        String[][] data = CVSReader.read(csv);
            // Determine which column is the one we need.
        int IDIndex = 0;
        for (int i = 0; i < data[0].length; i++) {
            if (data[0][i].equals(SettingsManager.ID_COLUMN)) {
                IDIndex = i;
            }
        }

            // Create.
        list = new Student[data.length - 1]; // The first row usually contains list information.
        for (int i = 0; i < data[0].length; i++) {
            list[i] = new Student(data[i+1][IDIndex]);
        }
    }

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
     * @param s
     */
    private AttendanceManager(Scanner s) {
        this(scannerToString(s));
    }

    private static String scannerToString(Scanner s) {
        String output = "";
        while (s.hasNextLine()) {
            output += s.nextLine();
        }
        return output;
    }

    private static class Student {
        /*
        name: The Student's ID.
        attending: If the student was present in class; true if they were, false if they weren't.
        timestamp: A timestamp representing the time that the student was signed in at.
        tardy: Whether the student arrived after the start time of the class or not.
         */
        String name;
        boolean attending, tardy;
        String timestamp;

        public Student(String id) {
            this.name = id;
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
    }
}
