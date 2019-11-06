package domain;

import java.util.Objects;

public class ReservationRequest {
    public final int nbSeats;
    public final TrainId trainId;

    public ReservationRequest(int nbSeats, TrainId trainId) {
        this.nbSeats = nbSeats;
        this.trainId = trainId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationRequest that = (ReservationRequest) o;
        return nbSeats == that.nbSeats &&
                Objects.equals(trainId, that.trainId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nbSeats, trainId);
    }
}
