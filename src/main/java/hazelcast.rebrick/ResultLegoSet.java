package hazelcast.rebrick;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

public class ResultLegoSet implements Serializable {

    private LegoSet legoSet;
    private float percentage;

    public ResultLegoSet(LegoSet legoSet, float percentage) {
        this.legoSet = legoSet;
        this.percentage = percentage;
    }

    public LegoSet getLegoSet() {
        return legoSet;
    }

    public float getPercentage() {
        return percentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultLegoSet that = (ResultLegoSet) o;
        return Float.compare(that.percentage, percentage) == 0 && Objects.equals(legoSet, that.legoSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(legoSet, percentage);
    }

    @Override
    public String toString() {
        return "ResultLegoSet{" +
                "legoSet=" + legoSet +
                ", percentage=" + percentage +
                '}';
    }
}
