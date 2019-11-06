package infra;

import domain.Seat;

import java.util.List;

public interface BookingReferenceClient {
    String generateBookingReference();

    void bookTrain(String trainId, String bookingReference, List<SeatDto> seats);
}
