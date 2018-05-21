package no.jstien.bikeapi.tsdb.read;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Query {
    private String aggregator = "sum";
    private String metric;
    private List<Filter> filters;
    private String downsample = "1s-sum";

    public Query(String metric) {
        this.metric = metric;
        this.filters = new ArrayList<>();
    }

    public void setAggregator(String aggregator) {
        this.aggregator = aggregator;
    }

    public void setDownsample(String downsample) {
        this.downsample = downsample;
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public List<Filter> getFilters() {
        return Collections.unmodifiableList(filters);
    }
}
