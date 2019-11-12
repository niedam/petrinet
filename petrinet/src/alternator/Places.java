package alternator;

import petrinet.Transition;
import petrinet.TransitionBuilder;

import java.util.LinkedList;
import java.util.List;

public class Places {
    public interface Place {
        public String getName();
    }

    public static class Mutex implements Place {
        private static final Mutex instance = new Mutex();
        private Mutex() {
        }
        public static Mutex get() {
            return instance;
        }
        @Override
        public String getName() {
            return "Mutex";
        }
    }

    public abstract static class ThreadSection implements Place {
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

    public static class CriticalSection extends ThreadSection {
        public CriticalSection(String name) {
            super(name, "CriticalSection");
        }
    }

    public static class WaitingSection extends ThreadSection {
        public WaitingSection(String name) {
            super(name, "WaitingSection");
        }
    }

    public static class ReadySection extends ThreadSection {
        public ReadySection(String name) {
            super(name, "ReadySection");
        }
    }

    public static class ThreadPack {
        public final CriticalSection criticalSection;
        public final WaitingSection waitingSection;
        public final ReadySection readySection;
        public final Transition<Place> runCriticalSection;
        public final List<Transition<Place>> transitions;

        private ThreadPack(String idName) {
            //CriticalSection criticalSection = new CriticalSection(idName);
            criticalSection = new CriticalSection(idName);
            this.readySection = new ReadySection(idName);
            //WaitingSection waitingSection = new WaitingSection(idName);
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

        public static ThreadPack makeThreadPack(String name) {
            return new ThreadPack(name);
        }

        public void addTransition(Transition<Place> transition) {
            transitions.add(transition);
            return;
        }
    }
}
