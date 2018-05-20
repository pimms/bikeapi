package no.jstien.bikeapi.tsdb.read;

import no.jstien.bikeapi.tsdb.Datum;

import java.time.ZonedDateTime;
import java.util.List;

public interface StationTSDBReader {
    List<Datum> queryStation(ZonedDateTime from, ZonedDateTime to, int ...stationIds);
}
