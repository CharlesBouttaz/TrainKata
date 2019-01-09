import com.google.gson.Gson;
import org.junit.*;

import java.util.List;
import java.util.stream.Collectors;

public class TicketOfficeServiceTest {
    private static final String TrainId = "9043-2018-05-24";
    private static final String BookingReference = "75bcd15";

    @Test
    public void Reserve_seats_when_train_is_empty()
    { 
        int seatsRequestedCount = 3;
        TicketOfficeService service = buildTicketOfficeService(TrainTopologies.With_10_available_seats());

        String reservation = makeReservation(seatsRequestedCount, service);

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \""+ BookingReference +"\", \"seats\": [\"1A\", \"2A\", \"3A\"]}", reservation);
    }

    @Test
    public void Not_reserve_seats_when_not_enough_free_place()
    {
        int seatsRequestedCount = 5;
        TicketOfficeService trainDataService = buildTicketOfficeService(TrainTopologies.With_10_seats_and_6_already_reserved());

        String reservation = makeReservation(seatsRequestedCount, trainDataService);

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
    }

    @Test
    public void Reserve_seats_when_one_coach_is_full_and_one_is_empty()
    {
        int seatsRequestedCount = 3;
        TicketOfficeService trainDataService = buildTicketOfficeService(TrainTopologies.With_2_coaches_and_the_first_coach_is_full());

        String reservation = makeReservation(seatsRequestedCount, trainDataService);

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \""+ BookingReference +"\", \"seats\": [\"1B\", \"2B\", \"3B\"]}", reservation);
    }

    @Test
    public void Reserve_all_seats_in_the_same_coach()
    {
        int seatsRequestedCount = 2;
        TicketOfficeService trainDataService = buildTicketOfficeService(TrainTopologies.With_2_coaches_and_9_seats_already_reserved_in_the_first_coach());

        String reservation = makeReservation(seatsRequestedCount, trainDataService);

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \""+ BookingReference +"\", \"seats\": [\"1B\", \"2B\"]}", reservation);
    }

    @Test
    public void Cannot_Reserve_When_Train_Is_Not_Full_But_Not_Coach_Is_Available()
    {
        int seatsRequestedCount = 2;
        TicketOfficeService trainDataService = buildTicketOfficeService(TrainTopologies.With_10_coaches_half_available());

        String reservation = makeReservation(seatsRequestedCount, trainDataService);

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
    }

    @Test
    @Ignore
    public void Not_reserve_seats_when_it_exceed_max_capacity_threshold()
    {
        int seatsRequestedCount = 3;
        TicketOfficeService service = buildTicketOfficeService(TrainTopologies.With_10_seats_and_6_already_reserved());

        String reservation = makeReservation(seatsRequestedCount, service);

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
    }

    private String makeReservation(int seatsRequestedCount, TicketOfficeService service) {
        ReservationResponseDto reservationResponseDto = service.makeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));
        String reservation = "{" +
                "\"train_id\": \"" + reservationResponseDto.trainId + "\", " +
                "\"booking_reference\": \"" + reservationResponseDto.bookingId + "\", " +
                "\"seats\": [" + reservationResponseDto.seats.stream().map(s -> "\"" + s.seatNumber + s.coach + "\"").collect(Collectors.joining(", ")) + "]" +
                "}";
        return reservation;
    }

    private static TicketOfficeService buildTicketOfficeService(String topologies)
    {
        return new TicketOfficeService(new TrainDataClientStub(topologies), new BookingReferenceClientStub(BookingReference));
    }
    private static class BookingReferenceClientStub implements BookingReferenceClient {

        private String bookingReference;

        public BookingReferenceClientStub(String bookingReference) {
            this.bookingReference = bookingReference;
        }

        @Override
        public String generateBookingReference(){
            return this.bookingReference;
        }

        @Override
        public void bookTrain(String trainId, String bookingReference, List<Seat> seats){
        }
    }

    private static class TrainDataClientStub implements TrainDataClient {
        private String topologies;

        public TrainDataClientStub(String topologies) {
            this.topologies = topologies;
        }

        @Override
        public Topologie getTopology(String trainId) {
            return new Gson().fromJson(this.topologies, Topologie.class);
        }
    }
}
