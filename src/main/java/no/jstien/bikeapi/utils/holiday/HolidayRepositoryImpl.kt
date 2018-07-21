package no.jstien.bikeapi.utils.holiday

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import no.jstien.bikeapi.utils.CalendarUtils
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.util.EntityUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.ArrayList
import java.util.Collections
import java.util.GregorianCalendar

class HolidayRepositoryImpl(private val httpClient: HttpClient) : HolidayRepository {
    private var holidays: List<Holiday>? = null
    private val cachedYear: Int

    override// literally future proof
    val holidaysForCurrentYear: List<Holiday>
        get() {
            if (cachedYear != currentYear) {
                refreshRegistry()
            }

            return Collections.unmodifiableList(holidays!!)
        }

    private val currentYear: Int
        get() = ZonedDateTime.now().year

    init {
        this.cachedYear = 0
        this.holidays = emptyList()
    }

    private fun refreshRegistry() {
        try {
            val request = createRequest()
            val response = httpClient.execute(request)
            holidays = parseResponse(response)
            for (holiday in holidays!!) {
                LOG.debug("HOLIDAY: {} ({})", holiday.date, holiday.description)
            }
        } catch (e: Exception) {
            LOG.error("Failed to refresh holiday registry", e)
        }

    }

    private fun createRequest(): HttpUriRequest {
        return HttpGet(WEBAPI_NO_URL + HOLIDAY_PATH + currentYear)
    }

    private fun parseResponse(response: HttpResponse): List<Holiday> {
        throwIfBadStatus(response)

        try {
            val responseJson = EntityUtils.toString(response.entity, "UTF-8")
            return parseJson(responseJson)
        } catch (e: IOException) {
            throw RuntimeException("Failed to parse response JSON", e)
        }

    }

    private fun throwIfBadStatus(response: HttpResponse) {
        val statusCode = response.statusLine.statusCode
        if (statusCode != 200) {
            throw RuntimeException("Server returned status $statusCode")
        }
    }

    private fun parseJson(json: String): List<Holiday> {
        val parser = JsonParser()
        val root = parser.parse(json).asJsonObject


        val data = root.get("data").asJsonArray
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

        val holidays = ArrayList<Holiday>()
        data.forEach { ele ->
            val obj = ele.asJsonObject

            try {
                val desc = obj.get("description").asString
                val dateStr = obj.get("date").asString

                val date = GregorianCalendar()
                date.time = dateFormat.parse(dateStr)
                CalendarUtils.clearTime(date)

                holidays.add(Holiday(date, desc))
            } catch (e: Exception) {
                LOG.error("Failed to parse date", e)
            }
        }

        return holidays
    }

    companion object {
        private val WEBAPI_NO_URL = "https://webapi.no"
        private val HOLIDAY_PATH = "/api/v1/holydays/"

        private val LOG = LogManager.getLogger()
    }

}
