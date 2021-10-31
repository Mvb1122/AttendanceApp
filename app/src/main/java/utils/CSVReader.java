package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CSVReader {
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

    /**
     * Reads CSV data into a String[][].
     * <a href=https://replit.com/@mb1122/CSV-Parsing-Test?v=1>Here's the Replit for this, if you want to see how it works.</a>
     * @param input A raw string of CSV data.
     * @return The data, now imported to a String[][].
     * Note, the array is imported by row, including the first line,
     * which is usually used to determine the names of the fields.
     */
    public static String[][] read(String input) {
        /**
         * I actually wrote this method twice; I rewrote it using arraylists.
         * You can find the old version <a href="https://gist.github.com/Mvb1122/70dc4dfc2e622f31748da5d1ce1d29e2">here.</a>
         */

        // Split the input by line.
        ArrayList<String> lines = new ArrayList<>(1);
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '\n') {
                // Push the line, and only the line, to the array.
                lines.add(input.substring(0, i));

                // Cut the line that we just removed off of the input.
                    // eg, make a substring from the next character and set that to the input.
                input = input.substring(i + 1);
                // // System.out.printf("2: %s %n%n", input);
                // Restart the loop, so that this code runs until there are no lines left.
                i = 0;
            }
        }

        // Add the rest of the line, since it has the rest of the data.
            // Yes this is part of a patch.
        if (input.length() != 0) lines.add(input);

        // Create an ArrayList to hold the outputted data.
        ArrayList[] output = new ArrayList[lines.size()];
        // Go through each Line, then add underscores before any commas which are after an odd number of quotes.
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            // Remove the \r at the end of the line, if there is one.
            if (line.endsWith("\r")) line = line.substring(0, line.length() - 1);

            // Create an array to hold the output of this line.
            output[i] = new ArrayList<String>(1);
            // Parse the line into the output array.
                // Split by "*," where * != "_" by looping until there is no line left.
                    // Navigate to the first comma, then see if the previous character wasn't an underscore or it's in an odd quote..
            int numQuotes = 0;
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == '"') numQuotes++;

                char prevChar;
                try {
                    prevChar = line.charAt(j - 1);
                } catch (StringIndexOutOfBoundsException e) {
                    prevChar = (char) 0;
                }

                if (line.charAt(j) == ',' && (prevChar == '"' || numQuotes % 2 != 1)) {
                    // If only the first conditional was true, push from j to the next comma. If the character being inspected is the first comma, then push from 0 to j.
                        // Note that we don't require an if-statement here, since I used continue; on line 245.

                    // Make the substring value.
                    String value = line.substring(0, j + 1);

                    // Subtract the substring from this one.
                    line = line.replaceFirst(value, "");

                    // Push from either the previous comma to this one or 0 to this one.
                    // Remove trailing or preceding commas.
                    if (value.startsWith(",")) value = value.substring(1);
                    if (value.endsWith(",")) value = value.substring(0, value.length() - 1);
                    output[i].add(value);

                    // Restart loop.
                    j = 0;
                }
            }
            // Add the remaining content of the line, since it holds the last value.
                // Yes this is a patch, how could you tell?
            output[i].add(line);
        }

        // Convert the ArrayList-Array to a 2d String array.
            // Calculate maximum size for all indexes in output.
        int maxSize = Integer.MIN_VALUE;
        for (int i = 0; i < output.length; i++) if (output[i].size() > maxSize) maxSize = output[i].size();
        String[][] s2dOutput = new String[output.length][maxSize];

        // Copy.
        for (int i = 0; i < s2dOutput.length; i++) {
            for (int j = 0; j < s2dOutput[i].length; j++) {
                try {
                    try {
                        s2dOutput[i][j] = String.valueOf(output[i].get(j));
                    } catch (IndexOutOfBoundsException ignored) {

                    }
                } catch (NullPointerException e) {
                    // Do nothing, since that means that value was blank for that row/column but there was one on another row.
                }
            }
        }

        // DEBUG: Output.
        String o = "";
        for (int i = 0; i < lines.size(); i++) {
            o += lines.get(i);
        }

        // Remove the "_," and the quotes from the data.
        for (int i = 0; i < s2dOutput.length; i++) {
            for (int j = 0; j < s2dOutput[i].length; j++) {
                if (s2dOutput[i][j] != null) {
                    s2dOutput[i][j] = s2dOutput[i][j].replaceAll("_,", ",");
                    s2dOutput[i][j] = s2dOutput[i][j].replaceAll("\"", "");
                }
            }
        }

        return s2dOutput;
    }

    public static class CVS {
        public String[][] data;

        /**
         * Parses a CVS string and uses it as data.
         * @param input The CVS file, converted to a String, that you want to use.
         */
        public CVS(String input) {
            data = read(input);
        }

        public CVS(String[][] data){this.data = data;}

        public CVS(Scanner reader) { data = read(reader); }


        /**
         * Gets a value from the CVS, from the specific column at a specific index.
         * @param name The column's name you want to fetch from.
         * @param row The row from which you want to pull the value from.
         * @return The value, if possible.
         * Null, if the value isn't found.
         */
        public String get(String name, int row) {
            int index = indexOf(name);
            if (index != -1) {
                // System.out.println("Of: " + arrayToString(data[row]) + "\nAt: " + index);
                return data[row][index];
            } else {
                return null;
            }
        }


        /**
         * Finds the index of a specified column.
         * @param name The name of the column you want to find.
         * @return The index at which the column is, -1 if it's not found.
         */
        public int indexOf(String name) {
            // System.out.printf("Requesting: %s%nFrom: %s%n", name, arrayToString(data[0]));
            for (int i = 0; i < data[0].length; i++) {
                if (data[0][i].equals(name)) {
                    return i;
                }
            }
            return -1;
        }
    }

    private static String arrayToString(String[] s) {
        String output = "[ ";
        for (int i = 0; i < s.length; i++) {
            output += "\"" + s[i] + "\",";
        }
        return output + " ]";
    }
}
