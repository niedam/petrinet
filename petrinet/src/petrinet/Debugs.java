package petrinet;

import alternator.Places;

import java.util.Map;
import java.util.Set;

public class Debugs<T> {

    public static boolean debugTestEnTrans(PetriNet<Integer> pn, Transition<Integer> t) {
        return pn.testEnabledTransition(pn.places, t);
    }


}
