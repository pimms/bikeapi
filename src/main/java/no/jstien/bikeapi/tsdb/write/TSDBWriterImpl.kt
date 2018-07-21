package no.jstien.bikeapi.tsdb.write

import com.google.gson.Gson
import no.jstien.bikeapi.tsdb.OpenTSDBPaths
import no.jstien.bikeapi.utils.ListUtils
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.scheduling.annotation.Scheduled

import java.io.IOException
import java.util.ArrayList
import java.util.concurrent.ConcurrentLinkedQueue

class TSDBWriterImpl(private val url: String, private val httpClient: HttpClient) : TSDBWriter {
    private val datumsCache: ConcurrentLinkedQueue<Datum>

    init {
        LOG.info("TSDBWriterImpl created with endpoint {}", url)
        datumsCache = ConcurrentLinkedQueue()
    }

    override fun createDatumBuilder(metricName: String): DatumBuilder {
        return DatumBuilder(metricName, this)
    }

    @Scheduled(fixedRateString = "\${tsdb.sync-interval}", initialDelayString = "\${tsdb.sync-interval}")
    @Synchronized
    private fun flushDatumsCache() {
        val datums = ArrayList<Datum>()
        while (!datumsCache.isEmpty()) {
            datums.add(datumsCache.poll())
        }

        if (!datums.isEmpty()) {
            syncDatums(datums)
        }
    }

    override fun addDatum(datum: Datum) {
        datumsCache.add(datum)
    }

    private fun syncDatums(datums: List<Datum>) {
        LOG.info("--> Syncing {} datums", datums.size)

        ListUtils.processBatchwise(datums, MAX_DATUMS_PER_REQUEST, { subList: List<Datum> ->
            LOG.debug("Sending datum subset with {} datums", subList.size)
            val post = HttpPost(url + OpenTSDBPaths.PUT_PATH)
            post.entity = getPostBody(subList)
            executeHttpPost(post)
        })
    }

    private fun getPostBody(datums: List<Datum>): StringEntity {
        val json = serializeDatums(datums)
        return StringEntity(json, ContentType.APPLICATION_JSON)
    }

    private fun serializeDatums(datums: List<Datum>): String {
        val gson = Gson()
        return gson.toJson(datums)
    }

    private fun executeHttpPost(post: HttpPost) {
        try {
            val response = httpClient.execute(post)
            logHttpResponse(response)
        } catch (e: IOException) {
            LOG.error("Failed to sync datums", e)
        }

    }

    @Throws(IOException::class)
    private fun logHttpResponse(response: HttpResponse) {
        val statusCode = response.statusLine.statusCode
        if (statusCode < 200 || statusCode > 299) {
            val entity = response.entity
            var responseString = ""
            if (entity != null) {
                responseString = EntityUtils.toString(response.entity, "UTF-8")
            }

            LOG.error("HTTP Request failed with status {}. Body: '{}'", statusCode, responseString)
        }
    }

    companion object {
        private val LOG = LogManager.getLogger()

        // As per OpenTSDB's recommendations, limit the number of datums per request to 50.
        private val MAX_DATUMS_PER_REQUEST = 50
    }
}
