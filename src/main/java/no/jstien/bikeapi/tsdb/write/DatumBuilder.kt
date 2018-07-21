package no.jstien.bikeapi.tsdb.write

import java.util.ArrayList

class DatumBuilder(private val metricName: String, private val tsdbWriter: TSDBWriter) {
    private val tagKeys = ArrayList<String>()

    fun addTagKey(tagKey: String): DatumBuilder {
        tagKeys.add(tagKey)
        return this
    }

    fun addDatum(vararg tagValues: String) {
        addDatum(1L, *tagValues)
    }

    fun addDatum(value: Long, vararg tagValues: String) {
        verifyTagValues(tagKeys, arrayOf(*tagValues))

        val datum = Datum(metricName, value)
        for (i in tagKeys.indices) {
            datum.addTag(tagKeys[i], tagValues[i])
        }

        tsdbWriter.addDatum(datum)
    }


    private fun verifyTagValues(keys: List<String>, values: Array<String>) {
        if (keys.size != values.size) {
            val sb = StringBuilder()
            sb.append("Expected " + keys.size + " tag values, got " + values.size + "! ")
            sb.append("Expectedd values for keys: ")
            keys.forEach { key -> sb.append("$key ") }
            throw RuntimeException(sb.toString())
        }
    }
}
