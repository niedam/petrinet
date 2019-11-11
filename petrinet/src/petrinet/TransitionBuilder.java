package petrinet;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class TransitionBuilder<T> {
    private final Map<T, Integer> input;
    private final Collection<T> reset;
    private final Collection<T> inhibitor;
    private final Map<T, Integer> output;

    public TransitionBuilder() {
        input = new HashMap<>();
        reset = new LinkedList<>();
        inhibitor = new LinkedList<>();
        output = new HashMap<>();
    }

    public TransitionBuilder<T> addInput(T key, int val) {
        input.put(key, val);
        return this;
    }

    public TransitionBuilder<T> addReset(T key) {
        reset.add(key);
        return this;
    }

    public TransitionBuilder<T> addInhibitor(T key) {
        inhibitor.add(key);
        return this;
    }

    public TransitionBuilder<T> addOutput(T key, int val) {
        output.put(key, val);
        return this;
    }

    public Transition<T> build() {
        return new Transition<>(input, reset, inhibitor, output);
    }
}
