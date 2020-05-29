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
}
