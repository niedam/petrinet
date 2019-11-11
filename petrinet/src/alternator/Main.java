package alternator;
import petrinet.*;

import java.util.*;

import alternator.Places.*;



public class Main {

    private static PetriNet<Place> petriNet;

    private static final Mutex mutex = new Places.Mutex();

    private static final LocalSection localA = new Places.LocalSection("A");
    private static final CriticalSection criticalA = new Places.CriticalSection("A");

    private static final LocalSection localB = new Places.LocalSection("B");
    private static final CriticalSection criticalB = new Places.CriticalSection("B");

    private static final LocalSection localC = new Places.LocalSection("C");
    private static final CriticalSection criticalC = new Places.CriticalSection("C");

    List<Transition<Place>> makeTransitionList(LocalSection local, CriticalSection critical) {
        List<Transition<Place>> result = new LinkedList<>();
        result.add(new TransitionBuilder<Place>()
                .addInput(mutex, 1)
                .addInput(local, 1)
                .addOutput(critical, 1)
                .build());
        result.add(new TransitionBuilder<Place>()
                .addInput(critical, 1)
                .addOutput(local, 1)
                .addOutput(mutex, 1)
                .build());
        return result;
    }

    public static List<Transition<Place>> makeTransList(LocalSection local, CriticalSection critical) {
        Transition<Place> preProtocol = new TransitionBuilder<Place>()
                .addInput(local, 1)
                .addInput(mutex, 1)
                .addOutput(critical, 1)
                .build();
        Transition<Place> postProtocol = new TransitionBuilder<Place>()
                .addInput(critical, 1)
                .addOutput(local, 1)
                .addOutput(mutex, 1)
                .build();
        List<Transition<Place>> result = new LinkedList<>();
        result.add(preProtocol);
        result.add(postProtocol);
        return result;
    }



    protected static class Process implements Runnable {
        private final String name;
        private final Transition<Place> preProtocol;
        private final Transition<Place> postProtocol;
        private final List<Transition<Place>> transitions;

        Process(String name, LocalSection local, CriticalSection critical) {
            this.name = name;
            this.preProtocol = new TransitionBuilder<Place>()
                    .addInput(local, 1)
                    .addInput(mutex, 1)
                    .addOutput(critical, 1)
                    .build();
            this.postProtocol = new TransitionBuilder<Place>()
                    .addInput(critical, 1)
                    .addOutput(local, 1)
                    .addOutput(mutex, 1)
                    .build();
            this.transitions = new LinkedList<>();
            transitions.add(preProtocol);
            transitions.add(postProtocol);
        }

        @Override
        public void run() {
            while (true) {
                try {

                    if (preProtocol == petriNet.fire(transitions)) {
                        System.out.println(name + '.');
                        petriNet = petriNet;
                    }
                } catch (InterruptedException e) {
                    System.out.println("co");
                }
            }
        }
    }



    public static void main(String[] args) throws InterruptedException {
        Map<Place, Integer> enviroment = new HashMap<>();
        enviroment.put(localA, 1);
        enviroment.put(localB, 1);
        enviroment.put(localC, 1);
        enviroment.put(mutex, 1);
        petriNet = new PetriNet<>(enviroment, false);

        List<Transition<Place>> transitions = new LinkedList<>();

        transitions.addAll(makeTransList(localA, criticalA));
        transitions.addAll(makeTransList(localB, criticalB));
        transitions.addAll(makeTransList(localC, criticalC));

        Set<Map<Place, Integer>> r =  petriNet.reachable(transitions);

        Thread tA = new Thread(new Process("A", localA, criticalA));
        Thread tB = new Thread(new Process("B", localB, criticalB));
        Thread tC = new Thread(new Process("C", localC, criticalC));

        tA.start();
        tB.start();
        tC.start();

    }
}
