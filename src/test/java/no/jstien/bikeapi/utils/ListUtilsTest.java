package no.jstien.bikeapi.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListUtilsTest {
    @Test
    public void elementsAreCorrectlyDispatched() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);

        Map<Integer,Integer> seen = new HashMap<>();
        ListUtils.processBatchwise(list, 3, sublist -> {
            for (Integer i: sublist) {
                seen.putIfAbsent(i, 0);
                seen.put(i, seen.get(i) + 1);
            }
        });

        Assert.assertEquals(7, seen.keySet().size());

        for (int i=1; i<=7; i++) {
            Assert.assertEquals(Integer.valueOf(1), seen.get(i));
        }
    }
}
