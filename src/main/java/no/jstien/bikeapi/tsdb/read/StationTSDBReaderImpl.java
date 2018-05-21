package no.jstien.bikeapi.tsdb.read;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.*;

public class StationTSDBReaderImpl implements StationTSDBReader {
    private static final Logger LOG = LogManager.getLogger();

    private HttpClient httpClient;
    private String url;

    public StationTSDBReaderImpl(String url, HttpClient httpClient) {
        this.url = url;
        this.httpClient = httpClient;
    }

    @Override
    public StationHistory queryStation(ZonedDateTime from, ZonedDateTime to, int stationId) {
        Map<Integer,StationHistory> result = queryStations(from, to, stationId);

        if (!result.containsKey(stationId))
            throw new RuntimeException("Expected to find station in result map");

        return result.get(stationId);
    }

    @Override
    public Map<Integer,StationHistory> queryStations(ZonedDateTime from, ZonedDateTime to, int ...stationIds) {
        Request request = new Request(from, to);
        Arrays.stream(stationIds).forEach(id -> addQueries(request, id));

        String json = executeRequest(request);
        return parseResult(json, stationIds);
    }

    private void addQueries(Request request, int stationId) {
        Query bikeQuery = new Query("bikes.free");
        bikeQuery.addFilter(Filter.literalOrFilter("stationId", false, String.valueOf(stationId)));

        Query lockQuery = new Query("locks.free");
        lockQuery.addFilter(Filter.literalOrFilter("stationId", false, String.valueOf(stationId)));

        request.addQuery(bikeQuery);
        request.addQuery(lockQuery);
    }

    private String executeRequest(Request request) {
        HttpPost httpPost = new HttpPost(url + OpenTSDBPaths.QUERY_PATH);
        String body = request.toJson();
        LOG.info("oTSDB Query: {}", body);
        httpPost.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));

        try {
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode < 200 || statusCode > 299) {
                throw new TSDBException("OpenTSDB server responded with " + statusCode, statusCode);
            }

            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (TSDBException e) {
            throw e;
        } catch (RuntimeException|IOException e) {
            throw new RuntimeException("Failed to query TSDB", e);
        }
    }

    private Map<Integer,StationHistory> parseResult(String json, int ...stationIds) {
        List<TSDBResponseDTO> dtoList = parseToResponseDTO(json);
        Map<Integer,StationHistory> result = new HashMap<>();

        Arrays.stream(stationIds).forEach(id -> result.put(id, new StationHistory(id)));

        dtoList.forEach(dto -> {
            int stationId = Integer.valueOf(dto.tags.get("stationId"));
            StationHistory history = result.get(stationId);

            if (dto.metric.equals("bikes.free")) {
                history.setFreeBikes(dto.dps);
            } else if (dto.metric.equals("locks.free")) {
                history.setFreeLocks(dto.dps);
            } else {
                throw new RuntimeException("Unable to map metric '"+dto.metric+"'");
            }
        });

        return result;
    }

    private List<TSDBResponseDTO> parseToResponseDTO(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(TimeSerie.class, new TimeSerieJsonDeserializer());
        Gson gson = builder.create();
        Type type = new TypeToken<List<TSDBResponseDTO>>(){}.getType();
        return gson.fromJson(json, type);
    }

}
