package no.jstien.bikeapi.utils

import org.junit.Assert
import org.junit.Test

import java.util.ArrayList
import java.util.HashMap

class ListUtilsTest {
    @Test
    fun elementsAreCorrectlyDispatched() {
        val list = ArrayList<Int>()
        list.add(1)
        list.add(2)
        list.add(3)
        list.add(4)
        list.add(5)
        list.add(6)
        list.add(7)

        val seen = HashMap<Int, Int>()
        ListUtils.processBatchwise(list, 3) { sublist ->
            for (i in sublist) {
                (seen as java.util.Map<Int, Int>).putIfAbsent(i, 0)
                seen[i] = seen[i]!! + 1
            }
        }

        Assert.assertEquals(7, seen.keys.size.toLong())

        for (i in 1..7) {
            Assert.assertEquals(Integer.valueOf(1), seen[i])
        }
    }
}
