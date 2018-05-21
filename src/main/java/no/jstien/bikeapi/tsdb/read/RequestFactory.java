package no.jstien.bikeapi.tsdb.read;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

public class RequestFactory {
    private ZonedDateTime from;
    private ZonedDateTime to;
    private int[] stationIds;
    private Optional<String> downsample;

    public RequestFactory(ZonedDateTime from, ZonedDateTime to, int ...stationIds) {
        this.from = from;
        this.to = to;
        this.stationIds = stationIds;
    }

    public void setDownsampleMinutes(int minutes) {
        if (minutes <= 0) {
            throw new RuntimeException("Downsample value must be positive");
        }

        downsample = Optional.of(""+minutes+"m-avg");
    }

    Request create() {
        Request request = new Request(from, to);
        Arrays.stream(stationIds).forEach(id -> addQuery(request, id));

        downsample.ifPresent(ds -> {
            request.getQueries().forEach(query -> {
                query.setDownsample(ds);
            });
        });

        return request;
    }

    int[] getStationIds() {
        return stationIds;
    }

    private void addQuery(Request request, int stationId) {
        Query bikeQuery = new Query("bikes.free");
        bikeQuery.addFilter(Filter.literalOrFilter("stationId", false, String.valueOf(stationId)));

        Query lockQuery = new Query("locks.free");
        lockQuery.addFilter(Filter.literalOrFilter("stationId", false, String.valueOf(stationId)));

        request.addQuery(bikeQuery);
        request.addQuery(lockQuery);
    }
}
