package no.jstien.bikeapi.tsdb.read

import com.google.gson.Gson

import java.time.ZonedDateTime
import java.util.ArrayList
import java.util.Collections

class Request(start: ZonedDateTime, end: ZonedDateTime) {
    private val start: Long
    private val end: Long
    private val queries: MutableList<Query>

    init {
        this.start = start.toEpochSecond()
        this.end = end.toEpochSecond()
        this.queries = ArrayList()
    }

    fun addQuery(query: Query) {
        queries.add(query)
    }

    fun toJson(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    fun getQueries(): List<Query> {
        return Collections.unmodifiableList(queries)
    }
}
