package petrinet;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/** Helps with building transitions */
public class TransitionBuilder<T> {
    private final Map<T, Integer> input;
    private final Collection<T> reset;
    private final Collection<T> inhibitor;
    private final Map<T, Integer> output;

    /** Create builder */
    public TransitionBuilder() {
        input = new HashMap<>();
        reset = new LinkedList<>();
        inhibitor = new LinkedList<>();
        output = new HashMap<>();
    }

    /** Add input arc to builder */
    public TransitionBuilder<T> addInput(T key, int val) {
        input.put(key, val);
        return this;
    }

    /** Add reset arc to builder */
    public TransitionBuilder<T> addReset(T key) {
        reset.add(key);
        return this;
    }

    /** Add inhibitor arc to builder */
    public TransitionBuilder<T> addInhibitor(T key) {
        inhibitor.add(key);
        return this;
    }

    /** Add output transition to builder */
    public TransitionBuilder<T> addOutput(T key, int val) {
        output.put(key, val);
        return this;
    }

    /** Build transition with added arc */
    public Transition<T> build() {
        return new Transition<>(input, reset, inhibitor, output);
    }
}
