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
    }
}
