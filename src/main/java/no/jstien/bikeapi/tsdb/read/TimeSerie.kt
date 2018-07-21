package no.jstien.bikeapi.tsdb.read

import java.util.ArrayList

class TimeSerie {
    private val values: MutableList<DataPoint>

    val dataPoints: List<DataPoint>
        get() = values

    init {
        this.values = ArrayList()
    }

    fun addDataPoint(dataPoint: DataPoint) {
        this.values.add(dataPoint)
    }
}
