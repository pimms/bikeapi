package no.jstien.bikeapi.tsdb.read

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import no.jstien.bikeapi.tsdb.OpenTSDBPaths
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.io.IOException
import java.lang.reflect.Type
import java.util.HashMap

class StationTSDBReaderImpl(private val url: String, private val httpClient: HttpClient) : StationTSDBReader {

    override fun queryStations(requestFactory: RequestFactory): Map<Int, StationHistory> {
        val request = requestFactory.create()
        val json = executeRequest(request)
        return parseResult(json)
    }

    private fun executeRequest(request: Request): String {
        val httpPost = HttpPost(url + OpenTSDBPaths.QUERY_PATH)
        val body = request.toJson()
        LOG.info("oTSDB Query: {}", body)
        httpPost.entity = StringEntity(body, ContentType.APPLICATION_JSON)

        try {
            val response = httpClient.execute(httpPost)
            val statusCode = response.statusLine.statusCode
            if (statusCode < 200 || statusCode > 299) {
                throw TSDBException("OpenTSDB server responded with $statusCode", statusCode)
            }

            return EntityUtils.toString(response.entity, "UTF-8")
        } catch (e: TSDBException) {
            throw e
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to query TSDB", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to query TSDB", e)
        }

    }

    private fun parseResult(json: String): Map<Int, StationHistory> {
        val dtoList = parseToResponseDTO(json)
        val result = HashMap<Int, StationHistory>()

        dtoList!!.forEach { dto ->
            val stationId = Integer.valueOf(dto.tags!!["stationId"])

            if (!result.containsKey(stationId))
                result[stationId] = StationHistory(stationId)

            val history = result[stationId]

            if (dto.metric == "bikes.free") {
                history!!.freeBikes = dto.dps
            } else if (dto.metric == "locks.free") {
                history!!.freeLocks = dto.dps
            } else {
                throw RuntimeException("Unable to map metric '" + dto.metric + "'")
            }
        }

        return result
    }

    private fun parseToResponseDTO(json: String): List<TSDBResponseDTO>? {
        val builder = GsonBuilder()
        builder.registerTypeAdapter(TimeSerie::class.java, TimeSerieJsonDeserializer())
        val gson = builder.create()
        val type = object : TypeToken<List<TSDBResponseDTO>>() {

        }.type
        return gson.fromJson<List<TSDBResponseDTO>>(json, type)
    }

    companion object {
        private val LOG = LogManager.getLogger()
    }

}
