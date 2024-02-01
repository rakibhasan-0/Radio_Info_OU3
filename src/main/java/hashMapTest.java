import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class hashMapTest {

    // Java program to remove each key value pair
// by iterating over the Hashmap
        public static void main(String[] args)
        {
            HashMap<String, Integer> gfg = new HashMap<>();

            // adding values in hashMap 1
            gfg.put("DSA", 100);
            gfg.put("Problem Solving", 100);
            gfg.put("Development", 99);
            gfg.put("Interviews", 99);
            gfg.put("Competitive Programming", 97);
            gfg.put("FANG", 99);

            // printing the size and elements
            System.out.println("-------before removing------");
            System.out.println(gfg);
            System.out.println(gfg.size());

            gfg.put("DSA", 233); //now DSA updated wiht new value


            System.out.println("--------After removing-------");
            System.out.println(gfg);
            System.out.println(gfg.size());
        }
    }


