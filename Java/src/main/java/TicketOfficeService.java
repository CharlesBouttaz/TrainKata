import com.google.gson.Gson;
import domain.AllTrains;
import domain.model.Seat;
import domain.model.Train;
import infra.out.Topologie;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO CBO: 29/05/2020 no package
// TODO CBO: 29/05/2020 rename according to domain
// TODO CBO: 29/05/2020 Mixed concerns / SRP
// TODO CBO: 29/05/2020 primitive obsession
// TODO CBO: 29/05/2020 tight coupling

public class TicketOfficeService {

    private BookingReferenceClient bookingReferenceClient;
    private AllTrains allTrains;

    public TicketOfficeService(AllTrains allTrains, BookingReferenceClient bookingReferenceClient) {
        this.bookingReferenceClient = bookingReferenceClient;
        this.allTrains = allTrains;
    }

    public String makeReservation(ReservationRequestDto request) {
        Train train = allTrains.findWith(request.trainId);

        List<Seat> seatsToBook = train.findSeatsForBooking(request.seatCount);

        List<SeatInfra> seats = computeAvailableSeats(trainData, request.seatCount);

        if(seats.isEmpty()) return "{\"train_id\": \""+request.trainId+"\", \"booking_reference\": \"\", \"seats\": []}";

        ReservationResponseDto reservation = new ReservationResponseDto(request.trainId, seats, bookingReferenceClient.generateBookingReference());
        return "{" +
                "\"train_id\": \"" + reservation.trainId + "\", " +
                "\"booking_reference\": \"" + reservation.bookingId + "\", " +
                "\"seats\": [" + reservation.seats.stream().map(s -> "\"" + s.seatNumber + s.coach + "\"").collect(Collectors.joining(", ")) + "]" +
                "}";
    }

    private List<SeatInfra> computeAvailableSeats(String trainData, int seatCount) {
        Topologie itemWithOwner = new Gson().fromJson(trainData, Topologie.class);

        return itemWithOwner.seats.values().stream()
                .collect(Collectors.groupingBy(topologieSeat -> topologieSeat.coach)).entrySet().stream()
                .filter(coach -> coach.getValue().stream()
                        .filter(s -> "".equals(s.booking_reference))
                        .count() >= seatCount)
                .findFirst()
                .map(coach -> coach.getValue().stream().filter(s -> "".equals(s.booking_reference)).limit(seatCount).map(s -> new SeatInfra(s.coach, s.seat_number)).collect(Collectors.toList()))
                .orElse(new ArrayList<SeatInfra>());
    }
}
