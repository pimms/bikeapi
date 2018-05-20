package no.jstien.bikeapi.tsdb.read;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Filter {
    private String type;
    private String tagk;
    private String filter;
    private boolean groupBy;

    public static Filter literalOrFilter(String tagk, boolean groupBy, int ...tagValues) {
        String[] objs = new String[tagValues.length];
        for (int i=0; i<tagValues.length; i++) {
            objs[i] = String.valueOf(tagValues[i]);
        }

        return literalOrFilter(tagk, groupBy, objs);
    }

    public static Filter literalOrFilter(String tagk, boolean groupBy, String ...tagValues) {
        Filter filter = new Filter();
        filter.type = "literal_or";
        filter.tagk = tagk;
        filter.groupBy = groupBy;
        filter.setFilter(tagValues);
        return filter;
    }

    public void setFilter(String ...tagValues) {
        filter = Arrays.stream(tagValues)
                .map(Object::toString)
                .collect(Collectors.joining("|"));
    }
}
