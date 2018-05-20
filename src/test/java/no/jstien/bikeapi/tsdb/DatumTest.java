package no.jstien.bikeapi.tsdb;

import com.google.gson.Gson;
import no.jstien.bikeapi.tsdb.Datum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DatumTest {
    private static String datumJsonString = "{\"metric\":\"metric\",\"value\":22,\"timestamp\":1111,\"tags\":{\"key\":\"value\"}}";
    private Datum datum;

    @Before
    public void setup() {
        datum = new Datum("metric", 22, 1111);
        datum.addTag("key", "value");
    }

    @Test
    public void serializationMatchesSchema() {
        Gson gson = new Gson();
        String json = gson.toJson(datum);

        Assert.assertEquals(datumJsonString, json);
    }

    @Test
    public void serializationOfArrayMatchesSchema() {
        List<Datum> list = new ArrayList<>();
        list.add(datum);
        list.add(datum);

        Gson gson = new Gson();
        String json = gson.toJson(list);

        String expected = "["+datumJsonString+","+datumJsonString+"]";
        Assert.assertEquals(expected, json);
    }
}
