package common

object Common {
    fun hamming(byte1: Int, byte2: Int): Int {
        val diff = byte1.toInt() xor byte2.toInt()
        tailrec fun hamming(diff: Int, acc: Int, bit: Int): Int {
            if (bit > 7) return acc
            else return hamming(diff, acc + ((diff shr bit) and 0x01), bit + 1)
        }
        return hamming(diff, 0, 0)
    }

    fun hamming(bytes1: List<Int>, bytes2: List<Int>): Int {
        return bytes1.zip(bytes2).map { it ->
            hamming(it.first.toInt(), it.second.toInt())
        }.sum()
    }

    fun hamming(str1: String, str2: String): Int {
        return hamming(str1.map { it.toInt() }, str2.map { it.toInt() })
    }

    fun <T> List<T>.splitIntoGroupsOfN(groupSize: Int): List<List<T>> {
        fun splitIntoGroupsOfN(rem: List<T>, acc: List<List<T>>): List<List<T>> {
            if (rem.size == 0) return acc
            else return splitIntoGroupsOfN(rem.drop(groupSize), acc + listOf(rem.take(groupSize)))
        }
        return splitIntoGroupsOfN(this, emptyList())
    }
}