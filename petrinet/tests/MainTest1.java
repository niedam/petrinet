import petrinet.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainTest1 {

    public static void main(String[] args) throws Exception {

        //Nulls as parameters for transition
        Transition<Integer> t = new Transition<Integer>(null, null, null, null);
        PetriNet<Integer> pn = new PetriNet<Integer>(null, false);


        //Conflict between input and inhibitors
        Map<Integer, Integer> input = new HashMap<Integer, Integer>();
        input.put(1,1);
        input.put(2, 1);
        List<Integer> inhibitorConflict = new LinkedList<>();
        inhibitorConflict.add(1);
        inhibitorConflict.add(3);

        //Alright input and inhibitors but not enough tokens
        List<Integer> inhibitor = new LinkedList<>();
        inhibitor.add(3);
        inhibitor.add(4);
        Transition<Integer> t2 = new Transition<>(input,null, inhibitor, null);

        //Alright input and inhibitors and enough tokens
        Map<Integer,Integer> map_pn = new HashMap<>();
        map_pn.put(1, 4);
        map_pn.put(2, 5);

    }

}
