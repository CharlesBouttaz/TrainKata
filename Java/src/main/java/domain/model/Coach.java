package domain.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private boolean canHandleBooking(int seatCount) {
        return seats.stream().filter(Seat::isAvailable).count() >= seatCount;
    }

    public Optional<List<Seat>> getSeatsForBooking(int seatCount) {
        if (canHandleBooking(seatCount)) {
            return Optional.of(seats.stream().filter(Seat::isAvailable).limit(seatCount).collect(Collectors.toList()));
        } else {
            return Optional.empty();
        }
    }
}
