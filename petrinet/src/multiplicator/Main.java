package multiplicator;

import petrinet.*;

import java.util.*;

public class Main {

    static PetriNet<Place> petriNet;
    static volatile boolean work = true;

    private enum Place {
        A, B, Empty, Buffor, Result;
    }

    private static class Process implements Runnable {

        List <Transition<Place>> transitions;

        Process(List<Transition<Place>> transitions) {
            this.transitions = new LinkedList<>(transitions);
        }

        @Override
        public void run() {
            int count = 0;
            while (work) {
                try {
                    petriNet.fire(transitions);
                    count++;
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " fired transitions " + count + " times.");
                }
            }
        }
    }


    private static void printStateSet(Set<Map<Place, Integer>> markingsSet) {
        for (Map<Place, Integer> i: markingsSet) {
            System.out.print('*');
            for (Map.Entry<Place, Integer> j: i.entrySet()) {
                System.out.print("(" + j + ") ");
            }
            System.out.print('\n');
        }
    }

    public static void main(String[] args) {


        Map<Place, Integer> init = new LinkedHashMap<>();
        init.put(Place.A, 2);
        init.put(Place.B, 3);

        petriNet = new PetriNet<Place>(init, false);

        Transition<Place> zeroA = new TransitionBuilder<Place>()
                .addInput(Place.B, 1)
                .addInhibitor(Place.A)
                .addReset(Place.B)
                .build();
        Transition<Place> zeroB = new TransitionBuilder<Place>()
                .addInhibitor(Place.B)
                .addInhibitor(Place.Buffor)
                .addInput(Place.A, 1)
                .addReset(Place.A)
                .build();
        Transition<Place> endedComputation = new TransitionBuilder<Place>()
                .addInhibitor(Place.A)
                .addInhibitor(Place.B)
                .addReset(Place.Buffor)
                .addReset(Place.Empty)
                .build();
        Transition<Place> decreaseA = new TransitionBuilder<Place>()
                .addInput(Place.A, 1)
                .addInput(Place.Buffor, 1)
                .addInhibitor(Place.B)
                .addInhibitor(Place.Empty)
                .addOutput(Place.Empty, 1)
                .addOutput(Place.Buffor, 1)
                .build();
        Transition<Place> decreaseB = new TransitionBuilder<Place>()
                .addInput(Place.B, 1)
                .addInput(Place.A, 1)
                .addInhibitor(Place.Empty)
                .addOutput(Place.Buffor, 1)
                .addOutput(Place.Result, 1)
                .addOutput(Place.A, 1)
                .build();
        Transition<Place> moveBuffor = new TransitionBuilder<Place>()
                .addInput(Place.Buffor, 1)
                .addInput(Place.Empty, 1)
                .addInput(Place.A, 1)
                .addOutput(Place.B, 1)
                .addOutput(Place.A, 1)
                .addOutput(Place.Empty, 1)
                .build();
        Transition<Place> resetB = new TransitionBuilder<Place>()
                .addInhibitor(Place.Buffor)
                .addInput(Place.Empty, 1)
                .addInput(Place.B, 1)
                .addInput(Place.A, 1)
                .addOutput(Place.B, 1)
                .addOutput(Place.A, 1)
                .build();

        List<Transition<Place>> lista = new LinkedList<>();
        lista.add(zeroA);
        lista.add(zeroB);
        lista.add(decreaseA);
        lista.add(decreaseB);
        lista.add(moveBuffor);
        lista.add(resetB);

        Thread[] t = new Thread[4];
        for (int i = 0; i < 4; i++) {
            t[i] = new Thread(new Process(lista));
            t[i].start();
        }

        List<Transition<Place>> lista2 = new LinkedList<>(lista);
        lista2.add(endedComputation);
        Set<Map<Place, Integer>> res = petriNet.reachable(lista2);
        printStateSet(res);

        List<Transition<Place>> end = new LinkedList<>();
        end.add(endedComputation);
        try {
            petriNet.fire(end);
        } catch (InterruptedException e) {
            System.out.print(e);
        }

        try {
            System.out.println(petriNet.getToken(Place.Result));
        } catch (InterruptedException e) {

        }

        for (int i = 0; i < 4; i++) {
            t[i].interrupt();
        }
    }
}
