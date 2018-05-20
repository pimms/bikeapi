package no.jstien.bikeapi.tsdb.write;

import no.jstien.bikeapi.tsdb.Datum;

import java.util.ArrayList;
import java.util.List;

public class DatumBuilder {
    private String metricName;
    private List<String> tagKeys = new ArrayList<>();
    private TSDBWriter tsdbWriter;


    public DatumBuilder(String metricName, TSDBWriter tsdbWriter) {
        this.metricName = metricName;
        this.tsdbWriter = tsdbWriter;
    }

    public DatumBuilder addTagKey(String tagKey) {
        tagKeys.add(tagKey);
        return this;
    }

    public void addDatum(String ...tagValues) {
        addDatum(1L, tagValues);
    }

    public void addDatum(long value, String ...tagValues) {
        verifyTagValues(tagKeys, tagValues);

        Datum datum = new Datum(metricName, value);
        for (int i=0; i<tagKeys.size(); i++) {
            datum.addTag(tagKeys.get(i), tagValues[i]);
        }

        tsdbWriter.addDatum(datum);
    }


    private void verifyTagValues(List<String> keys, String[] values) {
        if (keys.size() != values.length) {
            StringBuilder sb = new StringBuilder();
            sb.append("Expected " + keys.size() + " tag values, got " + values.length + "! ");
            sb.append("Expectedd values for keys: ");
            keys.forEach(key -> sb.append(key + " "));
            throw new RuntimeException(sb.toString());
        }
    }
}
