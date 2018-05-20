package no.jstien.bikeapi;

import no.jstien.bikeapi.requests.HeaderInterceptor;
import no.jstien.bikeapi.station.StationRepository;
import no.jstien.bikeapi.station.StationRepositoryImpl;
import no.jstien.bikeapi.station.StationTSDBUpdater;
import no.jstien.bikeapi.station.api.BikeAPI;
import no.jstien.bikeapi.station.api.BikeAPIImpl;
import no.jstien.bikeapi.tsdb.DevNullTSDB;
import no.jstien.bikeapi.tsdb.OpenTSDB;
import no.jstien.bikeapi.tsdb.TSDB;
import no.jstien.bikeapi.utils.VarUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
@EnableScheduling
@PropertySource("classpath:settings.properties")
public class JavaConfig {
    private final static Logger LOG = LogManager.getLogger();

    @Value("${api.identifier}")
    private String clientIdentifier;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new HeaderInterceptor(clientIdentifier)));
        return restTemplate;
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClientBuilder.create().build();
    }

    @Bean
    public StationRepository stationRepository(BikeAPI bikeAPI) {
        return new StationRepositoryImpl(bikeAPI);
    }

    @Bean
    public BikeAPI bikeAPI(RestTemplate restTemplate) {
        return new BikeAPIImpl(restTemplate);
    }

    @Bean
    @Autowired
    public TSDB tsdb(HttpClient httpClient) {
        String noTsdb = VarUtils.getEnv("BIKEAPI_NO_TSDB");
        if (noTsdb != null && noTsdb.equals("1")) {
            LOG.info("$BIKEAPI_NO_TSDB is 1 - using DevNullTSDB");
            return new DevNullTSDB();
        }

        String tsdbUrl = VarUtils.getEnv("TSDB_URL");
        if (tsdbUrl == null) {
            throw new NullPointerException("envvar '$TSDB_URL' is undefined");
        }

        return new OpenTSDB(tsdbUrl, httpClient);
    }

    @Bean
    @Autowired
    public StationTSDBUpdater stationTSDBUpdater(StationRepository stationRepository, TSDB tsdb) {
        return new StationTSDBUpdater(stationRepository, tsdb);
    }

}
