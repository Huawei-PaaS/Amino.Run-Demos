package org.openalpr;

import org.openalpr.model.Result;

import java.io.Serializable;
import java.util.List;

public class Results implements Serializable {

    private final Double epoch_time;

    private final Double processing_time_ms;

    private final List<Result> results;

    public Results(Double epoch_time, Double processing_time_ms, List<Result> results) {
        this.epoch_time = epoch_time;
        this.processing_time_ms = processing_time_ms;
        this.results = results;
    }

    public List<Result> getResults() {
        return results;
    }
}
