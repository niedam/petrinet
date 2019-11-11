package petrinet;

public class Debugs<T> {

    public static boolean debugTestEnTrans(PetriNet<Integer> pn, Transition<Integer> t) {
        return pn.testEnabledTransition(pn.places, t);
    }

}
