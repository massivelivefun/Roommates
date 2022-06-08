import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Roommates {
    // The number of participants in the roommate pairing.
    private final int numOfParticipants;

    // The mapping of the participant and their preferences.
    private final Map<String, List<String>> participantPreferences;

    // The list of people participating in the matching.
    private final List<String> participants;

    // Accumulator that keeps track of the number of times that the matching is
    // stable.
    private int numOfStableMatches = 0;

    public Roommates(
        int numOfParticipants,
        Map<String, List<String>> participantPreferences) {
        this.numOfParticipants = numOfParticipants;
        this.participantPreferences = participantPreferences;
        participants = participants();
    }

    public void run() {
        if (numOfParticipants % 2 != 0) {
            System.out.println("So the number of participants is odd, so...");
            System.out.println("I'm gonna stop now. Bye!");
            return;
        }

        for (final var matching: allPossibleMatchings(participants)) {
            if (verifyMatches(participants, matching, participantPreferences)) {
                numOfStableMatches++;
            }
        }

        if (numOfStableMatches > 0) {
            System.out.println("Yes " + numOfStableMatches);
        } else {
            System.out.println("No");
        }
    }

    // The double factorial of odd numbers sequence.
    // https://oeis.org/A001147
    // https://www.quora.com/How-many-ways-are-there-to-divide-m-people-into-pairs-assuming-there-are-an-even-number-of-people

    // m = number of people
    // k = number of pairs
    // k = m / 2
    // 2k = m
    // m! / (2^k * k!)

    // Example math with 8 participants
    // 8! / (2^4 * 4!)
    // 40320 / (16 * 24)
    // 40320 / 384 = 105 (nice)

    // None
    // r = 1
    // []

    // 1, 2
    // r = 1
    // [{(1, 2)}]

    // 1, 2, 3, 4
    // r = 3
    // [{(1, 2), (3, 4)},
    // {(1, 3), (2, 4)},
    // {(1, 4), (2, 3)}]

    // 1, 2, 3, 4, 5, 6
    // r = 15
    // [{(1, 2), (3, 4), (5, 6)},
    // {(1, 2), (3, 5), (4, 6)},
    // {(1, 2), (3, 6), (4, 5)},
    // {(1, 3), (2, 4), (5, 6)},
    // {(1, 3), (2, 5), (4, 6)},
    // {(1, 3), (2, 6), (4, 5)},
    // {(1, 4), (2, 3), (5, 6)},
    // {(1, 4), (2, 5), (3, 6)},
    // {(1, 4), (2, 6), (3, 5)},
    // {(1, 5), (2, 3), (4, 6)},
    // {(1, 5), (2, 4), (3, 6)},
    // {(1, 5), (2, 6), (3, 4)},
    // {(1, 6), (2, 3), (4, 5)},
    // {(1, 6), (2, 4), (3, 5)},
    // {(1, 6), (2, 5), (3, 4)}]

    // 1, 2, 3, 4, 5, 6, 7, 8
    // r = 105
    // ...

    // This python generator saved my life, but I still needed to convert it
    // from being a generator.
    // https://stackoverflow.com/questions/5360220/how-to-split-a-list-into-pairs-in-all-possible-ways
    private static ArrayList<HashMap<String, String>> allPossibleMatchings(
        final List<String> list) {
        final var returnList = new ArrayList<HashMap<String, String>>();
        if (list.size() < 2) {
            return new ArrayList<>();
        }
        // For handling odd sized lists. Ignore this as all lists are even.
//        if (list.size() % 2 == 1) {
//            for (int i = 1; i < list.toArray().length; i++) {
//                var array = new ArrayList<>(list.subList(0, i));
//                array.addAll(list.subList(i + 1, list.toArray().length));
//                for (var pair: recur(array)) {
//                    returnList.add(pair);
//                    map.putAll(pair);
//                }
//            }
//        } else {
        final var key = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            final var outerMap = new HashMap<String, String>();
            final var value = list.get(i);
            outerMap.put(key, value);
            outerMap.put(value, key);

            final var array = new ArrayList<>(list.subList(1, i));
            array.addAll(list.subList(i + 1, list.size()));

            final var recurList = allPossibleMatchings(array);
            if (recurList.size() == 0) {
                returnList.add(outerMap);
            }
            for (final var rest : recurList) {
                final var innerMap = new HashMap<>(outerMap);
                innerMap.putAll(rest);
                returnList.add(innerMap);
            }
        }
//        }
        return returnList;
    }

    private static boolean verifyMatches(
        final List<String> people,
        final Map<String, String> matches,
        final Map<String, List<String>> preferences) {
        // If the matching doesn't have everyone.
        if (!matches.keySet().containsAll(people)) {
            return false;
        }

        // Go through all the pairings in the matching that was given.
        for (final var pair: matches.entrySet()) {
            final var currParticipant = pair.getKey();
            final var currPartPrefList = preferences.get(currParticipant);
            final var currPartBetterPicks = new ArrayList<>(
                currPartPrefList.subList(
                    0,
                    currPartPrefList.indexOf(pair.getValue())
                )
            );

            // See if the receiver has a better person than what they have
            // that's willing to switch.
            for (final var potPartBetterPick: currPartBetterPicks) {
                final var potPartBetterPickPartner = matches
                    .get(potPartBetterPick);
                final var potPartBetterPicksPrefs = preferences
                    .get(potPartBetterPick);
                if (potPartBetterPicksPrefs.indexOf(potPartBetterPickPartner) >
                    potPartBetterPicksPrefs.indexOf(currParticipant)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Make the KeySet have no references to the participant preference list.
    private List<String> participants() {
        return new ArrayList<>(new HashSet<>(participantPreferences.keySet()));
    }
}
