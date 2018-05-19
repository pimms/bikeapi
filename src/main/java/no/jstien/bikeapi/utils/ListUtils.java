package no.jstien.bikeapi.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ListUtils {
    public static <T> void processBatchwise(List<T> list, int maxBatchSize, Consumer<List<T>> callback) {
        int itemNo = 0;
        int batchItem = 0;
        List<T> buffer = new ArrayList<>();

        while (itemNo < list.size()) {
            buffer.add(list.get(itemNo));

            itemNo++;
            batchItem++;

            if (batchItem >= maxBatchSize) {
                callback.accept(Collections.unmodifiableList(buffer));
                batchItem = 0;
                buffer.clear();
            }
        }

        if (!buffer.isEmpty()) {
            callback.accept(Collections.unmodifiableList(buffer));
            buffer.clear();
        }
    }
}
