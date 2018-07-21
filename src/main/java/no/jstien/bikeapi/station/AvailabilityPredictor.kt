package no.jstien.bikeapi.station

import no.jstien.bikeapi.tsdb.read.*
import no.jstien.bikeapi.utils.AnalogueDateFinder
import no.jstien.bikeapi.utils.CalendarUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.ZonedDateTime
import java.util.Calendar
import java.util.GregorianCalendar

class AvailabilityPredictor(private val tsdbReader: StationTSDBReader, private val dateFinder: AnalogueDateFinder) {
    private var downsampleMinutes = DEFAULT_DOWNSAMPLE_MINUTES

    fun setDownsampleMinutes(minutes: Int) {
        this.downsampleMinutes = minutes
    }

    fun predictForStation(stationId: Int, predictionDate: Calendar): StationHistory {
        // TODO: This is quite nasty, and I'm honestly surprised if it turns out to not be
        // buggy AF. Anyways, it's an OK POC I guess.
        val analogues = dateFinder.findAnaloguesForDay(predictionDate)

        val numElems = MINUTES_PER_DAY / downsampleMinutes
        val bikesCount = IntArray(numElems)
        val bikes = DoubleArray(numElems)
        val locksCount = IntArray(numElems)
        val locks = DoubleArray(numElems)

        analogues.forEach { day ->
            val startDate = CalendarUtils.startOfDay(day)
            val from = startDate.toZonedDateTime()
            val to = CalendarUtils.endOfDay(day).toZonedDateTime()
            val requestFactory = RequestFactory(from, to, stationId)
            requestFactory.setDownsampleMinutes(downsampleMinutes)

            val startTime = startDate.timeInMillis / 1000L
            val history = tsdbReader.queryStations(requestFactory)
            history.values.stream()
                    .forEach { h ->
                        processTimeSerie(h.freeBikes!!, startTime, bikes, bikesCount)
                        processTimeSerie(h.freeLocks!!, startTime, locks, locksCount)
                    }
        }

        // Cool, let's try to create a StationHistory from it
        reduceHistogram(bikes, bikesCount)
        reduceHistogram(locks, locksCount)

        val timeZero = CalendarUtils.startOfDay(predictionDate).timeInMillis / 1000L
        val bikeSerie = toTimeSerie(bikes, timeZero)
        val lockSerie = toTimeSerie(locks, timeZero)
        return StationHistory(stationId, bikeSerie, lockSerie)
    }

    private fun processTimeSerie(timeSerie: TimeSerie, startTime: Long, dest: DoubleArray, counts: IntArray) {
        timeSerie.dataPoints.forEach { dp ->
            val timestamp = dp.timestamp
            val index = (timestamp - startTime) / (downsampleMinutes * 60)
            dest[index.toInt()] += dp.value
            counts[index.toInt()]++
        }
    }

    private fun reduceHistogram(vals: DoubleArray, counts: IntArray) {
        for (i in vals.indices) {
            if (counts[i] != 0) {
                vals[i] /= counts[i].toDouble()
            }
        }
    }

    private fun toTimeSerie(vals: DoubleArray, firstTimeStamp: Long): TimeSerie {
        val ts = TimeSerie()

        val deltaTime = (downsampleMinutes * 60).toLong()
        var time = firstTimeStamp
        for (i in vals.indices) {
            ts.addDataPoint(DataPoint(time, vals[i]))
            time += deltaTime
        }

        return ts
    }

    companion object {
        private val LOG = LogManager.getLogger()

        private val MINUTES_PER_DAY = 60 * 24
        private val DEFAULT_DOWNSAMPLE_MINUTES = 15
    }

}
