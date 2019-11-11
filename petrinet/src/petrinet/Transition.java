package petrinet;

import javafx.util.Builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


public class Transition<T> {

    private final Map<T, Integer> input;
    private final Collection<T> reset;
    private final Collection<T> inhibitor;
    private final Map<T, Integer> output;
    private final boolean alwaysDisenable;

    public Transition(Map<T, Integer> input, Collection<T> reset, Collection<T> inhibitor, Map<T, Integer> output) {
        this.input = (input == null?new HashMap<>():new HashMap<>(input));
        this.reset = (reset == null?new LinkedList<>():new LinkedList<>(reset));
        this.inhibitor = (inhibitor == null?new LinkedList<>():new LinkedList<>(inhibitor));
        this.output = (output == null?new HashMap<>():new HashMap<>(output));
        boolean alwaysDisenable = true;
        for (T t: this.reset) {
            if (this.input.containsKey(t) && this.input.get(t) > 0) {
                alwaysDisenable = false;
                break;
            }
        }
        this.alwaysDisenable = alwaysDisenable;
    }


    Iterator<T> resetIterator() {
        return reset.iterator();
    }

    Iterator<T> inhibitorIterator() {
        return inhibitor.iterator();
    }

    Iterator<Map.Entry<T, Integer>> inputIterator() {
        return input.entrySet().iterator();
    }

    Iterator<Map.Entry<T, Integer>> outputIterator() {
        return output.entrySet().iterator();
    }

    boolean arcNotConflict() {
        return alwaysDisenable;
    }

}