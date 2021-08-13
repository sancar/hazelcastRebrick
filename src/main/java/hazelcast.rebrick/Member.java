package hazelcast.rebrick;

import com.hazelcast.config.Config;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.io.File;

public class Member {

    public static void main(String[] args) {
        Config config = new Config();
        config.setLicenseKey("PLATFORM#99Nodes#S9DOJKjdfMqYBHZ1Q5NEwnlmTPCAb68Wg0iUku2GyX19911011099001901911001919100001092900111113");
        config.getJetConfig().setEnabled(true);
        IndexConfig indexConfig = new IndexConfig(IndexType.SORTED, "percentage");
        config.addMapConfig(new MapConfig("resultMap").addIndexConfig(indexConfig));
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true).addMember("127.0.0.1");
        // enable persistence
        //config.getPersistenceConfig().setEnabled(true).setBaseDir(new File("persistent_data")).;
        config.getHotRestartPersistenceConfig().setEnabled(true);
        config.getMapConfig("sets").getHotRestartConfig().setEnabled(true);
        HazelcastInstance server = Hazelcast.newHazelcastInstance(config);
    }
}
