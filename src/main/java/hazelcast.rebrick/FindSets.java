package hazelcast.rebrick;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.function.FunctionEx;
import com.hazelcast.jet.JetService;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sink;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.map.IMap;
import com.hazelcast.sql.SqlResult;
import com.hazelcast.sql.SqlRow;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class FindSets {

    public static void main(String[] args) {

        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        client.getMap("resultMap").destroy();

        run(client, Arrays.asList("31046-1", "76069-1", "10243-1"));


    }

    public static void run(HazelcastInstance client, Collection<String> setNumbers) {
        JetService jetService = client.getJet();

        IMap<String, LegoSet> sets = client.getMap("sets");
        System.out.println("> 1 " + sets.size());
        LinkedList<LegoPart> allParts = new LinkedList<>();
        for (String setNumber : setNumbers) {
            LegoSet set = sets.get(setNumber);
            allParts.addAll(set.getParts());
        }

        System.out.println("PARTS WE HAVE");
        for (LegoPart allPart : allParts) {
            System.out.println(allPart);
        }

        Pipeline pipeline = Pipeline.create();
        pipeline.readFrom(Sources.map("sets")).map(new FunctionEx<Map.Entry<Object, Object>, Object>() {
            @Override
            public Object applyEx(Map.Entry<Object, Object> entry) throws Exception {
                String key = (String) entry.getKey();
                LegoSet value = (LegoSet) entry.getValue();
                Collection<LegoPart> parts = value.getParts();
                System.out.println(value);
                int neededSize = parts.size();
                for (LegoPart partWeHave : allParts) {
                    parts.remove(partWeHave);
                }
                int matchingSize = neededSize - parts.size();
                float percentage = (float) ((float) matchingSize * 100.0 / (float) neededSize);
                System.out.println(matchingSize + " " + neededSize + " " + percentage + " " + value);
                entry.setValue(new ResultLegoSet(value, percentage));
                return entry;

            }
        }).writeTo((Sink) Sinks.map("resultMap"));

        jetService.newJob(pipeline).join();

        IMap<Object, Object> resultMap = client.getMap("resultMap");
        System.out.println("Result map size " + resultMap.size());
        SqlResult sqlRows = client.getSql().execute("SELECT * FROM resultMap ORDER BY percentage DESC LIMIT 50");

        for (SqlRow sqlRow : sqlRows) {
            System.out.println(sqlRow.getObject("legoSet") + " " + sqlRow.getObject("percentage"));
        }
    }
}
