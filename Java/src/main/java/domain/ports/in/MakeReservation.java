package domain.ports.in;

import domain.Coach;
import domain.MakeReservationCommand;
import domain.Reservation;
import domain.Seat;
import domain.ports.out.BookTrain;
import domain.ports.out.GetTrainTopology;

import java.util.ArrayList;
import java.util.List;

public class MakeReservation {
    public final GetTrainTopology getTrainTopology;
    public final BookTrain bookTrain;

    public MakeReservation(GetTrainTopology getTrainTopology, BookTrain bookTrain) {
        this.getTrainTopology = getTrainTopology;
        this.bookTrain = bookTrain;
    }

    public Reservation makeReservation(MakeReservationCommand makeReservation) {
        List<Coach> coaches = getTrainTopology.getTrainTopology(makeReservation.trainId);

        Coach foundCoach = tryToFindAvailableCoach(makeReservation, coaches);

        List<Seat> chosenSeats = tryToChooseSeats(makeReservation, foundCoach);

        if (chosenSeats.isEmpty()) {
            // TODO: 17/04/2019 Optionnal
            return null;
        }
        Reservation reservation = bookTrain.bookTrain(makeReservation.trainId, chosenSeats, foundCoach);
        return reservation;
    }

    public List<Seat> tryToChooseSeats(MakeReservationCommand command, Coach foundCoach) {
        List<Seat> chosenSeats = new ArrayList<Seat>();
        if (foundCoach != null) {
            long limit = command.nbSeats;
            for (Seat seat : foundCoach.seats) {
                if (seat.available) {
                    if (limit-- == 0) break;
                    chosenSeats.add(seat);
                }
            }
        }
        return chosenSeats;
    }

    public Coach tryToFindAvailableCoach(MakeReservationCommand command, List<Coach> coaches) {
        Coach foundCoach = null;
        for (Coach coach : coaches) {
            long nbAvailableSeats = 0L;
            for (Seat seat : coach.seats) {
                if (seat.available) {
                    nbAvailableSeats++;
                }
            }
            if (nbAvailableSeats >= command.nbSeats) {
                foundCoach = coach;
                break;
            }
        }
        return foundCoach;
    }
}