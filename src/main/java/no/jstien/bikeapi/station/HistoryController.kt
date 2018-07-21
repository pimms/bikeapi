package no.jstien.bikeapi.station

import no.jstien.bikeapi.tsdb.read.RequestFactory
import no.jstien.bikeapi.tsdb.read.StationHistory
import no.jstien.bikeapi.tsdb.read.StationTSDBReader
import no.jstien.bikeapi.tsdb.read.TSDBException
import no.jstien.bikeapi.tsdb.write.DatumBuilder
import no.jstien.bikeapi.tsdb.write.TSDBWriter
import no.jstien.bikeapi.utils.AnalogueDateFinder
import no.jstien.bikeapi.utils.holiday.HolidayRegistry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.GregorianCalendar
import java.util.TimeZone

@RestController
class HistoryController @Autowired
constructor(tsdbWriter: TSDBWriter,
            private val stationRepository: StationRepository,
            private val tsdbReader: StationTSDBReader,
            private val holidayRegistry: HolidayRegistry) {
    private val httpCallMetric: DatumBuilder

    init {
        this.httpCallMetric = tsdbWriter.createDatumBuilder("http_calls").addTagKey("endpoint").addTagKey("method")
    }

    @RequestMapping("/stations/history")
    fun getStationHistory(
            response: HttpServletResponse,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: ZonedDateTime,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: ZonedDateTime,
            @RequestParam("id") stationIds: IntArray,
            @RequestParam(value = "dsm", defaultValue = "0") downsampleMinutes: Int): Collection<StationHistory>? {
        httpCallMetric.addDatum("history_controller", "/history")

        if (ChronoUnit.HOURS.between(from, to) > 30) {
            response.status = 400
            throw RuntimeException("Cannot query intervals larger than 30 hours")
        }

        if (stationIds.size > 5) {
            response.status = 400
            throw RuntimeException("Cannot query for more than 4 stations at a time")
        }

        try {
            val requestFactory = RequestFactory(from, to, *stationIds)

            if (downsampleMinutes > 0)
                requestFactory.setDownsampleMinutes(downsampleMinutes)

            return tsdbReader.queryStations(requestFactory).values
        } catch (e: TSDBException) {
            LOG.info("/history call failed (TSDB): " + e.message)
            response.status = e.statusCode
        } catch (e: Throwable) {
            LOG.error("/history call failed", e)
            response.status = 500
        }

        return null
    }


    @RequestMapping("/stations/prediction")
    fun prediction(@RequestParam("id") stationId: Int,
                   @RequestParam(value = "dsm", defaultValue = "15") downsampleMinutes: Int): StationHistory {
        if (downsampleMinutes < 1 || downsampleMinutes > 60) {
            throw RuntimeException("dsm must be in the range [1..60]")
        }

        httpCallMetric.addDatum("history_controller", "/prediction")

        val dateFinder = AnalogueDateFinder(holidayRegistry)

        val predictor = AvailabilityPredictor(tsdbReader, dateFinder)
        predictor.setDownsampleMinutes(downsampleMinutes)

        return predictor.predictForStation(stationId, GregorianCalendar(TimeZone.getTimeZone("ECT")))
    }

    companion object {
        private val LOG = LogManager.getLogger()
    }

}
