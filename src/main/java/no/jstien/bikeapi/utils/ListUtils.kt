package no.jstien.bikeapi.utils

import java.util.ArrayList
import java.util.Collections
import java.util.function.Consumer

object ListUtils {
    fun <T> processBatchwise(list: List<T>, maxBatchSize: Int, callback: (List<T>) -> Unit) {
        var itemNo = 0
        var batchItem = 0
        val buffer = ArrayList<T>()

        while (itemNo < list.size) {
            buffer.add(list[itemNo])

            itemNo++
            batchItem++

            if (batchItem >= maxBatchSize) {
                callback(Collections.unmodifiableList(buffer))
                batchItem = 0
                buffer.clear()
            }
        }

        if (!buffer.isEmpty()) {
            callback(Collections.unmodifiableList(buffer))
            buffer.clear()
        }
    }
}
