package no.jstien.bikeapi.station;

import no.jstien.bikeapi.tsdb.read.StationHistory;
import no.jstien.bikeapi.tsdb.read.StationTSDBReader;
import no.jstien.bikeapi.tsdb.read.TSDBException;
import no.jstien.bikeapi.tsdb.write.DatumBuilder;
import no.jstien.bikeapi.tsdb.write.TSDBWriter;
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
import java.util.Map;

@RestController
public class HistoryController {
    private static final Logger LOG = LogManager.getLogger();

    private StationTSDBReader tsdbReader;
    private StationRepository stationRepository;
    private DatumBuilder httpCallMetric;

    @Autowired
    public HistoryController(TSDBWriter tsdbWriter, StationRepository stationRepository, StationTSDBReader stationTSDBReader) {
        this.stationRepository = stationRepository;
        this.tsdbReader = stationTSDBReader;
        this.httpCallMetric = tsdbWriter.createDatumBuilder("http_calls").addTagKey("endpoint").addTagKey("method");
    }

    @RequestMapping("/history")
    public Collection<StationHistory> what(
            HttpServletResponse response,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to,
            @RequestParam("id") int[] stationIds,
            @RequestParam(value="dsm", defaultValue = "1") int downsampleMinutes)
    {
        if (ChronoUnit.HOURS.between(from, to) > 30) {
            response.setStatus(400);
            throw new RuntimeException("Cannot query intervals larger than 30 hours");
        }

        if (stationIds.length > 4) {
            response.setStatus(400);
            throw new RuntimeException("Cannot query for more than 4 stations at a time");
        }

        if (downsampleMinutes < 1) {
            response.setStatus(400);
            throw new RuntimeException("Invalid value for 'dsm' - must be a positive value.");
        }

        try {
            httpCallMetric.addDatum("history_controller", "/history");
            Map<Integer, StationHistory> historyMap;
            historyMap = tsdbReader.queryStations(from, to, stationIds);
            return historyMap.values();
        } catch (TSDBException e) {
            LOG.info("/history call failed (TSDB): " + e.getMessage());
            response.setStatus(e.getStatusCode());
        } catch (Throwable e) {
            LOG.error("/history call failed", e);
            response.setStatus(500);
        }

        return null;
    }

}
