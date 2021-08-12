package hazelcast.rebrick;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

public class LegoSet implements Serializable {

    private String set_num;
    private String name;
    private int num_parts;
    private Collection<LegoPart> parts;

    public LegoSet(String set_num, String name, int num_parts, Collection<LegoPart> parts) {
        this.set_num = set_num;
        this.name = name;
        this.num_parts = num_parts;
        this.parts = parts;
    }

    public String getSet_num() {
        return set_num;
    }

    public String getName() {
        return name;
    }

    public int getNum_parts() {
        return num_parts;
    }

    public Collection<LegoPart> getParts() {
        return parts;
    }

    public void setParts(Collection<LegoPart> parts) {
        this.parts = parts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegoSet set = (LegoSet) o;
        return Objects.equals(set_num, set.set_num);
    }

    @Override
    public int hashCode() {
        return Objects.hash(set_num);
    }

    @Override
    public String toString() {
        return "LegoSet{" +
                "set_num='" + set_num + '\'' +
                ", name='" + name + '\'' +
                ", num_parts=" + num_parts +
                ", parts=" + parts.size() +
                '}';
    }
}
