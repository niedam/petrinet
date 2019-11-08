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

    public Transition(Map<T, Integer> input, Collection<T> reset, Collection<T> inhibitor, Map<T, Integer> output) {
        this.input = new HashMap<>(input);
        this.reset = new LinkedList<>(reset);
        this.inhibitor = new LinkedList<>(inhibitor);
        this.output = new HashMap<>(output);
    }

    public ArcIterator arcIterator() {
        return new ArcIterator();
    }

    class ArcIterator implements Iterator<Map.Entry<T, Integer>> {
        Iterator<Map.Entry<T, Integer>> inputIter;
        Iterator<T> inhibitorIter;
        public ArcIterator() {
            inputIter = input.entrySet().iterator();
            inhibitorIter = inhibitor.iterator();
        }
        @Override
        public boolean hasNext() {
            return inputIter.hasNext() || inhibitorIter.hasNext();
        }
        @Override
        public Map.Entry<T, Integer> next() {
            if (inputIter.hasNext()) {
                return inputIter.next();
            } else if (inhibitorIter.hasNext()) {
                return new AbstractMap.SimpleEntry<>(inhibitorIter.next(), 0);
            }
            return null;
        }
    }
}