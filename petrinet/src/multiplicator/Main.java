package multiplicator;

import petrinet.*;

import java.util.*;

public class Main {

    private static final int THREADS = 4;

    private static PetriNet<Place> petriNet;

    private enum Place {
        A, B, Empty, Buffor, Result
    }

    private static class Process implements Runnable {
        private List <Transition<Place>> transitions;

        Process(List<Transition<Place>> transitions) {
            this.transitions = new LinkedList<>(transitions);
        }

        @Override
        public void run() {
            int count = 0;
            while (true) {
                try {
                    petriNet.fire(transitions);
                    count++;
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " fired transitions " + count + " times.");
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        // Create environment map for Petri net
        Map<Place, Integer> environment = new LinkedHashMap<>();
        // Get A and B from input and put it in net
        int A = input.nextInt();
        int B = input.nextInt();
        environment.put(Place.A, A);
        environment.put(Place.B, B);
        // Build Petri net
        petriNet = new PetriNet<>(environment, true);
        // If A-place is empty, B-place can be reseted (0 * B = 0, res + 0 * B = res)
        Transition<Place> zeroA = new TransitionBuilder<Place>()
                .addInput(Place.B, 1)
                .addInhibitor(Place.A)
                .addReset(Place.B)
                .build();
        // If B-blace is empty and Buffor is empty, A-place can be reseted (A * 0 = 0, res + A * 0 = res)
        Transition<Place> zeroB = new TransitionBuilder<Place>()
                .addInhibitor(Place.B)
                .addInhibitor(Place.Buffor)
                .addInput(Place.A, 1)
                .addReset(Place.A)
                .build();
        // If A-place and B-place is empty, then computation has been ended
        Transition<Place> endedComputation = new TransitionBuilder<Place>()
                .addInhibitor(Place.A)
                .addInhibitor(Place.B)
                .addReset(Place.Buffor)
                .addReset(Place.Empty)
                .build();
        // If B-place is empty and all their tokens are on Buffer-place,
        // tokens at A-place can be decreased and all tokens from Buffer have to
        // be moved to B-place
        Transition<Place> decreaseA = new TransitionBuilder<Place>()
                .addInput(Place.A, 1)
                .addInput(Place.Buffor, 1)
                .addInhibitor(Place.B)
                .addInhibitor(Place.Empty)
                .addOutput(Place.Empty, 1)
                .addOutput(Place.Buffor, 1)
                .build();
        // Increase result, move token to Buffer-place from B-place
        Transition<Place> decreaseB = new TransitionBuilder<Place>()
                .addInput(Place.B, 1)
                .addInput(Place.A, 1)
                .addInhibitor(Place.Empty)
                .addOutput(Place.Buffor, 1)
                .addOutput(Place.Result, 1)
                .addOutput(Place.A, 1)
                .build();
        // Move token from Buffor to B-place
        Transition<Place> moveBuffor = new TransitionBuilder<Place>()
                .addInput(Place.Buffor, 1)
                .addOutput(Place.B, 1)
                .addInput(Place.Empty, 1)
                .addOutput(Place.Empty, 1)
                .addInput(Place.A, 1)
                .addOutput(Place.A, 1)
                .build();
        // All tokens from Buffor had been moved to B-place
        Transition<Place> resetB = new TransitionBuilder<Place>()
                .addInput(Place.B, 1)
                .addOutput(Place.B, 1)
                .addInput(Place.Empty, 1)
                .addInhibitor(Place.Buffor)
                .addInput(Place.A, 1)
                .addOutput(Place.A, 1)
                .build();
        // Make collection with all transition
        List<Transition<Place>> transitions = new LinkedList<>();
        transitions.add(zeroA);
        transitions.add(zeroB);
        transitions.add(decreaseA);
        transitions.add(decreaseB);
        transitions.add(moveBuffor);
        transitions.add(resetB);
        // Make collection with final transition
        List<Transition<Place>> end = new LinkedList<>();
        end.add(endedComputation);
        // Run 4 threads
        Thread[] t = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            t[i] = new Thread(new Process(transitions));
        }
        for (int i = 0; i < THREADS; i++) {
            t[i].start();
        }
        // Fire final transition
        try {
            petriNet.fire(end);
        } catch (InterruptedException e) {
            System.err.println(e.toString());
        }
        System.out.println(petriNet.getToken(Place.Result));
        //
        for (int i = 0; i < THREADS; i++) {
            t[i].interrupt();
        }
    }
}
