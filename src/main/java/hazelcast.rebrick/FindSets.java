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

import java.util.*;

public class FindSets {

    public static void main(String[] args) {

        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        client.getMap("resultMap").destroy();

        int numIterations = 2;
        for (int i = 0; i < numIterations; i++) {
            run(client, Arrays.asList("31046-1", "76069-1", "10243-1"));
        }

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numIterations; i++) {
            run(client, Arrays.asList("31046-1", "76069-1", "10243-1"));
        }
        System.out.println("Took " + (System.currentTimeMillis() - startTime)/numIterations + " msecs on average");

        client.shutdown();
    }

    public static void run(HazelcastInstance client, Collection<String> setNumbers) {
        JetService jetService = client.getJet();

        IMap<String, LegoSet> sets = client.getMap("sets");
        System.out.println("> 1 " + sets.size());
        HashSet<LegoPart> allParts = new HashSet<>();

        for (String setNumber : setNumbers) {
            LegoSet set = sets.get(setNumber);
            allParts.addAll(set.getParts());
        }

        Pipeline pipeline = Pipeline.create();
        pipeline.readFrom(Sources.map("sets")).map(new FunctionEx<Map.Entry<Object, Object>, Object>() {
            @Override
            public Object applyEx(Map.Entry<Object, Object> entry) throws Exception {
                String key = (String) entry.getKey();
                LegoSet value = (LegoSet) entry.getValue();
                HashSet<LegoPart> parts = new HashSet<>(value.getParts());

                int neededSize = parts.size();
                parts.removeAll(allParts);
                int matchingSize = neededSize - parts.size();
                float percentage = (float) ((float) matchingSize * 100.0 / (float) neededSize);
                return new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), new ResultLegoSet(value, percentage));
            }
        }).writeTo((Sink) Sinks.map("resultMap"));

        jetService.newLightJob(pipeline).join();

        IMap<Object, Object> resultMap = client.getMap("resultMap");
        System.out.println("Result map size " + resultMap.size());
        SqlResult sqlRows = client.getSql().execute("SELECT * FROM resultMap ORDER BY percentage DESC LIMIT 50");

        for (SqlRow sqlRow : sqlRows) {
            System.out.println(sqlRow.getObject("legoSet") + " " + sqlRow.getObject("percentage"));
        }
    }
}
