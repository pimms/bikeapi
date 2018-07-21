package no.jstien.bikeapi.tsdb.write

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class DevNullTSDBWriter : TSDBWriter {
    internal var LOG = LogManager.getLogger()

    override fun createDatumBuilder(metricName: String): DatumBuilder {
        return DatumBuilder(metricName, this)
    }

    override fun addDatum(datum: Datum) {
        LOG.trace("DevNullTSDBWriter -> discarding datum")
    }
}
