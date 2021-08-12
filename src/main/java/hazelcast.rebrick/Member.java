package hazelcast.rebrick;

import com.hazelcast.config.Config;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;

public class Member {

    public static void main(String[] args) {
        Config config = new Config();
        config.getJetConfig().setEnabled(true);
        IndexConfig indexConfig = new IndexConfig(IndexType.SORTED, "percentage");
        config.addMapConfig(new MapConfig("resultMap").addIndexConfig(indexConfig));
        Hazelcast.newHazelcastInstance(config);
    }
}
