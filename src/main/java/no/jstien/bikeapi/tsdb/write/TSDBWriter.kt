package no.jstien.bikeapi.tsdb.write

interface TSDBWriter {
    fun createDatumBuilder(metricName: String): DatumBuilder
    fun addDatum(datum: Datum)
}

