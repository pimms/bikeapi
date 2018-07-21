package no.jstien.bikeapi.tsdb.write

import com.google.gson.Gson
import no.jstien.bikeapi.tsdb.write.Datum
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import java.util.ArrayList

class DatumTest {
    private var datum: Datum? = null

    @Before
    fun setup() {
        datum = Datum("metric", 22, 1111)
        datum!!.addTag("key", "value")
    }

    @Test
    fun serializationMatchesSchema() {
        val gson = Gson()
        val json = gson.toJson(datum)

        Assert.assertEquals(datumJsonString, json)
    }

    @Test
    fun serializationOfArrayMatchesSchema() {
        val list = ArrayList<Datum>()
        list.add(datum!!)
        list.add(datum!!)

        val gson = Gson()
        val json = gson.toJson(list)

        val expected = "[$datumJsonString,$datumJsonString]"
        Assert.assertEquals(expected, json)
    }

    companion object {
        private val datumJsonString = "{\"metric\":\"metric\",\"value\":22,\"timestamp\":1111,\"tags\":{\"key\":\"value\"}}"
    }
}
