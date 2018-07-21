package no.jstien.bikeapi.tsdb.read

import java.time.ZonedDateTime
import java.util.Arrays
import java.util.Optional

class RequestFactory(private val from: ZonedDateTime, private val to: ZonedDateTime, vararg stationIds: Int) {
    internal val stationIds: IntArray = stationIds
    private var downsample: Optional<String>? = null

    init {
        this.downsample = Optional.empty()
    }

    fun setDownsampleMinutes(minutes: Int) {
        if (minutes <= 0) {
            throw RuntimeException("Downsample value must be positive")
        }

        downsample = Optional.of("" + minutes + "m-avg")
    }

    internal fun create(): Request {
        val request = Request(from, to)
        Arrays.stream(stationIds).forEach { id -> addQuery(request, id) }

        downsample!!.ifPresent { ds -> request.getQueries().forEach { query -> query.setDownsample(ds) } }

        return request
    }

    private fun addQuery(request: Request, stationId: Int) {
        val bikeQuery = Query("bikes.free")
        bikeQuery.addFilter(Filter.literalOrFilter("stationId", false, stationId.toString()))

        val lockQuery = Query("locks.free")
        lockQuery.addFilter(Filter.literalOrFilter("stationId", false, stationId.toString()))

        request.addQuery(bikeQuery)
        request.addQuery(lockQuery)
    }
}
