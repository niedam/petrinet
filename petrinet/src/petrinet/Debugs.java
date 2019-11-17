package petrinet;

import alternator.Places;

import java.util.Map;
import java.util.Set;

public class Debugs<T> {

    public static boolean debugTestEnTrans(PetriNet<Integer> pn, Transition<Integer> t) {
        return pn.testEnabledTransition(pn.places, t);
    }

    private static void printStateSet(Set<Map<Places.Place, Integer>> markingsSet) {
        for (Map<Places.Place, Integer> i: markingsSet) {
            for (Map.Entry<Places.Place, Integer> j: i.entrySet()) {
                System.out.print("(" + j.getKey().getName() + ", " + j.getValue() + ") ");
            }
            System.out.print('\n');
        }
    }

}
