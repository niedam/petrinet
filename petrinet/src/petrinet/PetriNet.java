package petrinet;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;


public class PetriNet<T> {
    HashMap<T, Integer> places;
    private final Semaphore fireSecurity;
    private final BlockingQueue<Semaphore> waitingThreads;
    private Iterator<Semaphore> iterWaitingThreads;

    /**
     * Build petri net
     * @param[in] initial - map of places with their tokens number
     * @param[in] fair - set fair property
     */
    public PetriNet(Map<T, Integer> initial, boolean fair) {
        places = ((initial == null) ? new HashMap<>() : new HashMap<>(initial));
        Iterator<Map.Entry<T, Integer>> iteratorPlace = places.entrySet().iterator();
        while (iteratorPlace.hasNext()) {
            Map.Entry<T, Integer> p = iteratorPlace.next();
            if (p.getValue() == 0) {
                iteratorPlace.remove();
            }
        }
        this.fireSecurity = new Semaphore(1, true);
        this.waitingThreads = new LinkedBlockingQueue<>();
    }


    /**
     * Generate all possible marking of net
     * @param[in] transitions - list of transitions
     * @return All possible states reachable with transitions from list
     */
    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {
        if (transitions == null) {
            transitions = new LinkedList<>();
        }
        Set<Map<T, Integer>> result = new HashSet<>();
        Queue<Map<T, Integer>> queue = new LinkedList<>();
        Map<T, Integer> first_state = new HashMap<>();
        try {
            fireSecurity.acquire();
            first_state = new HashMap<>(places);
            fireSecurity.release();
        } catch(final InterruptedException e) {
            System.err.println(e);
        }
        result.add(first_state);
        queue.add(first_state);
        while (!queue.isEmpty()) {
            Map<T, Integer> state = queue.poll();
            for (Transition<T> t: transitions) {
                Map<T, Integer> new_state = new HashMap<>(state);
                fireOne(new_state, t);
                if (!result.contains(new_state)) {
                    queue.add(new_state);
                    result.add(new_state);
                }
            }
        }
        return result;
    }

    /**
     * Run one available transition from transitions list.
     * If there's no any available transition, wait until one of them became available.
     * @param[in] transitions - list of transitions
     * @return Fired transition.
     * @throws InterruptedException
     */
    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {
        if (transitions == null) {
            return null;
        } else {
            transitions = new LinkedList<>(transitions);
        }
        Transition<T> result = null;
        Semaphore mySemaphore = null;
        while (true) {
            if (mySemaphore == null) {
                fireSecurity.acquire();
            }
            for (Transition<T> t: transitions) {
                if (fireOne(places, t)) {
                    result = t;
                    break;
                }
            }
            if (result == null) {
                if (mySemaphore == null) {
                    mySemaphore = new Semaphore(0);
                    waitingThreads.add(mySemaphore);
                }
                if (iterWaitingThreads != null && iterWaitingThreads.hasNext()) {
                    Semaphore s = iterWaitingThreads.next();
                    s.release();
                } else {
                    fireSecurity.release();
                }
                try {
                    mySemaphore.acquire();
                } catch (InterruptedException e) {
                    waitingThreads.remove(mySemaphore);
                    throw e;
                }
            } else {
                if (mySemaphore != null) {
                    waitingThreads.remove(mySemaphore);
                }
                iterWaitingThreads = waitingThreads.iterator();
                if (iterWaitingThreads.hasNext()) {
                    Semaphore s = iterWaitingThreads.next();
                    s.release();
                } else {
                    fireSecurity.release();
                }
                return result;
            }
        }
    }

    /**
     * @param[in] key - key of place in net
     * @return Amount of tokens at place with key
     */
    public Integer getToken(T key) {
        int result = 0;
        try {
            fireSecurity.acquire();
            result = places.getOrDefault(key, 0);
            fireSecurity.release();
        } catch (InterruptedException e) {
            System.err.println(Thread.currentThread() + " " + e);
        }
        return result;
    }

    /**
     * Fire one transition on map of place
     * @param[in, out] places - map of places in net
     * @param[in] transition - transition to fire
     * @return If transition was fired - @p true, if transition was unavailable - @p false
     */
    private boolean fireOne(Map<T, Integer> places, Transition<T> transition) {
        if (!testEnabledTransition(places, transition)) {
            return false;
        }
        Iterator<Map.Entry<T, Integer>> inputIterator = transition.inputIterator();
        while (inputIterator.hasNext()) {
            Map.Entry<T, Integer> arc = inputIterator.next();
            T arcKey = arc.getKey();
            int arcVal = arc.getValue();
            if (places.get(arcKey) - arcVal == 0) {
                places.remove(arcKey);
            } else {
                places.put(arcKey, places.get(arcKey) - arcVal);
            }
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

    /**
     * Test if it's possible to fire transition.
     * @param[in] places - map of places in net
     * @param[in, out] transition - transition to test
     * @return @p true - it's possible to fire transition, @p false if transition is unavailable
     */
    boolean testEnabledTransition(Map<T, Integer> places, Transition<T> transition) {
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