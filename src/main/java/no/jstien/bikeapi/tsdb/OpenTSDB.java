package no.jstien.bikeapi.tsdb;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OpenTSDB implements TSDB {
    private static final Logger LOG = LogManager.getLogger();

    private static final String TSDB_PUT_PATH = "/api/put";

    private String url;
    private List<Datum> datums;
    private HttpClient httpClient;

    public OpenTSDB(String url, HttpClient httpClient) {
        LOG.info("OpenTSDB created with endpoint {}", url);
        this.url = url;
        datums = Collections.synchronizedList(new ArrayList<>());
        this.httpClient = httpClient;
    }

    @Override
    public DatumBuilder createDatumBuilder(String metricName) {
        return new DatumBuilder(metricName, this);
    }

    @Override
    public void addDatum(Datum datum) {
        // datums.add(datum);
        syncDatums(Collections.singletonList(datum));
    }


    private void syncDatums(List<Datum> datums) {
        LOG.info("Syncing {} datums", datums.size());
        HttpPost post = new HttpPost(url + TSDB_PUT_PATH);
        post.setEntity(getPostBody(datums));
        executeHttpPost(post);
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
