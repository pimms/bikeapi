package no.jstien.bikeapi;

import no.jstien.bikeapi.requests.BysykkelAuthInterceptor;
import no.jstien.bikeapi.station.StationRepository;
import no.jstien.bikeapi.station.StationRepositoryImpl;
import no.jstien.bikeapi.station.StationTSDBUpdater;
import no.jstien.bikeapi.station.api.BikeAPI;
import no.jstien.bikeapi.station.api.BikeAPIImpl;
import no.jstien.bikeapi.tsdb.read.DevNullStationTSDBReader;
import no.jstien.bikeapi.tsdb.read.StationTSDBReader;
import no.jstien.bikeapi.tsdb.read.StationTSDBReaderImpl;
import no.jstien.bikeapi.tsdb.write.DevNullTSDBWriter;
import no.jstien.bikeapi.tsdb.write.TSDBWriter;
import no.jstien.bikeapi.tsdb.write.TSDBWriterImpl;
import no.jstien.bikeapi.utils.VarUtils;
import no.jstien.bikeapi.utils.holiday.HolidayRegistry;
import no.jstien.bikeapi.utils.holiday.HolidayRepository;
import no.jstien.bikeapi.utils.holiday.HolidayRepositoryImpl;
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
        restTemplate.setInterceptors(Collections.singletonList(new BysykkelAuthInterceptor(clientIdentifier)));
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
    public TSDBWriter tsdbWriter(HttpClient httpClient) {
        String noTsdb = VarUtils.getEnv("TSDB_NO_WRITE");
        if (noTsdb != null && noTsdb.equals("1")) {
            LOG.info("$TSDB_NO_WRITE is 1 - using DevNullTSDBWriter");
            return new DevNullTSDBWriter();
        }

        String tsdbUrl = VarUtils.getEnv("TSDB_URL");
        if (tsdbUrl == null) {
            throw new NullPointerException("envvar '$TSDB_URL' is undefined");
        }

        return new TSDBWriterImpl(tsdbUrl, httpClient);
    }

    @Bean
    @Autowired
    public StationTSDBReader stationTSDBReader(HttpClient httpClient) {
        String noTsdb = VarUtils.getEnv("TSDB_NO_READ");
        if (noTsdb != null && noTsdb.equals("1")) {
            return new DevNullStationTSDBReader();
        }

        String tsdbUrl = VarUtils.getEnv("TSDB_URL");
        if (tsdbUrl == null) {
            throw new NullPointerException("envvar '$TSDB_URL' is undefined");
        }

        return new StationTSDBReaderImpl(tsdbUrl, httpClient);
    }

    @Bean
    @Autowired
    public StationTSDBUpdater stationTSDBUpdater(StationRepository stationRepository, TSDBWriter tsdbWriter) {
        return new StationTSDBUpdater(stationRepository, tsdbWriter);
    }

    @Bean
    @Autowired
    public HolidayRepository holidayRepository(HttpClient httpClient) {
        return new HolidayRepositoryImpl(httpClient);
    }

    @Bean
    @Autowired
    public HolidayRegistry holidayRegistry(HolidayRepository holidayRepository) {
        return new HolidayRegistry(holidayRepository);
    }
}
