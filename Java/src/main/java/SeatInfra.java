
public class SeatInfra {
    public final String coach;
    public final int seatNumber;

    public SeatInfra(String coach, int seatNumber) {
        this.coach = coach;
        this.seatNumber = seatNumber;
    }

    public boolean equals(Object o) {
        SeatInfra other = (SeatInfra)o;
        return coach==other.coach && seatNumber==other.seatNumber;
    }
}
