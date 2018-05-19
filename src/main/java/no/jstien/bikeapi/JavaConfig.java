package no.jstien.bikeapi;

import no.jstien.bikeapi.requests.HeaderInterceptor;
import no.jstien.bikeapi.station.StationRepository;
import no.jstien.bikeapi.station.StationRepositoryImpl;
import no.jstien.bikeapi.station.api.BikeAPI;
import no.jstien.bikeapi.station.api.BikeAPIImpl;
import no.jstien.bikeapi.tsdb.OpenTSDB;
import no.jstien.bikeapi.tsdb.TSDB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
@EnableScheduling
@PropertySource("classpath:api-identifier.properties")
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
    public StationRepository stationDAO(BikeAPI bikeAPI) {
        return new StationRepositoryImpl(bikeAPI);
    }

    @Bean
    public BikeAPI bikeAPI(RestTemplate restTemplate) {
        return new BikeAPIImpl(restTemplate);
    }

    @Bean
    public TSDB tsdb() {
        return new OpenTSDB();
    }

}
