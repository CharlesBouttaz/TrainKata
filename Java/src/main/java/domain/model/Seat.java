package domain.model;

public class Seat {

    private String seatId;

    private Boolean available;

    public Seat(String seatId, Boolean available) {
        this.seatId = seatId;
        this.available = available;
    }

    boolean isAvailable() {
        return available;
    }
}
