package hazelcast.rebrick;

import java.io.Serializable;
import java.util.Objects;

public class LegoPart implements Serializable {

    private String part_num;
    private String color_id;
    private boolean is_spare;

    public LegoPart(String part_num, String color_id, boolean is_spare) {
        this.part_num = part_num;
        this.color_id = color_id;
        this.is_spare = is_spare;
    }

    public String getPart_num() {
        return part_num;
    }

    public String getColor_id() {
        return color_id;
    }

    public boolean isIs_spare() {
        return is_spare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegoPart legoPart = (LegoPart) o;
        return Objects.equals(part_num, legoPart.part_num) && Objects.equals(color_id, legoPart.color_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(part_num, color_id);
    }

    @Override
    public String toString() {
        return "LegoPart{" +
                "part_num='" + part_num + '\'' +
                ", color_id='" + color_id + '\'' +
                ", is_spare=" + is_spare +
                '}';
    }
}
