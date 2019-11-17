package alternator;
import petrinet.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import alternator.Places.*;


public class Main {
    private static PetriNet<Place> petriNet;

    /** Runners for thread */
    protected static class Process implements Runnable {
        private final List<Transition<Place>> transitions;
        private final Transition<Place> runCriticalSection;

        Process(ThreadPack threadPack) {
            transitions = threadPack.transitions;
            runCriticalSection = threadPack.runCriticalSection;
        }

        @Override
        public void run() {
            while (true) try {
                if (runCriticalSection == petriNet.fire(transitions)) {
                    System.out.print(Thread.currentThread().getName() + '.');
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /** Runners to prevent dead-lock */
    protected static class AntyDeadLock implements Runnable {

        private final List<Transition<Place>> transitions;

        AntyDeadLock(List<Transition<Place>> transitions) {
            this.transitions = transitions;
        }

        @Override
        public void run() {
            while (true) try {
                petriNet.fire(transitions);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private static void printStateSet(Set<Map<Place, Integer>> markingsSet) {
        for (Map<Place, Integer> i: markingsSet) {
            for (Map.Entry<Place, Integer> j: i.entrySet()) {
                System.out.print("(" + j.getKey().getName() + ", " + j.getValue() + ") ");
            }
            System.out.print('\n');
        }
    }

    /** Check if in every critical section is only one process. */
    private static boolean testSafety(Map<Place, Integer> map, CriticalSection c1,
                                        CriticalSection c2, CriticalSection c3) {
        return (!((map.get(c1) != null && map.get(c2) != null)
                || (map.get(c1) != null && map.get(c3) != null)
                || (map.get(c2) != null && map.get(c3) != null)));
    }

    public static void main(String[] args) {
        Map<Place, Integer> enviroment = new HashMap<>();
        // All places and transitions connected with particularly thread
        ThreadPack A = ThreadPack.makeThreadPack("A");
        ThreadPack B = ThreadPack.makeThreadPack("B");
        ThreadPack C = ThreadPack.makeThreadPack("C");
        // Build special transition to prevent dead-lock
        Transition<Place> noDeadLockTransition = new TransitionBuilder<Place>()
                .addInput(Mutex.get(), 1)
                .addInput(A.waitingSection, 1)
                .addInput(B.waitingSection, 1)
                .addInput(C.waitingSection, 1)
                .addOutput(Mutex.get(), 1)
                .addOutput(A.readySection, 1)
                .addOutput(B.readySection, 1)
                .addOutput(C.readySection, 1)
                .build();
        List <Transition<Place>> noDeadLockList = new LinkedList<>();
        noDeadLockList.add(noDeadLockTransition);
        // Add tokens to places in Petri net
        enviroment.put(A.readySection, 1);
        enviroment.put(B.readySection, 1);
        enviroment.put(C.readySection, 1);
        enviroment.put(Mutex.get(), 1);
        // Build Petri net
        petriNet = new PetriNet<>(enviroment, false);
        // All transitions in net to simulation with PetriNet.reachable()
        List <Transition<Place>> transitions = new LinkedList<>();
        transitions.addAll(A.transitions);
        transitions.addAll(B.transitions);
        transitions.addAll(C.transitions);
        transitions.add(noDeadLockTransition);
        // Generate all possible states using list of all transitions.
        Set<Map<Place,Integer>> markingsSet = petriNet.reachable(transitions);
        System.out.println("Possible marking state: " + markingsSet.size());
        // Test safety of all states.
        boolean safety = true;
        for (Map<Place, Integer> map: markingsSet) {
            safety = safety && testSafety(map, A.criticalSection, B.criticalSection, C.criticalSection);
        }
        System.out.println("All states are safety: " + safety);
        //Run additionally threads to control dead-lock states
        Thread noDeadLockControl = new Thread(new AntyDeadLock(noDeadLockList));
        noDeadLockControl.start();
        // Creating threads with their transitions and places packages.
        Thread threadA = new Thread(new Process(A), A.idName);
        Thread threadB = new Thread(new Process(B), B.idName);
        Thread threadC = new Thread(new Process(C), C.idName);
        threadA.start();
        threadB.start();
        threadC.start();
        try {
            Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
            System.err.println(e.toString());
        } finally {
            threadA.interrupt();
            threadB.interrupt();
            threadC.interrupt();
            noDeadLockControl.interrupt();
        }
    }
}
