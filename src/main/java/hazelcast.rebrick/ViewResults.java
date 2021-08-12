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

public class ViewResults {

    public static void main(String[] args) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();


        IMap<Object, Object> resultMap = client.getMap("resultMap");
        System.out.println("Result map size " + resultMap.size());
        SqlResult sqlRows = client.getSql().execute("SELECT * FROM resultMap ORDER BY percentage DESC LIMIT 50");

        for (SqlRow sqlRow : sqlRows) {
            System.out.println(sqlRow.getObject("legoSet") + " " + sqlRow.getObject("percentage"));
        }
    }
}
