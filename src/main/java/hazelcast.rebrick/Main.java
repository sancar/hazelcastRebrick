package hazelcast.rebrick;

import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
        LinkedList<Object> objects = new LinkedList<>();
        objects.add(new LegoPart("13212", "321", false));
        objects.add(new LegoPart("13212", "322", false));
        objects.add(new LegoPart("13212", "323", false));

        System.out.println(
                objects.contains(new LegoPart("13212", "321", false) ));


        objects.remove(new LegoPart("13212", "321", false) );

        System.out.println(objects.size());
    }
}
