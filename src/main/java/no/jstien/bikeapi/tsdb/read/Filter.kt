package no.jstien.bikeapi.tsdb.read

import java.util.Arrays
import java.util.stream.Collectors

class Filter {
    private var type: String? = null
    private var tagk: String? = null
    private var filter: String? = null
    private var groupBy: Boolean = false

    fun setFilter(vararg tagValues: String) {
        filter = Arrays.stream(tagValues)
                .map{ i -> i.toString() }
                .collect(Collectors.joining("|"))
    }

    companion object {

        fun literalOrFilter(tagk: String, groupBy: Boolean, vararg tagValues: Int): Filter {
            val objs = Array(tagValues.size, { i -> tagValues[i].toString() })
            return literalOrFilter(tagk, groupBy, *objs)
        }

        fun literalOrFilter(tagk: String, groupBy: Boolean, vararg tagValues: String): Filter {
            val filter = Filter()
            filter.type = "literal_or"
            filter.tagk = tagk
            filter.groupBy = groupBy
            filter.setFilter(*tagValues)
            return filter
        }
    }
}
