package hazelcast.rebrick;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class LoadData {

    static String COMMA_INSIDE = ",(?=[^\"]*\")";

    public static void main(String[] args) throws IOException {
        Map<String, String> inventoryIdToSetNum = loadInventories();
        Map<String, LegoSet> setNumToSet = loadLegoSets();
        Map<String, HashSet<LegoPart>> setNumToParts = loadParts(inventoryIdToSetNum);

        HazelcastInstance client = HazelcastClient.newHazelcastClient();

        IMap<String, LegoSet> map = client.getMap("sets");

        for (Map.Entry<String, HashSet<LegoPart>> entry : setNumToParts.entrySet()) {
            LegoSet legoSet = setNumToSet.get(entry.getKey());
            legoSet.setParts(entry.getValue());
            map.put(entry.getKey(), legoSet);
        }

        System.out.println(map.size());
        client.shutdown();
    }

    private static void verification(Map<String, LegoSet> setNumToSet, Map<String, Collection<LegoPart>> setNumToParts) {
        int falseP = 0;
        for (Map.Entry<String, Collection<LegoPart>> entry : setNumToParts.entrySet()) {
            String setNum = entry.getKey();
            Collection<LegoPart> legoParts = entry.getValue();

            int size = 0;
            for (LegoPart legoPart : legoParts) {
                if (!legoPart.isIs_spare()) {
                    size++;
                }
            }
            LegoSet legoSet = setNumToSet.get(setNum);
            if (legoSet.getNum_parts() != size) {
//                System.out.println(legoSet + " != " + size);
                falseP++;
            } else {
//                System.out.println("EQUAL");
            }
        }
        System.out.println(falseP + " " + setNumToParts.size());
    }

    private static Map<String, HashSet<LegoPart>> loadParts(Map<String, String> inventoryIdToSetNum) throws IOException {
        HashMap<String, HashSet<LegoPart>> setNumToParts = new HashMap<>();
        InputStream resourceAsStream = LoadData.class.getResourceAsStream("/inventory_parts.csv");
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(resourceAsStream));

        String row;
        inputStream.readLine();
        while ((row = inputStream.readLine()) != null) {
            String[] data = row.split(",");
            String setNum = inventoryIdToSetNum.get(data[0]);
            HashSet<LegoPart> legoParts = setNumToParts.computeIfAbsent(setNum, s -> new HashSet<>());
            for (int i = 0; i < Integer.parseInt(data[3]); i++) {
                legoParts.add(new LegoPart(data[1], data[2], !data[4].equals("f")));
            }

        }
        inputStream.close();
        return setNumToParts;
    }

    private static Map<String, LegoSet> loadLegoSets() throws IOException {
        HashMap<String, LegoSet> map = new HashMap<>();

        InputStream resourceAsStream = LoadData.class.getResourceAsStream("/sets.csv");
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(resourceAsStream));

        String row;
        inputStream.readLine();
        while ((row = inputStream.readLine()) != null) {
            String[] data = row.split(",");
            if (data[1].startsWith("\"")) {
                row = row.replaceAll(COMMA_INSIDE, " OR");
                row = row.replaceAll(" OR\"", ",");
            }
            data = row.split(",");
            if (data.length != 5) {
                throw new IllegalStateException(row);
            }
            //System.out.println(data[0] + " | " + data[1] + " | " + data[4]);
            if (map.put(data[0], new LegoSet(data[0], data[1], Integer.parseInt(data[4]), null)) != null) {
                System.out.println("========= Duplicate content " + data[0] + "=========");
            }

        }
        inputStream.close();
        return map;
    }

    private static Map<String, String> loadInventories() throws IOException {
        HashMap<String, String> map = new HashMap<>();
        InputStream resourceAsStream = LoadData.class.getResourceAsStream("/inventories.csv");
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(resourceAsStream));

        String row;
        inputStream.readLine();
        while ((row = inputStream.readLine()) != null) {
            String[] data = row.split(",");
            map.put(data[0], data[2]);
        }
        inputStream.close();
        return map;
    }


    private static void printContent(String file) throws IOException {
        HashSet<String> strings = new HashSet<>();
        InputStream resourceAsStream = LoadData.class.getResourceAsStream(file);
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(resourceAsStream));
        String row = inputStream.readLine();
        while ((row = inputStream.readLine()) != null) {
            String[] data = row.split(",");
            if (!strings.add(data[0])) {
                System.out.println("========= Duplicate content " + data[0] + "=========");
            }
            for (String datum : data) {
                System.out.print(datum);
                System.out.print(" ");
            }
            System.out.println();
        }
        inputStream.close();
    }
}
