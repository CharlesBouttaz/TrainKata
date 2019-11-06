package domain.portin;

import domain.*;
import domain.portout.BookTrain;
import domain.portout.GetBookingReference;
import domain.portout.GetTrainTopology;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MakeReservation {

    BookTrain bookTrain;
    GetBookingReference getBookingReference;
    GetTrainTopology getTrainTopology;

    public MakeReservation(BookTrain bookTrain, GetBookingReference getBookingReference, GetTrainTopology getTrainTopology) {
        this.bookTrain = bookTrain;
        this.getBookingReference = getBookingReference;
        this.getTrainTopology = getTrainTopology;
    }

    public Optional<Object> execute(ReservationRequest reservationRequest) {
        var topology = getTrainTopology.get(reservationRequest.trainId);
        Optional<Coach> availableCoach = getAvailableCoach(reservationRequest, topology);
        Optional<List<SeatId>> availableSeats = getAvailableSeats(reservationRequest, availableCoach);
        if (availableSeats.isPresent()) {
            Reservation reservation = new Reservation(reservationRequest.trainId,
                    getBookingReference.get(),
                    availableSeats.get());

            bookTrain.book(reservation);
            return Optional.of(reservation);
        }
        return Optional.empty();
    }

    public Optional<Coach> getAvailableCoach(ReservationRequest reservationRequest, Topology topology) {
        return topology.coaches.stream()
                .filter(coach -> isPossibleToBookCoach(reservationRequest, coach))
                .findFirst();
    }

    // TODO CBO: 06/11/2019 move to Topology
    private boolean isPossibleToBookCoach(ReservationRequest request, Coach coach) {
        return countAvailableSeats(coach) >= request.nbSeats;
    }

    private long countAvailableSeats(Coach coach) {
        return coach.seats.stream()
                .filter(seat -> seat.available)
                .count();
    }

    public Optional<List<SeatId>> getAvailableSeats(ReservationRequest request, Optional<Coach> availableSeatsByCoaches) {
        return availableSeatsByCoaches
                .map(
                        seatsByCoaches -> seatsByCoaches.seats.stream()
                                .filter(seat -> seat.available)
                                .limit(request.nbSeats)
                                .map(topologieSeat -> topologieSeat.seatId)
                                .collect(Collectors.toList())
                );

    }
}
