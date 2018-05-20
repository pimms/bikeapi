package no.jstien.bikeapi.tsdb.read;

import com.google.gson.Gson;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Request {
    private long start;
    private long end;
    private List<Query> queries;

    public Request(ZonedDateTime start, ZonedDateTime end) {
        this.start = start.toEpochSecond();
        this.end = end.toEpochSecond();
        this.queries = new ArrayList<>();
    }

    public void addQuery(Query query) {
        queries.add(query);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public List<Query> getQueries() {
        return Collections.unmodifiableList(queries);
    }
}
