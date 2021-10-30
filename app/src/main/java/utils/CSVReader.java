package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CSVReader {
    /*
    public static String[][] read(String input) {
        // Brings in CSV data.
        String exCSV = input;

        // Place underscores before commas between quotes.
            // Basically, we can tell if it's between quotes by measuring the number of quotes before the point in the line, and if it's odd, it's "probably" within a quote.
            // Split into lines.
                // Split by newlines into an ArrayList.
        ArrayList<String> lines = new ArrayList<>(1);
        {
            String output = exCSV;
            for (int i = 0; i < output.length(); i++) {
                if (output.charAt(i) == '\n') {
                    lines.add(output.substring(0, i) + "\n");

                    System.out.printf("Line: :%s:%n%n", output.substring(0, i));

                    // Cut down and restart loop.
                    output = output.substring(i + 1);
                    i = 0;
                }
            }

            // For each line, detect if a comma's between quotes by measuring that there's an odd number between it and the beginning of the line.
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                // Count the number of quotes before the comma.
                int numQuotes = 0;
                for (int j = 0; j < line.length(); j++) {
                    if (line.charAt(j) == '"') numQuotes++;

                    boolean bool;
                    try {
                        // TODO: Remove this
                        String currentLocation = line.substring(0, j + 1) + "|" + line.substring(j);
                        boolean isAnOddQuote = numQuotes % 2 == 1;
                        bool = (!(line.charAt(j - 1) + "" + line.charAt(j)).equals("_,") && line.charAt(j) == ',' && isAnOddQuote);
                    } catch (StringIndexOutOfBoundsException e) {
                        bool = false;
                    }

                    // If there's a comma and it's at an odd-position, which does not already have an underscore, place an underscore before it.
                    if (bool) {
                        line = line.substring(0, j) + "_" + line.substring(j);
                    }
                }

                // Push changes to the array.
                lines.set(i, line);
                output += line;
            }

            // Push changes back onto exCSV.
            exCSV = output;
        }

        // Create an Array to hold the data.
        // Find the number of values-- how many lines there are.
        int newLines = 0;
        for (int i = 0; i < exCSV.length(); i++) if (exCSV.charAt(i) == '\n') newLines++;

        // Find how many different parts for the data there are-- the number of commas in the first line + 1
        int numParts = 1;
        {
            Scanner s = new Scanner(exCSV);
            String firstLine = s.nextLine();
            for (int i = 0; i < firstLine.length(); i++) if (firstLine.charAt(i) == ',') numParts++;
            s.close();
        }

        // Create the actual array.
        String[][] data = new String[newLines][numParts];

        // Parse the data into the array.
        for (int i = 0; i < lines.size(); i++) {
            // Split into parts based off of the commas in the line.
            String line = lines.get(i);

            // Get the indexes of the commas and store them in an array if they don't have an underscore before them.
            int numCommas = 0;
            for (int k = 0; k < line.length(); k++) {
                if (k == 0) {
                  if (line.charAt(i) == ',') numCommas++;
                } else if (!(line.charAt(k - 1) + "" + line.charAt(k)).equals("_,") && line.charAt(k) == ',') numCommas++;
            }
            int[] commaIndexes = new int[numCommas + 1];
            // Note, I added one onto the end since we have to store the end of the string, as well, so it can split into two parts.

            // Store the indexes of the commas in the array.
            for (int k = 0; k < line.length(); k++) {
                if (k == 0) {
                    if (line.charAt(i) == ',') {
                        // Find the first empty index in the array and put the found comma's index in there.
                        for (int j = 0; j < commaIndexes.length; j++) {
                            if (commaIndexes[j] == 0) {
                                commaIndexes[j] = k;
                                break;
                            }
                        }
                    }
                } else if (!(line.charAt(k - 1) + "" + line.charAt(k)).equals("_,") && line.charAt(k) == ',') {
                    // Find the first empty index in the array and put the found comma's index in there.
                    for (int j = 0; j < commaIndexes.length; j++) {
                        if (commaIndexes[j] == 0) {
                            commaIndexes[j] = k;
                            break;
                        }
                    }
                }
            }


            // Add a "comma" onto the end of the list, so it goes to the end.
            commaIndexes[commaIndexes.length - 1] = line.length() + 1;

            // Loop through the substrings and place each into its corresponding part.
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
                    value = line.substring(previousIndex, commaIndexes[substringIndex] - 1);
                } catch (StringIndexOutOfBoundsException e) {
                    try {
                        value = line.substring(previousIndex, commaIndexes[substringIndex]);
                    } catch (StringIndexOutOfBoundsException f) {
                        value = line.substring(previousIndex);
                    }
                }

                // Put the value into the array, trimmed so as to remove any spaces, proceeding commas or quotes.
                value = value.trim();
                if (value.startsWith(",")) value = value.substring(1);
                if (value.startsWith("\"")) value = value.substring(1);
                if (value.endsWith("\"")) value = value.substring(0, value.length() - 1);

                System.out.printf("Value: \"%s\"%n", value);
                data[i][substringIndex] = value;
            }
        }

        // Remove the "_," from the data.
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] != null)
                data[i][j] = data[i][j].replaceAll("_,", ",");
            }
        }

        return data;
    }
    */

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
        // System.out.printf("1: %s %n%n", input);
        // Split the input by line.
        ArrayList<String> lines = new ArrayList<>(1);
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '\n') {
                // Push the line, and only the line, to the array.
                lines.add(input.substring(0, i));

                // Cut the line that we just removed off of the input.
                    // eg, make a substring from the next character and set that to the input.
                input = input.substring(i + 1);
                // System.out.printf("2: %s %n%n", input);
                // Restart the loop, so that this code runs until there are no lines left.
                i = 0;
            }
        }

        // Create an ArrayList to hold the outputted data.
        ArrayList[] output = new ArrayList[lines.size()];
        // Go through each Line, then add underscores before any commas which are after an odd number of quotes.
        for (int i = 0; i < lines.size(); i++) {
            int numQuotes = 0;
            String line = lines.get(i);

            // Create an array to hold the output of this line.
            output[i] = new ArrayList<String>(1);
            // Parse the line into the output array.
                // Split by "*," where * != "_" by looping until there is no line left.
                    // Navigate to the first comma, then see if the previous character is an underscore.
            for (int j = 0; j < line.length(); j++) {
                char prevChar;
                try {
                    prevChar = line.charAt(j - 1);
                } catch (StringIndexOutOfBoundsException e) {
                    prevChar = (char) 0;
                }

                if (line.charAt(j) == ',' && prevChar == '"') {
                    // If only the first conditional was true, push from j to the next comma. If the character being inspected is the first comma, then push from 0 to j.
                        // Note that we don't require an if-statement here, since I used continue; on line 245.

                    // Make the substring value.
                    String value = line.substring(0, j);

                    String placeInString = line.substring(0, j) + '|' + line.substring(j);

                    // Subtract the substring from this one.
                    line = line.replaceFirst(value, "");

                    // Push from either the previous comma to this one or 0 to this one.
                    // Remove trailing or preceding commas.
                    if (value.startsWith(",")) value = value.substring(1);
                    output[i].add(value);

                    // Restart loop.
                    j = 0;
                }
                System.out.println(output[i]);
                // System.out.printf("4: %s%n%n", line);
            }
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
                System.out.println("Of: " + arrayToString(data[index]) + "\nAt: " + index);
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
            System.out.printf("Requesting: %s%nFrom: %s%n", name, arrayToString(data[0]));
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
