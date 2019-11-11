package petrinet;

import java.util.*;


public class PetriNet<T> {

    private Map<T, Integer> places;
    private final boolean fair;

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

    private boolean testEnabledTransition(Transition<T> transition) {
        if (transition == null || !transition.arcConflict()) {
            return false;
        }
        Iterator<T> inhibitorIterator = transition.inhibitorIterator();
        while (inhibitorIterator.hasNext()) {
            T place = inhibitorIterator.next();
            if (places.containsKey(place) && places.get(place) > 0)
                return false;
        }
        Iterator<Map.Entry<T, Integer>> inputIterator = transition.inputIterator();
        while (inputIterator.hasNext()) {
            Map.Entry<T, Integer> arc = inputIterator.next();
            if (!places.containsKey(arc.getKey()) ||
                    places.get(arc.getKey()) < arc.getValue())
                return false;
        }
        return true;
    }

}