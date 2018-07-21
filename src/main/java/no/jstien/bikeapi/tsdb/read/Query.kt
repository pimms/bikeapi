package no.jstien.bikeapi.tsdb.read

import java.util.ArrayList
import java.util.Collections

class Query(private val metric: String) {
    private var aggregator = "sum"
    private val filters: MutableList<Filter>
    private var downsample = "1s-sum"

    init {
        this.filters = ArrayList()
    }

    fun setAggregator(aggregator: String) {
        this.aggregator = aggregator
    }

    fun setDownsample(downsample: String) {
        this.downsample = downsample
    }

    fun addFilter(filter: Filter) {
        filters.add(filter)
    }

    fun getFilters(): List<Filter> {
        return Collections.unmodifiableList(filters)
    }
}
