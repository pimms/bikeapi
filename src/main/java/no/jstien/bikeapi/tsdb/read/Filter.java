package no.jstien.bikeapi.tsdb.read;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Filter {
    private String type;
    private String tagk;
    private String filter;
    private boolean groupBy;

    public static Filter literalOrFilter(String tagk, boolean groupBy, int ...tagValues) {
        Object[] objs = new Object[tagValues.length];
        for (int i=0; i<tagValues.length; i++) {
            objs[i] = tagValues[i];
        }

        return literalOrFilter(tagk, groupBy, objs);
    }

    public static Filter literalOrFilter(String tagk, boolean groupBy, Object ...tagValues) {
        Filter filter = new Filter();
        filter.type = "literal_or";
        filter.tagk = tagk;
        filter.groupBy = groupBy;
        filter.filter = Arrays.stream(tagValues)
                              .map(Object::toString)
                              .collect(Collectors.joining("|"));
        return filter;
    }
}
