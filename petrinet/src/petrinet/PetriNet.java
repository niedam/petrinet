package petrinet;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;


public class PetriNet<T> {

    private ConcurrentHashMap<T, Integer> places;
    private final Semaphore fireSecurity;
    private final Semaphore fireWaiting;

    private final BlockingQueue<Semaphore> waitingThreads;
    private Iterator<Semaphore> iterWaitingThreads;
    private volatile int countFireWaiting;
    //private final boolean fair;

    public PetriNet(Map<T, Integer> initial, boolean fair) {
        places = ((initial == null) ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(initial));
        this.countFireWaiting = 0;
        this.fireSecurity = new Semaphore(1, true);
        this.fireWaiting = new Semaphore(0, fair);
        this.waitingThreads = new LinkedBlockingQueue<Semaphore>();
        //this.waitingThreads = Collections.synchronizedList(new LinkedList<Semaphore>());
    }

    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {
        return null;
    }

    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {
        Transition<T> result = null;
        Semaphore mySemaphore = null;
        while (true) {
            fireSecurity.acquire();
            for (Transition<T> t: transitions) {
                if (fireOne(t)) {
                    result = t;
                    break;
                }
            }
            if (result == null) {
                if (mySemaphore == null) {
                    mySemaphore = new Semaphore(0);
                    waitingThreads.add(mySemaphore);
                }
                if (iterWaitingThreads.hasNext()) {
                    iterWaitingThreads.next().release();
                } else {
                    fireSecurity.release();
                }
                mySemaphore.acquire();
            } else {
                if (mySemaphore != null) {
                    iterWaitingThreads.remove();
                }
                iterWaitingThreads = waitingThreads.iterator();
                if (iterWaitingThreads.hasNext()) {
                    iterWaitingThreads.next().release();
                } else {
                    fireSecurity.release();
                }
                return result;
            }
        }
    }


    boolean fireOne(Transition<T> transition) throws InterruptedException {
        if (!testEnabledTransition(transition)) {
            return false;
        }
        Iterator<Map.Entry<T, Integer>> inputIterator = transition.inputIterator();
        while (inputIterator.hasNext()) {
            Map.Entry<T, Integer> arc = inputIterator.next();
            T arcKey = arc.getKey();
            int arcVal = arc.getValue();
            places.put(arcKey, places.get(arcKey) - arcVal);
        }
        Iterator<T> resetIterator = transition.resetIterator();
        while (resetIterator.hasNext()) {
            places.remove(resetIterator.next());
        }
        Iterator<Map.Entry<T, Integer>> outputIter = transition.outputIterator();
        while (outputIter.hasNext()) {
            Map.Entry<T, Integer> arc = outputIter.next();
            T arcKey = arc.getKey();
            int arcNewTok = arc.getValue();
            int arcOldVal = places.getOrDefault(arcKey, 0);
            places.put(arcKey, arcOldVal + arcNewTok);
        }
        return true;
    }


    boolean testEnabledTransition(Transition<T> transition) {
        if (transition == null || !transition.arcNotConflict()) {
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