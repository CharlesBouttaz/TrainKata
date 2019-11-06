package domain;

import java.util.Objects;

public class TrainId {
    public final String id;

    public TrainId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainId trainId = (TrainId) o;
        return Objects.equals(id, trainId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
