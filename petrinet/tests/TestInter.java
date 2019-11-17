/**
 * Test by Paweł Czajka
 */

import petrinet.*;
import java.util.*;
import java.util.concurrent.*;

public class TestInter {

    private static Collection<Transition<Integer>> create_transition(int origin,
                                                                     int destination) {
        HashSet<Transition<Integer>> result = new HashSet<>();
        Map<Integer, Integer> input = new HashMap<>();
        Map<Integer, Integer> output = new HashMap<>();

        input.put(origin, 1);
        output.put(destination, 1);
        result.add(new Transition<>(input, new HashSet<>(), new HashSet<>(), output));
        return result;
    }

    public static class Worker implements Runnable {
        private Collection<Transition<Integer>> task;
        private PetriNet<Integer> net;
        private String nazwa;
        public Worker(int origin, int destination, PetriNet<Integer> net, String nazwa) {
            task = create_transition(origin, destination);
            this.net = net;
            this.nazwa = nazwa;
        }

        @Override
        public void run() {
            try {
                System.out.println("zaczynam " + nazwa);
                net.fire(task);
                System.out.println("koncze normalnie " + nazwa);

            } catch (InterruptedException e) {
                System.out.println("poszlo interruption skonczylem " + nazwa);
            }
        }
    }

    public static void main(String[] args) {
        HashMap<Integer, Integer> initial = new HashMap<>();
        initial.put(0, 1);
        PetriNet<Integer> net = new PetriNet<>(initial, false);

        Thread do_interruptowania = new Thread(new Worker(1, 2, net, "do_interruptowania"));
        do_interruptowania.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new IllegalArgumentException();
        }

        do_interruptowania.interrupt();
        try {
            do_interruptowania.join();
        } catch (InterruptedException e) {
            throw new IllegalArgumentException();
        }

        Thread normalna = new Thread(new Worker(0, 1, net, "normalna"));
        normalna.start();

        try {
            normalna.join();
        } catch (InterruptedException e) {
            throw new IllegalArgumentException();
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new IllegalArgumentException();
        }

        /*
        // non standard interface function, mozna sobie wykomentowac do
        Map<Integer, Integer> state = net.give_current_state_interface();

        System.out.println("state");
        for(int k : state.keySet()) {
            System.out.println("miejsce " + k + " liczba tokenow " + state.get(k));
        }
        // tego miejsca
        */

        Thread kaczaca = new Thread(new Worker(1, 2, net, "kaczaca"));
        kaczaca.start();

        try {
            kaczaca.join();
        } catch (InterruptedException e) {
            throw new IllegalArgumentException();
        }

        System.out.println("\n\n\n przeszlo test");
    }
}