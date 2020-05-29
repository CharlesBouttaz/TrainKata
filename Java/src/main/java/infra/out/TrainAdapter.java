package infra.out;

import com.google.gson.Gson;
import domain.AllTrains;
import domain.model.Train;

public class TrainAdapter implements AllTrains {
    TrainDataClient trainDataClient;

    public TrainAdapter(TrainDataClient trainDataClient) {
        this.trainDataClient = trainDataClient;
    }

    @Override
    public Train findWith(String trainId) {
        String topologyAsString = trainDataClient.getTopology(trainId);
        Topologie topology = new Gson().fromJson(topologyAsString, Topologie.class);
        Train train = new Train(trainId);
        return train;
    }
}
