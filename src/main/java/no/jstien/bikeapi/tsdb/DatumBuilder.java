package no.jstien.bikeapi.tsdb;

import java.util.ArrayList;
import java.util.List;

public class DatumBuilder {
    private String metricName;
    private List<String> tagKeys = new ArrayList<>();
    private TSDB tsdb;


    public DatumBuilder(String metricName, TSDB tsdb) {
        this.metricName = metricName;
        this.tsdb = tsdb;
    }

    public DatumBuilder addTagKey(String tagKey) {
        tagKeys.add(tagKey);
        return this;
    }

    public void addDatum(String ...tagValues) {
        addDatum(1.0, tagValues);
    }

    public void addDatum(double value, String ...tagValues) {
        verifyTagValues(tagKeys, tagValues);

        Datum datum = new Datum(metricName, value);
        for (int i=0; i<tagKeys.size(); i++) {
            datum.addTag(tagKeys.get(i), tagValues[i]);
        }

        tsdb.addDatum(datum);
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
