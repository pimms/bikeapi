package no.jstien.bikeapi.tsdb.write;

import com.google.gson.Gson;
import no.jstien.bikeapi.tsdb.Datum;
import no.jstien.bikeapi.tsdb.OpenTSDBPaths;
import no.jstien.bikeapi.utils.ListUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TSDBWriterImpl implements TSDBWriter {
    private static final Logger LOG = LogManager.getLogger();

    // As per OpenTSDB's recommendations, limit the number of datums per request to 50.
    private static final int MAX_DATUMS_PER_REQUEST = 50;

    private String url;
    private ConcurrentLinkedQueue<Datum> datumsCache;
    private HttpClient httpClient;

    public TSDBWriterImpl(String url, HttpClient httpClient) {
        LOG.info("TSDBWriterImpl created with endpoint {}", url);
        this.url = url;
        datumsCache = new ConcurrentLinkedQueue<>();
        this.httpClient = httpClient;
    }

    @Override
    public DatumBuilder createDatumBuilder(String metricName) {
        return new DatumBuilder(metricName, this);
    }

    @Scheduled(fixedRateString = "${tsdb.sync-interval}",
            initialDelayString = "${tsdb.sync-interval}")
    private synchronized void flushDatumsCache() {
        List<Datum> datums = new ArrayList<>();
        while (!datumsCache.isEmpty()) {
            datums.add(datumsCache.poll());
        }

        if (!datums.isEmpty()) {
            syncDatums(datums);
        }
    }

    @Override
    public void addDatum(Datum datum) {
        datumsCache.add(datum);
    }

    private void syncDatums(List<Datum> datums) {
        LOG.info("--> Syncing {} datums", datums.size());

        ListUtils.processBatchwise(datums, MAX_DATUMS_PER_REQUEST, subList -> {
            LOG.debug("Sending datum subset with {} datums", subList.size());
            HttpPost post = new HttpPost(url + OpenTSDBPaths.PUT_PATH);
            post.setEntity(getPostBody(subList));
            executeHttpPost(post);
        });
    }

    private StringEntity getPostBody(List<Datum> datums) {
        String json = serializeDatums(datums);
        StringEntity postBody = new StringEntity(json, ContentType.APPLICATION_JSON);
        return postBody;
    }

    private String serializeDatums(List<Datum> datums) {
        Gson gson = new Gson();
        return gson.toJson(datums);
    }

    private void executeHttpPost(HttpPost post) {
        try {
            HttpResponse response = httpClient.execute(post);
            logHttpResponse(response);
        } catch (IOException e) {
            LOG.error("Failed to sync datums", e);
        }
    }

    private void logHttpResponse(HttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            HttpEntity entity = response.getEntity();
            String responseString = "";
            if (entity != null) {
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }

            LOG.error("HTTP Request failed with status {}. Body: '{}'", statusCode, responseString);
        }
    }
}
