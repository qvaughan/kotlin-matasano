package codec

/*  Challenge 1: Convert Hex to base 64.
 *
 */

object Codec {

    val hexCharacters = "0123456789ABCDEF"
    val base64Characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"


    fun fromHex(hex: String): List<Int> {
        fun convertPair(pair: String): Int {
            val a = pair[0].toUpperCase()
            val b = pair[1].toUpperCase()
            return ((hexCharacters.indexOf(a) shl 4) + hexCharacters.indexOf(b))
        }

        tailrec fun fromHex(hex: String, acc: List<Int>): List<Int> {
            if (hex.length == 0) return acc
            else return fromHex(hex.drop(2), acc + convertPair(hex.take(2)))
        }

        if (hex.length % 2 != 0) return fromHex("0" + hex)
        else return fromHex(hex, emptyList())
    }


    fun toHex(bytes: List<Int>): String {
        fun byteToHex(byte: Int): String {
            val a = (byte shr 4) and 0x0F
            val b = byte and 0x0F
            val aChar = hexCharacters[a]
            val bChar = hexCharacters[b]
            return "$aChar$bChar"
        }

        tailrec fun toHex(bytes: List<Int>, acc: String): String {
            if (bytes.size == 0) return acc
            else return toHex(bytes.drop(1), acc + byteToHex(bytes.first()))
        }

        return toHex(bytes, "")

    }


    fun fromBase64(base64: String): List<Int> {
        tailrec fun fromBase64(y: List<Int>, acc: List<Int>): List<Int> {
            if (y.size == 0) return acc
            else {
                val fourVals = y.take(4)
                val byte1 = (fourVals[0] shl 2) + (fourVals[1] shr 4)
                val byte2 = if (fourVals[2] == -1) 0 else ((fourVals[1] and 0x0F) shl 4) + ((fourVals[2] shr 2) and 0x0F)
                val byte3 = if (fourVals[3] == -1) 0 else ((fourVals[2] and 0x03) shl 6) + fourVals[3]
                return fromBase64(y.drop(4), acc + byte1 + byte2 + byte3)
            }
        }
        val x = base64.map {base64Characters.indexOf(it)}
        return fromBase64(x, emptyList())
    }


    fun toBase64(bytes: List<Int>): String {
        fun convertThreeBytes(byte1: Int, byte2: Int?, byte3: Int?): String {
            val a = byte1 shr 2 and 0x3F
            val b = ((byte1 and 0x03) shl 4) + (if (byte2 != null) byte2 shr 4 and 0x0F else 0)
            val c: Int? = if (byte2 == null) null else ((byte2 and 0x0F) shl 2) + if (byte3 != null) (byte3 shr 6) and 0x03 else 0
            val d: Int? = if (byte3 == null) null else ((byte3 and 0x3F))

            val charA: Char = base64Characters[a]
            val charB: Char = base64Characters[b]
            val charC: Char = if (c != null) base64Characters[c] else '='
            val charD: Char = if (d != null) base64Characters[d] else '='

            return "$charA$charB$charC$charD"

        }

        tailrec fun toBase64(bytes: List<Int>, acc: String): String {
            if (bytes.size == 0) return acc
            else {
                val byte1 = bytes[0]
                val byte2 = if (bytes.size > 1) bytes[1] else null
                val byte3 = if (bytes.size > 2) bytes[2] else null
                return toBase64(bytes.drop(3), acc + convertThreeBytes(byte1, byte2, byte3))
            }
        }

        return toBase64(bytes, "")
    }
}