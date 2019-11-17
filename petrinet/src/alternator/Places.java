package alternator;

import petrinet.Transition;
import petrinet.TransitionBuilder;
import java.util.LinkedList;
import java.util.List;

public class Places {

    /** Interface for places in Alternator */
    interface Place {
        String getName();
    }

    /** Singleton class for Mutex */
    static class Mutex implements Place {
        private static final Mutex instance = new Mutex();
        private Mutex() {
        }
        static Mutex get() {
            return instance;
        }
        @Override
        public String getName() {
            return "Mutex";
        }
    }

    /** Abstract class for thread's places */
    abstract static class ThreadSection implements Place {
        private String idThread;
        private String sectionType;

        ThreadSection(String id, String sectionType) {
            idThread = id;
            this.sectionType = sectionType;
        }

        @Override
        public String getName() {
            return sectionType + "_" + idThread;
        }
    }

    /** Critical section - allows writting. */
    static class CriticalSection extends ThreadSection {
        CriticalSection(String name) {
            super(name, "CriticalSection");
        }
    }

    /** Waiting section - for other thread get their critical section. */
    static class WaitingSection extends ThreadSection {
        WaitingSection(String name) {
            super(name, "WaitingSection");
        }
    }

    /** Ready section - able to get Mutex and go to Critical section. */
    static class ReadySection extends ThreadSection {
        ReadySection(String name) {
            super(name, "ReadySection");
        }
    }

    /** Utilities for tread- their places, transitions. */
    static class ThreadPack {
        final String idName;
        final CriticalSection criticalSection;
        final WaitingSection waitingSection;
        final ReadySection readySection;
        final Transition<Place> runCriticalSection;
        final List<Transition<Place>> transitions;

        private ThreadPack(String idName) {
            this.idName = idName;
            criticalSection = new CriticalSection(idName);
            this.readySection = new ReadySection(idName);
            waitingSection = new WaitingSection(idName);
            this.runCriticalSection = new TransitionBuilder<Place>()
                    .addInput(Mutex.get(), 1 )
                    .addInput(readySection, 1)
                    .addOutput(criticalSection, 1)
                    .build();
            Transition<Place> goReadySection = new TransitionBuilder<Place>()
                    .addInhibitor(Mutex.get())
                    .addInput(waitingSection, 1)
                    .addOutput(readySection, 1)
                    .build();
            Transition<Place> goWaitingSection = new TransitionBuilder<Place>()
                    .addInput(criticalSection, 1)
                    .addOutput(waitingSection, 1)
                    .addOutput(Mutex.get(), 1)
                    .build();
            transitions = new LinkedList<>();
            transitions.add(goReadySection);
            transitions.add(goWaitingSection);
            transitions.add(runCriticalSection);
        }

        /** Build all thread's places and transitions. */
        static ThreadPack makeThreadPack(String name) {
            return new ThreadPack(name);
        }
    }
}
