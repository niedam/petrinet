package petrinet;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


public class Transition<T> {

    private Map<T, Integer> input;
    private Collection<T> reset;
    private Collection<T> inhibitor;
    private Map<T, Integer> output;
    private final boolean alwaysDisenable;

    public Transition(Map<T, Integer> input, Collection<T> reset, Collection<T> inhibitor, Map<T, Integer> output) {
        this.input = new HashMap<>(input);
        this.reset = new LinkedList<>(reset);
        this.inhibitor = new LinkedList<>(inhibitor);
        this.output = new HashMap<>(output);
        boolean alwaysDisenable = true;
        for (T t: reset) {
            if (input.containsKey(t) && input.get(t) > 0) {
                alwaysDisenable = false;
                break;
            }
        }
        this.alwaysDisenable = alwaysDisenable;
    }


    public Iterator<T> resetIterator() {
        return reset.iterator();
    }

    public Iterator<T> inhibitorIterator() {
        return inhibitor.iterator();
    }

    public Iterator<Map.Entry<T, Integer>> inputIterator() {
        return input.entrySet().iterator();
    }

    public Iterator<Map.Entry<T, Integer>> outputIterator() {
        return output.entrySet().iterator();
    }

    public boolean arcConflict() {
        return alwaysDisenable;
    }

}