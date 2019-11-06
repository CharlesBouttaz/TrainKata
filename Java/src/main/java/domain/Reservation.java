package domain;

import java.util.List;
import java.util.Objects;

public class Reservation {
    public final TrainId trainId;
    public final String reservationNumber;
    public final List<SeatId> bookedSeats;

    public Reservation(TrainId trainId, String reservationNumber, List<SeatId> bookedSeats) {
        this.trainId = trainId;
        this.reservationNumber = reservationNumber;
        this.bookedSeats = bookedSeats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(trainId, that.trainId) &&
                Objects.equals(reservationNumber, that.reservationNumber) &&
                Objects.equals(bookedSeats, that.bookedSeats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainId, reservationNumber, bookedSeats);
    }
}
