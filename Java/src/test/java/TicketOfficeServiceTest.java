import infra.BookingReferenceClient;
import infra.SeatDto;
import infra.TrainDataClient;
import org.junit.*;

import java.util.List;

public class TicketOfficeServiceTest {
    private static final String TrainId = "9043-2018-05-24";
    private static final String BookingReference = "75bcd15";

    @Test
    public void Reserve_seats_when_train_is_empty()
    { 
        int seatsRequestedCount = 3;
        BookingReferenceClientStub bookingReferenceClient = new BookingReferenceClientStub(BookingReference);
        TicketOfficeService service = buildTicketOfficeService(TrainTopologies.With_10_available_seats(), bookingReferenceClient);
        
        String reservation = service.makeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \""+ BookingReference +"\", \"seats\": [\"1A\", \"2A\", \"3A\"]}", reservation);
        Assert.assertEquals(TrainId, bookingReferenceClient.trainIdBooked);
        Assert.assertEquals(BookingReference, bookingReferenceClient.referenceBooked);
        Assert.assertEquals(3, bookingReferenceClient.seatsBooked.size());
        Assert.assertEquals(new SeatDto("A", 1).toString(), bookingReferenceClient.seatsBooked.get(0).toString());
        Assert.assertEquals(new SeatDto("A", 2).toString(), bookingReferenceClient.seatsBooked.get(1).toString());
        Assert.assertEquals(new SeatDto("A", 3).toString(), bookingReferenceClient.seatsBooked.get(2).toString());
    }

    @Test
    public void Not_reserve_seats_when_not_enough_free_place()
    {
        int seatsRequestedCount = 5;
        BookingReferenceClientStub bookingReferenceClient = new BookingReferenceClientStub(BookingReference);
        TicketOfficeService trainDataService = buildTicketOfficeService(TrainTopologies.With_10_seats_and_6_already_reserved(), bookingReferenceClient);

        String reservation = trainDataService.makeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
        Assert.assertFalse(bookingReferenceClient.booked);
    }

    @Test
    public void Reserve_seats_when_one_coach_is_full_and_one_is_empty()
    {
        int seatsRequestedCount = 3;
        BookingReferenceClientStub bookingReferenceClient = new BookingReferenceClientStub(BookingReference);
        TicketOfficeService trainDataService = buildTicketOfficeService(TrainTopologies.With_2_coaches_and_the_first_coach_is_full(), bookingReferenceClient);

        String reservation = trainDataService.makeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \""+ BookingReference +"\", \"seats\": [\"1B\", \"2B\", \"3B\"]}", reservation);
        Assert.assertTrue(bookingReferenceClient.booked);
    }

    @Test
    public void Reserve_all_seats_in_the_same_coach()
    {
        int seatsRequestedCount = 2;
        BookingReferenceClientStub bookingReferenceClient = new BookingReferenceClientStub(BookingReference);
        TicketOfficeService trainDataService = buildTicketOfficeService(TrainTopologies.With_2_coaches_and_9_seats_already_reserved_in_the_first_coach(), bookingReferenceClient);

        String reservation = trainDataService.makeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \""+ BookingReference +"\", \"seats\": [\"1B\", \"2B\"]}", reservation);
        Assert.assertTrue(bookingReferenceClient.booked);
    }

    @Test
    public void Cannot_Reserve_When_Train_Is_Not_Full_But_Not_Coach_Is_Available()
    {
        int seatsRequestedCount = 2;
        BookingReferenceClientStub bookingReferenceClient = new BookingReferenceClientStub(BookingReference);
        TicketOfficeService trainDataService = buildTicketOfficeService(TrainTopologies.With_10_coaches_half_available(), bookingReferenceClient);

        String reservation = trainDataService.makeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
        Assert.assertFalse(bookingReferenceClient.booked);
    }

    @Test
    @Ignore
    public void Not_reserve_seats_when_it_exceed_max_capacity_threshold()
    {
        int seatsRequestedCount = 3;
        BookingReferenceClientStub bookingReferenceClient = new BookingReferenceClientStub(BookingReference);
        TicketOfficeService service = buildTicketOfficeService(TrainTopologies.With_10_seats_and_6_already_reserved(), bookingReferenceClient);

        String reservation = service.makeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
        Assert.assertFalse(bookingReferenceClient.booked);
    }

    private static TicketOfficeService buildTicketOfficeService(String topologies, BookingReferenceClient bookingReferenceClient)
    {
        return new TicketOfficeService(new TrainDataClientStub(topologies), bookingReferenceClient);
    }

    private static class BookingReferenceClientStub implements BookingReferenceClient {
        private String bookingReference;
        public String trainIdBooked;
        public String referenceBooked;
        public List<SeatDto> seatsBooked;
        public Boolean booked = false;

        public BookingReferenceClientStub(String bookingReference) {
            this.bookingReference = bookingReference;
        }

        @Override
        public String generateBookingReference(){
            return this.bookingReference;
        }

        @Override
        public void bookTrain(String trainId, String bookingReference, List<SeatDto> seats){
            this.trainIdBooked = trainId;
            this.referenceBooked = bookingReference;
            this.seatsBooked = seats;
            this.booked = true;
        }
    }

    private static class TrainDataClientStub implements TrainDataClient {
        private String topologies;

        public TrainDataClientStub(String topologies) {
            this.topologies = topologies;
        }

        @Override
        public String getTopology(String trainId) {
            return this.topologies;
        }
    }
}
