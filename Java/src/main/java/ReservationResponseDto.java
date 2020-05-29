import java.util.List;

public class ReservationResponseDto {
	public final String trainId;
    public final String bookingId;
    public final List<SeatInfra> seats;

    public ReservationResponseDto(String trainId, List<SeatInfra> seats, String bookingId) {
		this.trainId = trainId;
        this.bookingId = bookingId;
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "ReservationResponseDto{" +
                "trainId='" + trainId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", seats=" + seats +
                '}';
    }
}
