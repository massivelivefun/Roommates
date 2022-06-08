import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.NoSuchElementException;

public class Program {
    public static void main(String[] args) {
        // Get the input file from the end user.
        Scanner scr = new Scanner(System.in);
        String fileName;
        var numOfParticipants = 0;
        Map<String, List<String>> participantPreferences = null;
        System.out.print("Enter the input file relative to the pwd: ");
        try {
            fileName = scr.nextLine();
            scr = new Scanner(new File(fileName));
            numOfParticipants = numOfParticipants(scr);
            participantPreferences = participantPreferences(scr);
        } catch (FileNotFoundException |
                 IllegalStateException |
                 NoSuchElementException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Roommates rm = new Roommates(
            numOfParticipants, participantPreferences);
        rm.run();
    }

    private static int numOfParticipants(Scanner scr)
        throws IllegalStateException,
        NoSuchElementException {
        var numberOfParticipants = scr.nextInt();
        scr.nextLine();
        return numberOfParticipants;
    }

    private static Map<String, List<String>> participantPreferences(Scanner scr)
        throws NoSuchElementException, IllegalStateException {
        Map<String, List<String>> preferences = new HashMap<>();
        int index = 1;

        // Get each link from file and put it into preferences hashmap.
        while (scr.hasNextLine()) {
            String line = scr.nextLine();
            String indexString = String.valueOf(index);
            // .split() will never throw a PatternSyntaxException because the
            // pattern is always valid.
            String[] numbers = line.trim().split("\\s+");
            // .asList will never throw a NullPointerException because the
            // numbers array that its given is always allocated to a non-null
            // space
            List<String> lineList = Arrays.asList(numbers);
            if (lineList.contains(indexString)) {
                throw new IllegalStateException("Participant's value cannot " +
                    "be in their own preference list.");
            }
            preferences.put(indexString, lineList);
            index += 1;
        }

        return preferences;
    }
}
