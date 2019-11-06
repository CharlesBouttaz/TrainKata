package domain;

import java.util.List;

public class Coach {
    public final List<Seat> seats;

    public Coach(List<Seat> seats) {
        this.seats = seats;
    }
}
