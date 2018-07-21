package no.jstien.bikeapi.tsdb.read

import com.google.gson.*

import java.lang.reflect.Type

class TimeSerieJsonDeserializer : JsonDeserializer<TimeSerie> {
    @Throws(JsonParseException::class)
    override fun deserialize(jsonElement: JsonElement, type: Type, jsonDeserializationContext: JsonDeserializationContext): TimeSerie {
        val jsonObject = jsonElement.asJsonObject

        val timeSerie = TimeSerie()

        jsonObject.keySet().forEach { key ->
            val timestamp = java.lang.Long.valueOf(key)
            val value = jsonObject.get(key).asDouble
            val dp = DataPoint(timestamp, value)
            timeSerie.addDataPoint(dp)
        }

        return timeSerie
    }
}

