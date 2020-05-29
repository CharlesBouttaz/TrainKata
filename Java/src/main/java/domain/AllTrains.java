package domain;

import domain.model.Train;

public interface AllTrains {
    Train findWith(String trainId);
}
