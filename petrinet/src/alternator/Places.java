package alternator;

import petrinet.Transition;
import petrinet.TransitionBuilder;

import java.util.LinkedList;
import java.util.List;

public class Places {
    public interface Place {
    }

    public static class Mutex implements Place {
        private static final Mutex instance = new Mutex();
        private Mutex() {
        }
        public Mutex get() {
            return instance;
        }
    }

    public abstract static class ThreadSection implements Place {
        private String idThread;

        ThreadSection(String id) {
            idThread = id;
        }

        public String getName() {
            return idThread;
        }
    }

    public static class CriticalSection extends ThreadSection {
        public CriticalSection(String name) {
            super(name);
        }
    }

    public static class WaitingSection extends ThreadSection {
        public WaitingSection(String name) {
            super(name);
        }
    }

    public static class ReadySection extends ThreadSection {
        public ReadySection(String name) {
            super(name);
        }
    }

    public static class ThreadPack {
        private String idName;
        //private final CriticalSection criticalSection;
        //private final WaitingSection waitingSection;
        public final ReadySection readySection;
        public final Transition<Place> runCriticalSection;
        public final List<Transition<Place>> transitions;

        private ThreadPack(String idName) {
            this.idName = idName;
            CriticalSection criticalSection = new CriticalSection(this.idName);
            this.readySection = new ReadySection(this.idName);
            WaitingSection waitingSection = new WaitingSection(this.idName);
            this.runCriticalSection = new TransitionBuilder<>()
                    .addInput(Mutex.get(), 1 )
                    .addInput(readySection, 1)
                    .addOutput(criticalSection, 1)
                    .build();
            Transition<Place> goReadySection = new TransitionBuilder<>()
                    .addInhibitor(Mutex.get())
                    .addInput(waitingSection, 1)
                    .addOutput(readySection, 1)
                    .build();
            Transition<Place> goWaitingSection = new TransitionBuilder<>()
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
    }
}
