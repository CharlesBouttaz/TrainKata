import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TicketOfficeService {

    private TrainDataClient trainDataClient;
    private BookingReferenceClient bookingReferenceClient;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.trainDataClient = trainDataClient;
        this.bookingReferenceClient = bookingReferenceClient;
    }

    public String makeReservation(ReservationRequestDto request) {
        String trainData = trainDataClient.getTopology(request.trainId);

        List<Seat> seats = computeSeats(trainData, request.seatCount);

        if(seats.isEmpty()) return "{\"train_id\": \""+request.trainId+"\", \"booking_reference\": \"\", \"seats\": []}";

        ReservationResponseDto reservation = new ReservationResponseDto(request.trainId, seats, bookingReferenceClient.generateBookingReference());
        return "{" +
                "\"train_id\": \"" + reservation.trainId + "\", " +
                "\"booking_reference\": \"" + reservation.bookingId + "\", " +
                "\"seats\": [" + reservation.seats.stream().map(s -> "\"" + s.seatNumber + s.coach + "\"").collect(Collectors.joining(", ")) + "]" +
                "}";
    }

    private List<Seat> computeSeats(String trainData, int seatCount) {
        Topologie itemWithOwner = new Gson().fromJson(trainData, Topologie.class);

        return itemWithOwner.seats.values().stream()
                .collect(Collectors.groupingBy(topologieSeat -> topologieSeat.coach)).entrySet().stream()
                .filter(coach -> coach.getValue().stream()
                        .filter(s -> "".equals(s.booking_reference))
                        .count() >= seatCount)
                .findFirst()
                .map(coach -> coach.getValue().stream().filter(s -> "".equals(s.booking_reference)).limit(seatCount).map(s -> new Seat(s.coach, s.seat_number)).collect(Collectors.toList()))
                .orElse(new ArrayList<Seat>());
    }
}