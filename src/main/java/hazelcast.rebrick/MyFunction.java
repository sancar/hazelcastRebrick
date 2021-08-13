package hazelcast.rebrick;

import com.hazelcast.function.FunctionEx;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;

public class MyFunction implements FunctionEx<Map.Entry<Object, Object>, Object>, Serializable {

    private HashSet<LegoPart> allParts;

    public MyFunction(HashSet<LegoPart> allParts) {
        this.allParts = allParts;
    }

    public MyFunction() {
    }

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
}
