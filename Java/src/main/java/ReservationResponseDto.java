import infra.SeatDto;

import java.util.List;

public class ReservationResponseDto {
	public final String trainId;
    public final String bookingId;
    public final List<SeatDto> seats;

    public ReservationResponseDto(String trainId, List<SeatDto> seats, String bookingId) {
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
