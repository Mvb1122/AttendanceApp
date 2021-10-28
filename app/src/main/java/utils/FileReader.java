package utils;

import com.ihaveawebsitetk.attendanceapp.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/** Updated from a past project at <a href=https://github.com/Mvb1122/Java-Projects/blob/main/FilesApp/Strigoi/app/src/main/java/com/main/strigoi/ui/Reader.java>my Github.</a> **/
public class FileReader {
    private static final File path = MainActivity.getPath();

    public static void writeFile(String data, String fileName) {
        File file = new File(path, File.pathSeparator + fileName);

        System.out.println(file.toString());

        try (FileOutputStream stream = new FileOutputStream(file)) {
            try {
                stream.write(data.getBytes());
            } finally {
                stream.close();
            }
            System.out.println("File written.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPath() {
        return path.toString();
    }

    public static String readFile(String inputPath) throws IOException {

        File p = new File(path, File.separator + inputPath);
        int length = (int) p.length();
        byte[] bytes = new byte[length];

        try (FileInputStream in = new FileInputStream(p)) {
            try {
                in.read(bytes);
            } catch (IOException e) {
                in.close();
                return e.toString();
            }
        } catch (IOException e) {
            throw e;
        }
        return new String(bytes);
    }

    public static boolean fileExists(String path, String type) {
        String file = null;
        try {
            file = readFile(path + type);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}