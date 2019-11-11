package alternator;

public class Places {
    public static class Mutex implements Place {
        public Mutex() {
        }
    }

    public static class CriticalSection implements Place {
        public final String name;
        public CriticalSection(String name) {
            this.name = name;
        }
    }

    public static class LocalSection implements Place {
        public final String name;
        public LocalSection(String name) {
            this.name = name;
        }
    }
}
