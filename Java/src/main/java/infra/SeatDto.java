package infra;

import domain.SeatId;

public class SeatDto {
    public final String coach;
    public final int seatNumber;

    public SeatDto(String coach, int seatNumber) {
        this.coach = coach;
        this.seatNumber = seatNumber;
    }
    public SeatDto(SeatId seatId) {
        this.coach = seatId.coachNumber;
        this.seatNumber = seatId.seatNumber;
    }

    public boolean equals(Object o) {
        SeatDto other = (SeatDto)o;
        return coach==other.coach && seatNumber==other.seatNumber;
    }

    @Override
    public String toString() {
        return this.coach + this.seatNumber;
    }
}
