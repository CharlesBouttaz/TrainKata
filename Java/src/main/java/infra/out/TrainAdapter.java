package infra.out;

import com.google.gson.Gson;
import domain.AllTrains;
import domain.model.Coach;
import domain.model.Seat;
import domain.model.Train;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TrainAdapter implements AllTrains {
    TrainDataClient trainDataClient;

    public TrainAdapter(TrainDataClient trainDataClient) {
        this.trainDataClient = trainDataClient;
    }

    @Override
    public Train findWith(String trainId) {
        String topologyAsString = trainDataClient.getTopology(trainId);
        Topologie topology = new Gson().fromJson(topologyAsString, Topologie.class);
        List<Coach> coaches = topology.seats.values().stream()
                .collect(Collectors.groupingBy(topologieSeat -> topologieSeat.coach))
                .entrySet()
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());

        return new Train(trainId, coaches);
    }

    private Seat convert(Topologie.TopologieSeat topologieSeat) {
        return new Seat(topologieSeat.seat_number + topologieSeat.coach, null == topologieSeat.booking_reference);
    }

    private Coach convert(Map.Entry<String, List<Topologie.TopologieSeat>> seatsByCoach) {
        return new Coach(seatsByCoach.getKey(), seatsByCoach.getValue().stream().map(this::convert).collect(Collectors.toList()));
    }
}
