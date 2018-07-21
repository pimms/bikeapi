package no.jstien.bikeapi.tsdb.write

import java.util.HashMap

class Datum {
    var metric: String? = null
        private set
    var value: Long = 0
        private set
    var timestamp: Long = 0
        private set
    private val tags = HashMap<String, String>()

    constructor(metric: String, value: Long, timestamp: Long) {
        this.metric = metric
        this.value = value
        this.timestamp = timestamp
    }

    constructor(metric: String, value: Long) {
        this.metric = metric
        this.value = value
        this.timestamp = System.currentTimeMillis() / 1000L
    }

    fun addTag(key: String, value: String) {
        tags[key] = value
    }


    override fun toString(): String {
        return String.format("{metric:'%s', value:'%d', timestamp:'%d' tags:'%s'", metric, value, timestamp, tags.toString())
    }
}

