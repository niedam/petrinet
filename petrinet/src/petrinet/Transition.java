package petrinet;

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
    private final boolean alwaysEnable;

    /**
     * Create transition with collection of arcs
     * @param[in] input - input arcs
     * @param[in] reset - reset arcs
     * @param[in] inhibitor - inhibitor arcs
     * @param[in] output - output arcs
     */
    public Transition(Map<T, Integer> input, Collection<T> reset, Collection<T> inhibitor, Map<T, Integer> output) {
        this.input = (input == null?new HashMap<>():new HashMap<>(input));
        this.reset = (reset == null?new LinkedList<>():new LinkedList<>(reset));
        this.inhibitor = (inhibitor == null?new LinkedList<>():new LinkedList<>(inhibitor));
        this.output = (output == null?new HashMap<>():new HashMap<>(output));
        boolean alwaysEnable = true;
        for (T t: this.inhibitor) {
            if (this.input.containsKey(t) && this.input.get(t) > 0) {
                alwaysEnable = false;
                break;
            }
        }
        this.alwaysEnable = alwaysEnable;
    }

    /**
     * @return Return iterator for reset arc in transition.
     */
    Iterator<T> resetIterator() {
        return reset.iterator();
    }

    /**
     * @return Return iterator for inhibitor arc in transition.
     */
    Iterator<T> inhibitorIterator() {
        return inhibitor.iterator();
    }

    /**
     * @return Return iterator for input arc in transtion.
     */
    Iterator<Map.Entry<T, Integer>> inputIterator() {
        return input.entrySet().iterator();
    }

    /**
     * @return Return iterator for output arc in transition
     */
    Iterator<Map.Entry<T, Integer>> outputIterator() {
        return output.entrySet().iterator();
    }

    /** Check if some arc make conflict (ex. positive input and inhibitor arc to ane place)
     * @return @p true - no conflict, @p false - conflict
     */
    boolean arcNotConflict() {
        return alwaysEnable;
    }

}