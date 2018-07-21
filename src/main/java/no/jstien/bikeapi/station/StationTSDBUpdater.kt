package no.jstien.bikeapi.station

import no.jstien.bikeapi.tsdb.write.DatumBuilder
import no.jstien.bikeapi.tsdb.write.TSDBWriter
import org.apache.logging.log4j.LogManager
import org.springframework.scheduling.annotation.Scheduled

class StationTSDBUpdater(private val stationRepository: StationRepository, private val tsdbWriter: TSDBWriter) {

    private val freeBikesMetric: DatumBuilder
    private val freeLocksMetric: DatumBuilder

    init {

        this.freeBikesMetric = tsdbWriter.createDatumBuilder("bikes.free").addTagKey("stationId")
        this.freeLocksMetric = tsdbWriter.createDatumBuilder("locks.free").addTagKey("stationId")
    }

    @Scheduled(fixedRateString = "\${station.refresh.tsdb-interval}")
    private fun syncStationAvailabilityDatums() {
        LOG.info("--> Syncing station availability datums")

        stationRepository.getAllStations().forEach { station ->
            freeBikesMetric.addDatum(station.freeBikes.toLong(), station.id.toString())
            freeLocksMetric.addDatum(station.freeLocks.toLong(), station.id.toString())
        }
    }

    companion object {
        private val LOG = LogManager.getLogger()
    }
}
