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

        var availableSeatsByCoaches = getAvailableCoaches(request, seatsByCoaches);

        var bookedSeats = getAvailableSeats(request, availableSeatsByCoaches);

        if (bookedSeats.isPresent()) {
            ReservationResponseDto reservation = new ReservationResponseDto(request.trainId, bookedSeats.get(), bookingReferenceClient.generateBookingReference());
            this.bookingReferenceClient.bookTrain(reservation.trainId, reservation.bookingId, reservation.seats);
            return booking(reservation);
        } else {
            return noBooking(request);
        }
    }

    private String booking(ReservationResponseDto reservation) {
        return "{" +
                "\"train_id\": \"" + reservation.trainId + "\", " +
                "\"booking_reference\": \"" + reservation.bookingId + "\", " +
                "\"seats\": [" + reservation.seats.stream().map(s -> "\"" + s.seatNumber + s.coach + "\"").collect(Collectors.joining(", ")) + "]" +
                "}";
    }

    private String noBooking(ReservationRequestDto request) {
        return "{\"train_id\": \"" + request.trainId + "\", \"booking_reference\": \"\", \"seats\": []}";
    }

    private Optional<List<Seat>> getAvailableSeats(ReservationRequestDto request, Optional<Map.Entry<String, List<Topologie.TopologieSeat>>> availableSeatsByCoaches) {
        return availableSeatsByCoaches
                .map(
                        seatsByCoaches -> seatsByCoaches.getValue().stream()
                                .filter(this::isSeatAvailable)
                                .limit(request.seatCount)
                                .map(topologieSeat -> new Seat(topologieSeat.coach, topologieSeat.seat_number))
                                .collect(Collectors.toList())
                );

    }

    private Optional<Map.Entry<String, List<Topologie.TopologieSeat>>> getAvailableCoaches(ReservationRequestDto request, Map<String, List<Topologie.TopologieSeat>> seatsByCoaches) {

        return seatsByCoaches.entrySet().stream()
                .filter(coach -> isPossibleToBookCoach(request, coach))
                .findFirst();
    }

    private long countAvailableSeats(Map.Entry<String, List<Topologie.TopologieSeat>> coach) {
        return coach.getValue().stream()
                .filter(this::isSeatAvailable)
                .count();
    }

    private boolean isPossibleToBookCoach(ReservationRequestDto request, Map.Entry<String, List<Topologie.TopologieSeat>> coach) {
        return countAvailableSeats(coach) >= request.seatCount;
    }

    private boolean isSeatAvailable(Topologie.TopologieSeat seat) {
        return "".equals(seat.booking_reference);
    }
}
