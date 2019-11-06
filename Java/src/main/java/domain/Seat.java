package domain;

import java.util.Objects;

public class Seat {
    public final SeatId seatId;
    public final boolean available;

    public Seat(SeatId seatId, boolean available) {
        this.seatId = seatId;
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return available == seat.available &&
                Objects.equals(seatId, seat.seatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatId, available);
    }
}
