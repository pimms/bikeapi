package no.jstien.bikeapi.tsdb.read

import com.fasterxml.jackson.annotation.JsonProperty

class DataPoint(@get:JsonProperty("ts")
                val timestamp: Long, @get:JsonProperty("val")
                val value: Double)
