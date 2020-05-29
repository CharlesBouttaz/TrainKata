package domain.model;

import java.util.List;

public class Coach {
    private String coachId;
    private List<Seat> seats;

    public String getCoachId() {
        return coachId;
    }

    public Coach(String coachId, List<Seat> seats) {
        this.coachId = coachId;
        this.seats = seats;
    }

    // TODO CBO: 29/05/2020 70% rule
    public boolean canHandle(int seatCount) {
        return seats.stream().filter(Seat::isAvailable).count() >= seatCount;
    }
}
