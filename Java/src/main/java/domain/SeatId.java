package domain;

import java.util.Objects;

public class SeatId {
    public final int seatNumber;
    public final String coachNumber;

    public SeatId(int seatNumber, String coachNumber) {
        this.seatNumber = seatNumber;
        this.coachNumber = coachNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatId that = (SeatId) o;
        return seatNumber == that.seatNumber &&
                Objects.equals(coachNumber, that.coachNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatNumber, coachNumber);
    }

    @Override
    public String toString() {
        return seatNumber + coachNumber;
    }
}
