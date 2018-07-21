package no.jstien.bikeapi

import no.jstien.bikeapi.requests.BysykkelAuthInterceptor
import no.jstien.bikeapi.station.StationRepository
import no.jstien.bikeapi.station.StationRepositoryImpl
import no.jstien.bikeapi.station.StationTSDBUpdater
import no.jstien.bikeapi.station.api.BikeAPI
import no.jstien.bikeapi.station.api.BikeAPIImpl
import no.jstien.bikeapi.tsdb.read.DevNullStationTSDBReader
import no.jstien.bikeapi.tsdb.read.StationTSDBReader
import no.jstien.bikeapi.tsdb.read.StationTSDBReaderImpl
import no.jstien.bikeapi.tsdb.write.DevNullTSDBWriter
import no.jstien.bikeapi.tsdb.write.TSDBWriter
import no.jstien.bikeapi.tsdb.write.TSDBWriterImpl
import no.jstien.bikeapi.utils.VarUtils
import no.jstien.bikeapi.utils.holiday.HolidayRegistry
import no.jstien.bikeapi.utils.holiday.HolidayRepository
import no.jstien.bikeapi.utils.holiday.HolidayRepositoryImpl
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@EnableScheduling
@PropertySource("classpath:settings.properties")
open class Application {

    @Value("\${api.identifier}")
    private val clientIdentifier: String? = null

    @Bean
    open fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.interceptors = listOf<ClientHttpRequestInterceptor>(BysykkelAuthInterceptor(clientIdentifier!!))
        return restTemplate
    }

    @Bean
    open fun httpClient(): HttpClient {
        return HttpClientBuilder.create().build()
    }

    @Bean
    open fun stationRepository(bikeAPI: BikeAPI): StationRepository {
        return StationRepositoryImpl(bikeAPI)
    }

    @Bean
    open fun bikeAPI(restTemplate: RestTemplate): BikeAPI {
        return BikeAPIImpl(restTemplate)
    }

    @Bean
    @Autowired
    open fun tsdbWriter(httpClient: HttpClient): TSDBWriter {
        val noTsdb = VarUtils.getEnv("TSDB_NO_WRITE")
        if (noTsdb != null && noTsdb == "1") {
            LOG.info("\$TSDB_NO_WRITE is 1 - using DevNullTSDBWriter")
            return DevNullTSDBWriter()
        }

        val tsdbUrl = VarUtils.getEnv("TSDB_URL") ?: throw NullPointerException("envvar '\$TSDB_URL' is undefined")

        return TSDBWriterImpl(tsdbUrl, httpClient)
    }

    @Bean
    @Autowired
    open fun stationTSDBReader(httpClient: HttpClient): StationTSDBReader {
        val noTsdb = VarUtils.getEnv("TSDB_NO_READ")
        if (noTsdb != null && noTsdb == "1") {
            return DevNullStationTSDBReader()
        }

        val tsdbUrl = VarUtils.getEnv("TSDB_URL") ?: throw NullPointerException("envvar '\$TSDB_URL' is undefined")

        return StationTSDBReaderImpl(tsdbUrl, httpClient)
    }

    @Bean
    @Autowired
    open fun stationTSDBUpdater(stationRepository: StationRepository, tsdbWriter: TSDBWriter): StationTSDBUpdater {
        return StationTSDBUpdater(stationRepository, tsdbWriter)
    }

    @Bean
    @Autowired
    open fun holidayRepository(httpClient: HttpClient): HolidayRepository {
        return HolidayRepositoryImpl(httpClient)
    }

    @Bean
    @Autowired
    open fun holidayRegistry(holidayRepository: HolidayRepository): HolidayRegistry {
        return HolidayRegistry(holidayRepository)
    }

    companion object {
        private val LOG = LogManager.getLogger()
    }
}
