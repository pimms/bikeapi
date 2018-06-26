package no.jstien.bikeapi.station;

import no.jstien.bikeapi.tsdb.read.*;
import no.jstien.bikeapi.utils.AnalogueDateFinder;
import no.jstien.bikeapi.utils.CalendarUtils;

import java.time.ZonedDateTime;
import java.util.*;

public class AvailabilityPredictor {
    private static final int MINUTES_PER_DAY = 60 * 24;
    private static final int DEFAULT_DOWNSAMPLE_MINUTES = 15;

    private StationTSDBReader tsdbReader;
    private AnalogueDateFinder dateFinder;
    private int downsampleMinutes = DEFAULT_DOWNSAMPLE_MINUTES;

    public AvailabilityPredictor(StationTSDBReader tsdbReader, AnalogueDateFinder analogueDateFinder) {
        this.tsdbReader = tsdbReader;
        this.dateFinder = analogueDateFinder;
    }

    public void setDownsampleMinutes(int minutes) {
        this.downsampleMinutes = minutes;
    }

    public StationHistory predictForStation(int stationId, Calendar predictionDate) {
        // TODO: This is quite nasty, and I'm honestly surprised if it turns out to not be
        // buggy AF. Anyways, it's an OK POC I guess.
        List<Calendar> analogues = dateFinder.findAnaloguesForDay(predictionDate);

        final int numElems = MINUTES_PER_DAY / downsampleMinutes;
        int[] bikesCount = new int[numElems];
        double[] bikes = new double[numElems];
        int[] locksCount = new int[numElems];
        double[] locks = new double[numElems];

        analogues.forEach(day -> {
            GregorianCalendar startDate = CalendarUtils.startOfDay(day);
            final long startTime = startDate.getTimeInMillis() / 1000L;

            ZonedDateTime from = startDate.toZonedDateTime();
            ZonedDateTime to = CalendarUtils.endOfDay(day).toZonedDateTime();
            RequestFactory requestFactory = new RequestFactory(from, to, stationId);
            requestFactory.setDownsampleMinutes(downsampleMinutes);

            Map<Integer, StationHistory> history = tsdbReader.queryStations(requestFactory);
            history.values().stream()
                .forEach(h -> {
                    processTimeSerie(h.getFreeBikes(), startTime, bikes, bikesCount);
                    processTimeSerie(h.getFreeLocks(), startTime, locks, locksCount);
                });
        });

        // Cool, let's try to create a StationHistory from it
        reduceHistogram(bikes, bikesCount);
        reduceHistogram(locks, locksCount);

        final long timeZero = CalendarUtils.startOfDay(predictionDate).getTimeInMillis() / 1000L;
        TimeSerie bikeSerie = toTimeSerie(bikes, timeZero);
        TimeSerie lockSerie = toTimeSerie(locks, timeZero);
        return new StationHistory(stationId, bikeSerie, lockSerie);
    }

    private void processTimeSerie(TimeSerie timeSerie, long startTime, double[] dest, int[] counts) {
        timeSerie.getDataPoints().forEach(dp -> {
            final long timestamp = dp.getTimestamp();
            final long index = (timestamp - startTime) / (downsampleMinutes * 60);
            dest[(int)index] += dp.getValue();
            counts[(int)index]++;
        });
    }

    private void reduceHistogram(double[] vals, int[] counts) {
        for (int i=0; i<vals.length; i++) {
            if (counts[i] != 0) {
                vals[i] /= (double)counts[i];
            }
        }
    }

    private TimeSerie toTimeSerie(double[] vals, long firstTimeStamp) {
        TimeSerie ts = new TimeSerie();

        final long deltaTime = (downsampleMinutes * 60);
        long time = firstTimeStamp;
        for (int i=0; i<vals.length; i++) {
            ts.addDataPoint(new DataPoint(time, vals[i]));
            time += deltaTime;
        }

        return ts;
    }

}
