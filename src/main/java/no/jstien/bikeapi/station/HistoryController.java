package no.jstien.bikeapi.station;

import no.jstien.bikeapi.tsdb.read.RequestFactory;
import no.jstien.bikeapi.tsdb.read.StationHistory;
import no.jstien.bikeapi.tsdb.read.StationTSDBReader;
import no.jstien.bikeapi.tsdb.read.TSDBException;
import no.jstien.bikeapi.tsdb.write.DatumBuilder;
import no.jstien.bikeapi.tsdb.write.TSDBWriter;
import no.jstien.bikeapi.utils.AnalogueDateFinder;
import no.jstien.bikeapi.utils.CalendarUtils;
import no.jstien.bikeapi.utils.holiday.HolidayRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

@RestController
public class HistoryController {
    private static final Logger LOG = LogManager.getLogger();

    private StationTSDBReader tsdbReader;
    private StationRepository stationRepository;
    private DatumBuilder httpCallMetric;
    private HolidayRegistry holidayRegistry;

    @Autowired
    public HistoryController(TSDBWriter tsdbWriter,
                             StationRepository stationRepository,
                             StationTSDBReader stationTSDBReader,
                             HolidayRegistry holidayRegistry) {
        this.stationRepository = stationRepository;
        this.tsdbReader = stationTSDBReader;
        this.httpCallMetric = tsdbWriter.createDatumBuilder("http_calls").addTagKey("endpoint").addTagKey("method");
        this.holidayRegistry = holidayRegistry;
    }

    @RequestMapping("/stations/history")
    public Collection<StationHistory> getStationHistory(
            HttpServletResponse response,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to,
            @RequestParam("id") int[] stationIds,
            @RequestParam(value="dsm", defaultValue = "0") int downsampleMinutes)
    {
        httpCallMetric.addDatum("history_controller", "/history");

        if (ChronoUnit.HOURS.between(from, to) > 30) {
            response.setStatus(400);
            throw new RuntimeException("Cannot query intervals larger than 30 hours");
        }

        if (stationIds.length > 5) {
            response.setStatus(400);
            throw new RuntimeException("Cannot query for more than 4 stations at a time");
        }

        try {
            RequestFactory requestFactory = new RequestFactory(from, to, stationIds);

            if (downsampleMinutes > 0)
                requestFactory.setDownsampleMinutes(downsampleMinutes);

            return tsdbReader.queryStations(requestFactory).values();
        } catch (TSDBException e) {
            LOG.info("/history call failed (TSDB): " + e.getMessage());
            response.setStatus(e.getStatusCode());
        } catch (Throwable e) {
            LOG.error("/history call failed", e);
            response.setStatus(500);
        }

        return null;
    }


    @RequestMapping("/stations/prediction")
    public StationHistory prediction(@RequestParam("id") int stationId) {
        httpCallMetric.addDatum("history_controller", "/prediction");

        AnalogueDateFinder dateFinder = new AnalogueDateFinder(holidayRegistry);
        AvailabilityPredictor predictor = new AvailabilityPredictor(tsdbReader, dateFinder);
        return predictor.predictForStation(stationId, CalendarUtils.currentDate());
    }

}
