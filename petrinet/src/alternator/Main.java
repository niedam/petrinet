package alternator;
import petrinet.*;

import java.util.*;

import alternator.Places.*;



public class Main {
    private static PetriNet<Place> petriNet;

    protected static class Process implements Runnable {
        private final List<Transition<Place>> transitions;
        private final Transition<Place> runCriticalSection;

        Process(ThreadPack threadPack) {
            transitions = threadPack.transitions;
            runCriticalSection = threadPack.runCriticalSection;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (runCriticalSection == petriNet.fire(transitions)) {
                        System.out.print(Thread.currentThread().getName() + '.');
                    }
                } catch (InterruptedException e) {
                    System.out.println("co");
                }
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

    private static boolean testSafety(Map<Place, Integer> map, CriticalSection c1,
                                        CriticalSection c2, CriticalSection c3) {
        return (!((map.get(c1) != null && map.get(c2) != null)
                    || (map.get(c1) != null && map.get(c3) != null)
                    || (map.get(c2) != null && map.get(c3) != null))?true:false);
    }

    public static void main(String[] args) throws InterruptedException {
        Map<Place, Integer> enviroment = new HashMap<>();
        ThreadPack packA = ThreadPack.makeThreadPack("A");
        ThreadPack packB = ThreadPack.makeThreadPack("B");
        ThreadPack packC = ThreadPack.makeThreadPack("C");
        enviroment.put(packA.readySection, 1);
        enviroment.put(packB.readySection, 1);
        enviroment.put(packC.readySection, 1);
        enviroment.put(Mutex.get(), 1);
        petriNet = new PetriNet<>(enviroment, false);

        Transition<Place> noDeadLockTransition = new TransitionBuilder<Place>()
                .addInput(Mutex.get(), 1)
                .addInput(packA.waitingSection, 1)
                .addInput(packB.waitingSection, 1)
                .addInput(packC.waitingSection, 1)
                .addOutput(Mutex.get(), 1)
                .addOutput(packA.readySection, 1)
                .addOutput(packB.readySection, 1)
                .addOutput(packC.readySection, 1)
                .build();
        List <Transition<Place>> transitions = new LinkedList<>();
        transitions.addAll(packA.transitions);
        transitions.addAll(packB.transitions);
        transitions.addAll(packC.transitions);
        transitions.add(noDeadLockTransition);
        Set<Map<Place,Integer>> markingsSet = petriNet.reachable(transitions);
        System.out.println("Posible marking state: " + markingsSet.size());

        boolean safety = true;
        for (Map<Place, Integer> map: markingsSet) {
            safety = safety && testSafety(map, packA.criticalSection, packB.criticalSection, packC.criticalSection);
        }
        System.out.println("All states are safety: " + safety);

        packA.addTransition(noDeadLockTransition);
        packB.addTransition(noDeadLockTransition);
        packC.addTransition(noDeadLockTransition);
        Thread threadA = new Thread(new Process(packA), "A");
        Thread threadB = new Thread(new Process(packB), "B");
        Thread threadC = new Thread(new Process(packC), "C");
        threadA.start();
        threadB.start();
        threadC.start();
    }
}
