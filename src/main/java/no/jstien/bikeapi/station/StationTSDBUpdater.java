package no.jstien.bikeapi.station;

import no.jstien.bikeapi.tsdb.DatumBuilder;
import no.jstien.bikeapi.tsdb.TSDB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

public class StationTSDBUpdater {
    private static final Logger LOG = LogManager.getLogger();

    private StationRepository stationRepository;
    private TSDB tsdb;

    private DatumBuilder freeBikesMetric;
    private DatumBuilder freeLocksMetric;

    public StationTSDBUpdater(StationRepository stationRepository, TSDB tsdb) {
        this.stationRepository = stationRepository;
        this.tsdb = tsdb;

        this.freeBikesMetric = tsdb.createDatumBuilder("bikes.free").addTagKey("stationId");
        this.freeLocksMetric = tsdb.createDatumBuilder("locks.free").addTagKey("stationId");
    }

    @Scheduled(fixedRateString = "${station.refresh.tsdb-interval}")
    private void syncStationAvailabilityDatums() {
        LOG.info("--> Syncing station availability datums");

        stationRepository.getAllStations().forEach(station -> {
            freeBikesMetric.addDatum(station.getFreeBikes(), String.valueOf(station.getId()));
            freeLocksMetric.addDatum(station.getFreeLocks(), String.valueOf(station.getId()));
        });
    }
}
