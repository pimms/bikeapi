package no.jstien.bikeapi.tsdb.read;

import no.jstien.bikeapi.tsdb.Datum;
import no.jstien.bikeapi.tsdb.OpenTSDBPaths;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StationTSDBReaderImpl implements StationTSDBReader {
    private static final Logger LOG = LogManager.getLogger();

    private HttpClient httpClient;
    private String url;

    public StationTSDBReaderImpl(String url, HttpClient httpClient) {
        this.url = url;
        this.httpClient = httpClient;
    }

    @Override
    public List<Datum> queryStation(ZonedDateTime from, ZonedDateTime to, int ...stationIds) {
        Request request = createRequest(from, to, stationIds);
        executeRequest(request).ifPresent(s -> {
            LOG.info("WE DID IT BOIIIIS: {}", s);
        });
        return Collections.emptyList();
    }

    private Request createRequest(ZonedDateTime from, ZonedDateTime to, int[] stationIds) {
        Request request = new Request(from, to);

        Query bikeQuery = new Query("bikes.free");
        bikeQuery.addFilter(Filter.literalOrFilter("stationId", false, stationIds));

        Query lockQuery = new Query("locks.free");
        lockQuery.addFilter(Filter.literalOrFilter("stationId", false, stationIds));

        request.addQuery(bikeQuery);
        request.addQuery(lockQuery);

        return request;
    }

    private Optional<String> executeRequest(Request request) {
        HttpPost httpPost = new HttpPost(url + OpenTSDBPaths.QUERY_PATH);
        String body = request.toJson();
        LOG.info("BODY CONTENT:\n{}\n\n", body);
        httpPost.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));

        try {
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode < 200 || statusCode > 299) {
                throw new RuntimeException("OpenTSDB server responded with " + statusCode);
            }

            return Optional.of(EntityUtils.toString(response.getEntity(), "UTF-8"));
        } catch (RuntimeException|IOException e) {
            LOG.error("Failed to query OpenTSDB", e);
            return Optional.empty();
        }
    }
}
