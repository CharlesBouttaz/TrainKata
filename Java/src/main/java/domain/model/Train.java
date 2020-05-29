package domain.model;

import java.util.List;

public class Train {
    private String trainId;
    private List<Coach> coaches;

    public Train(String trainId) {
        this.trainId = trainId;
    }

    public String getTrainId() {
        return trainId;
    }
}
