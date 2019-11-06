package domain.portout;

import domain.Topology;
import domain.TrainId;

public interface GetTrainTopology {
    Topology get(TrainId trainId);
}
