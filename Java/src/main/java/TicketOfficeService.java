import com.google.gson.Gson;

import java.util.*;
import java.util.stream.Collectors;

public class TicketOfficeService {

    private TrainDataClient trainDataClient;
    private BookingReferenceClient bookingReferenceClient;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.trainDataClient = trainDataClient;
        this.bookingReferenceClient = bookingReferenceClient;
    }

    public String makeReservation(ReservationRequestDto request) {
        String trainTopology = trainDataClient.getTopology(request.trainId);

        Map<String, List<Topologie.TopologieSeat>> seatsByCoaches = new HashMap<>();
        for (Topologie.TopologieSeat seat : new Gson().fromJson(trainTopology, Topologie.class).seats.values()) {
            seatsByCoaches.computeIfAbsent(seat.coach, k -> new ArrayList<>()).add(seat);
        }
        Map.Entry<String, List<Topologie.TopologieSeat>> availableSeatsByCoaches = null;
        for (Map.Entry<String, List<Topologie.TopologieSeat>> coach : seatsByCoaches.entrySet()) {
            long availableSeats = 0L;
            for (Topologie.TopologieSeat seat : coach.getValue()) {
                if ("".equals(seat.booking_reference)) {
                    availableSeats++;
                }
            }
            if (availableSeats >= request.seatCount) {
                availableSeatsByCoaches = coach;
                break;
            }
        }
        List<Seat> seats = new ArrayList<>();
        if(availableSeatsByCoaches != null) {
            long limit = request.seatCount;
            for (Topologie.TopologieSeat seat1 : availableSeatsByCoaches.getValue()) {
                if ("".equals(seat1.booking_reference)) {
                    if (limit-- == 0) break;
                    Seat seat = new Seat(seat1.coach, seat1.seat_number);
                    seats.add(seat);
                }
            }
        }

        if (!seats.isEmpty()) {
            ReservationResponseDto reservation = new ReservationResponseDto(request.trainId, seats, bookingReferenceClient.generateBookingReference());
            this.bookingReferenceClient.bookTrain(reservation.trainId, reservation.bookingId, reservation.seats);
            return "{" +
                    "\"train_id\": \"" + reservation.trainId + "\", " +
                    "\"booking_reference\": \"" + reservation.bookingId + "\", " +
                    "\"seats\": [" + reservation.seats.stream().map(s -> "\"" + s.seatNumber + s.coach + "\"").collect(Collectors.joining(", ")) + "]" +
                    "}";
        } else {
            return "{\"train_id\": \"" + request.trainId + "\", \"booking_reference\": \"\", \"seats\": []}";
        }
    }
}
