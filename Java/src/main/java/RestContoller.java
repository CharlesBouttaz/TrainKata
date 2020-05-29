import domain.AllTrains;
import infra.in.MakeReservation;
import infra.out.TrainAdapter;

public class RestContoller {
    public void book() {
        //Adapter out
        AllTrains allTrains = new TrainAdapter(trainId -> null);

        //Hexagon
        MakeReservation makeReservation = new TicketOfficeService(allTrains, null);
        List<Seats> seatsToBook= makeReservation.makeReservation(request());

        // Adapter in
        String seatsToBookJSON = serializeSeats(seatsToBook);
        return seatsToBookJSON;
    }

    private ReservationRequestDto request() {
        return null;
    }
}
