package no.jstien.bikeapi.station;

import no.jstien.bikeapi.tsdb.write.DatumBuilder;
import no.jstien.bikeapi.tsdb.write.TSDBWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

public class StationTSDBUpdater {
    private static final Logger LOG = LogManager.getLogger();

    private StationRepository stationRepository;
    private TSDBWriter tsdbWriter;

    private DatumBuilder freeBikesMetric;
    private DatumBuilder freeLocksMetric;

    public StationTSDBUpdater(StationRepository stationRepository, TSDBWriter tsdbWriter) {
        this.stationRepository = stationRepository;
        this.tsdbWriter = tsdbWriter;

        this.freeBikesMetric = tsdbWriter.createDatumBuilder("bikes.free").addTagKey("stationId");
        this.freeLocksMetric = tsdbWriter.createDatumBuilder("locks.free").addTagKey("stationId");
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
