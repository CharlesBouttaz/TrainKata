package domain.model;

import java.util.List;
import java.util.Optional;

public class Train {
    private String trainId;
    private List<Coach> coaches;

    public Train(String trainId, List<Coach> coaches) {
        this.trainId = trainId;
        this.coaches = coaches;
    }

    public String getTrainId() {
        return trainId;
    }

    public Optional<List<Seat>> findSeatsForBooking(int seatCount){

        return coaches.stream().map(coach -> coach.getSeatsForBooking(seatCount))
                .findFirst().get();

        /*return itemWithOwner.seats.values().stream()
                .collect(Collectors.groupingBy(topologieSeat -> topologieSeat.coach)).entrySet().stream()
                .filter(coach -> coach.getValue().stream()
                        .filter(s -> "".equals(s.booking_reference))
                        .count() >= seatCount)
                .findFirst()
                .map(coach -> coach.getValue().stream().filter(s -> "".equals(s.booking_reference)).limit(seatCount).map(s -> new SeatInfra(s.coach, s.seat_number)).collect(Collectors.toList()))
                .orElse(new ArrayList<SeatInfra>());*/
    }
}
