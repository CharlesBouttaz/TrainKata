import java.util.Map;

public class Topologie {
    public Map<String, TopologieSeat> seats;

    public class TopologieSeat {
        public String booking_reference;
        public int seat_number;
        public String coach;
    }
}
