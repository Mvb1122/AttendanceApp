package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CVSReader {
    /**
     * Reads CSV data into a String[][].
     * <a href=https://replit.com/@mb1122/CSV-Parsing-Test?v=1>Here's the Replit for this, if you want to see how it works.</a>
     * @param input A raw string of CSV data.
     * @return The data, now imported to a String[][].
     * Note, the array is imported by row, including the first line,
     * which is usually used to determine the names of the fields.
     */
    public static String[][] read(String input) {
        // Brings in CSV data.
        String csv = input;
        String exCSV = "";
        {
            Scanner s = new Scanner(csv);

                while (s.hasNextLine()) {
                    exCSV += s.nextLine() + "\n";
                }

                s.close();
        }
        System.out.println(exCSV);

        Scanner s = new Scanner(csv);

        // Create an Array to hold the data.
        // Find the number of values-- how many lines there are.
        int newLines = 0;
        for (int i = 0; i < exCSV.length(); i++) if (exCSV.charAt(i) == '\n') newLines++;

        // Find how many different parts for the data there are-- the number of commas in the first line + 1
        int numParts = 1;
        {
            String firstLine = s.nextLine();
            for (int i = 0; i < firstLine.length(); i++) if (firstLine.charAt(i) == ',') numParts++;
        }

        // Create the actual array.
        String[][] data = new String[newLines][numParts];

        // Recycle the Scanner, so we can parse the *entire* array, not just the parts that we haven't cut out yet.
        s.close();
        s = new Scanner(csv);

        // Parse the data into the array.
        for (int i = 0; i < newLines; i++) {
            // Split into parts based off of the commas in the line.
            String line = s.nextLine();

            // Get the indexes of the commas and store them in an array.
            int numCommas = 0;
            for (int k = 0; k < line.length(); k++) if (line.charAt(k) == ',') numCommas++;
            int[] commaIndexes = new int[numCommas + 1];
            // Note, I added one onto the end since we have to store the end of the string, as well, so it can split into two parts.

            // Store the indexes of the commas in the array.
            for (int k = 0; k < line.length(); k++) if (line.charAt(k) == ',') {
                // Find the first empty index in the array and put the found comma's index in there.
                for (int j = 0; j < commaIndexes.length; j++) {
                    if (commaIndexes[j] == 0) {
                        commaIndexes[j] = k;
                        break;
                    }
                }
            }

            // Add a "comma" onto the end of the list, so it goes to the end.
            commaIndexes[commaIndexes.length - 1] = line.length() + 1;

            // Loop through the substrings and place each into its cooresponding part.
            for (int substringIndex = 0; substringIndex < commaIndexes.length; substringIndex++) {
                // Generate substring.
                int previousIndex;
                try {
                    previousIndex = commaIndexes[substringIndex - 1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    previousIndex = 0;
                }

                // Make the actual substring.
                String value = "";
                try {
                    value = line.substring(previousIndex, commaIndexes[substringIndex]);
                } catch (StringIndexOutOfBoundsException e) {
                    if (data[0].length > 1) {
                        value = line.substring(previousIndex + 1, commaIndexes[substringIndex] - 1);
                    } else {
                        value = line.substring(previousIndex, commaIndexes[substringIndex] - 1);
                    }
                }

                System.out.printf("Value: \"%s\"%n%n", value.trim());

                // Put the value into the array, trimmed so as to remove any spaces.
                data[i][substringIndex] = value.trim();
            }
        }

        s.close();

        return data;
    }

    public static String[][] read(Scanner s) {
        String input = "";
        while (s.hasNextLine()) {
            input += s.nextLine();
        }
        return read(input);
    }

    public static String[][] read(File f) throws FileNotFoundException {
        return read(new Scanner(f));
    }
}
