package domain.model;

import java.util.List;

public class Train {
    private String trainId;
    private List<Coach> coaches;

    public Train(String trainId, List<Coach> coaches) {
        this.trainId = trainId;
        this.coaches = coaches;
    }

    public String getTrainId() {
        return trainId;
    }
}
