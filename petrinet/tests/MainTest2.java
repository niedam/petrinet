import petrinet.PetriNet;
import petrinet.Transition;

import java.util.*;

public class MainTest2 {



    public static void main(String[] args) throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put("Cat",7);
        map.put("Dog", 5);

        PetriNet<String> petnet = new PetriNet<>(map, true);

        Set<Map<String, Integer>> r1 = petnet.reachable(null);
        if (!(r1.size() == 1 && r1.contains(map))) {
            throw new Exception();
        }

        var lt1 = new LinkedList<Transition<String>>();
        var input1 = new HashMap<String, Integer>();
        input1.put("Cat", 2);
        lt1.add(new Transition<>(input1, null, null, null));
        var r2 = petnet.reachable(lt1);
        if (!(r2.size() == 4)) {
            throw new Exception();
        }
    }

}
