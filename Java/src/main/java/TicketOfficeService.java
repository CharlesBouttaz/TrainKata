import com.google.gson.Gson;
import domain.*;
import domain.portin.MakeReservation;
import infra.BookingReferenceClient;
import infra.SeatDto;
import infra.TrainDataClient;

import java.util.*;
import java.util.stream.Collectors;

public class TicketOfficeService {

    private final MakeReservation makeReservation;
    private TrainDataClient trainDataClient;
    private BookingReferenceClient bookingReferenceClient;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.trainDataClient = trainDataClient;
        this.bookingReferenceClient = bookingReferenceClient;
        this.makeReservation = new MakeReservation();
    }

    public String makeReservation(ReservationRequestDto request) {
        String trainTopology = trainDataClient.getTopology(request.trainId);

        Map<String, List<TopologieDto.TopologieSeat>> seatsByCoaches = new HashMap<>();
        for (TopologieDto.TopologieSeat seat : new Gson().fromJson(trainTopology, TopologieDto.class).seats.values()) {
            seatsByCoaches.computeIfAbsent(seat.coach, k -> new ArrayList<>()).add(seat);
        }

        ReservationRequest reservationRequest = new ReservationRequest(request.seatCount, new TrainId(request.trainId));
        var availableSeatsByCoaches = getAvailableCoaches(seatsByCoaches, reservationRequest);

        var bookedSeats = makeReservation.getAvailableSeats(reservationRequest, availableSeatsByCoaches);

        if (bookedSeats.isPresent()) {
            String bookingId = bookingReferenceClient.generateBookingReference();
            ReservationResponseDto reservation = new ReservationResponseDto(request.trainId, bookedSeats.get().stream().map(s -> new SeatDto(s)).collect(Collectors.toList()), bookingId);
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

    private Optional<Coach> getAvailableCoaches(
            Map<String, List<TopologieDto.TopologieSeat>> seatsByCoaches, ReservationRequest reservationRequest) {

        List<Coach> coaches = seatsByCoaches.entrySet().stream()
                .map(coach -> new Coach(coach.getValue().stream()
                        .map(seat -> new Seat(new SeatId(seat.seat_number, seat.coach), isSeatAvailable(seat))).collect(Collectors.toList()))).collect(Collectors.toList());
        Topology topology = new Topology(coaches);

        return makeReservation.getAvailableCoach(reservationRequest, topology);
    }



    private boolean isSeatAvailable(TopologieDto.TopologieSeat seat) {
        return "".equals(seat.booking_reference);
    }
}
