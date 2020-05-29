package infra.out;

import domain.AllTrains;

public class TrainAdapter implements AllTrains {
    TrainDataClient trainDataClient;

    public TrainAdapter(TrainDataClient trainDataClient) {
        this.trainDataClient = trainDataClient;
    }

    @Override
    public Train findWith(String trainId) {
        String topology = trainDataClient.getTopology(trainId);
        return null;
    }
}
