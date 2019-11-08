package petrinet;

import java.util.*;


public class PetriNet<T> {

    private Map<T, Integer> places;
    private boolean fair;

    public PetriNet(Map<T, Integer> initial, boolean fair) {
        places = new HashMap<>(initial);
        this.fair = fair;
    }

    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {
        return null;
    }

    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {

        return null;
    }

    private boolean enabledTransition(Transition<T> transition) {
        Iterator<Map.Entry<T, Integer>> iter = transition.arcIterator();
        for (Map.Entry<T, Integer> arc: iter) {

            if (places.get(arc.getKey()) != arc.getValue()) {
                return false;
            }
        }
        return true;
    }

}