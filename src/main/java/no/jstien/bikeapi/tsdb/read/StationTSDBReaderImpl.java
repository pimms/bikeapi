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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationTSDBReaderImpl implements StationTSDBReader {
    private static final Logger LOG = LogManager.getLogger();

    private HttpClient httpClient;
    private String url;

    public StationTSDBReaderImpl(String url, HttpClient httpClient) {
        this.url = url;
        this.httpClient = httpClient;
    }

    @Override
    public Map<Integer,StationHistory> queryStations(RequestFactory requestFactory) {
        Request request = requestFactory.create();
        String json = executeRequest(request);
        return parseResult(json);
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

    private Map<Integer,StationHistory> parseResult(String json) {
        List<TSDBResponseDTO> dtoList = parseToResponseDTO(json);
        Map<Integer,StationHistory> result = new HashMap<>();

        dtoList.forEach(dto -> {
            int stationId = Integer.valueOf(dto.tags.get("stationId"));

            if (!result.containsKey(stationId))
                result.put(stationId, new StationHistory(stationId));

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
