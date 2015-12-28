package set1

import codec.Codec
import common.Utils
import common.Common
import common.Common.splitIntoGroupsOfN

import org.junit.Test
import scorer.CharacterFrequencyScorer

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.test.assertEquals
import kotlin.test.assertTrue


/**
 * @author Michael Vaughan
 */
class Set1Tests {



    data class Guess(val character: Char, val input: String, val guess: String, val score: Float)

    /*Convert hex to base64
     *
     * The string:
     * 49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d
     *
     * Should produce:
     * SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t
     *
     * So go ahead and make that happen. You'll need to use this code for the rest of the exercises.
     *
     * Cryptopals Rule
     * Always operate on raw bytes, never on encoded strings. Only use hex and base64 for pretty-printing.
     */
    @Test
    fun challenge1() {
        val expected = "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t"
        val hex = Codec.fromHex("49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d")
        val base64 = Codec.toBase64(hex)
        assertEquals(expected, base64)
    }


    /* Fixed XOR
     *
     * Write a function that takes two equal-length buffers and produces their XOR combination.
     *
     * If your function works properly, then when you feed it the string: 1c0111001f010100061a024b53535009181c
     *
     * ... after hex decoding, and when XOR'd against: 686974207468652062756c6c277320657965
     *
     * ... should produce: 746865206b696420646f6e277420706c6179
     */
    @Test
    fun challenge2() {
        val input = "1c0111001f010100061a024b53535009181c"
        val xorArg = "686974207468652062756c6c277320657965"
        val expected = "746865206b696420646f6e277420706c6179"
        val inputBytes = Codec.fromHex(input)
        val xorArgBytes = Codec.fromHex(xorArg)

        val zipped = inputBytes.zip(xorArgBytes)
        val xored = zipped.map { (it.first.toInt() xor it.second.toInt()) }
        val xoredHex = Codec.toHex(xored)
        assertEquals(expected, xoredHex.toLowerCase())

    }


    /* Single-byte XOR cipher
     *
     * The hex encoded string:
     *
     * 1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736
     * ... has been XOR'd against a single character. Find the key, decrypt the message.
     *
     * You can do this by hand. But don't: write code to do it for you.
     *
     * How? Devise some method for "scoring" a piece of English plaintext. Character frequency is a good metric.
     * Evaluate each output and choose the one with the best score.
     *
     */
    @Test
    fun challenge3() {



        val input = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736"
        val inputBytes = Codec.fromHex(input)


        val guesses = (32 until 126).map { c ->

            val str = String(inputBytes.map { b ->
                (c.xor(b.toInt())).toChar()
            }.toCharArray())

            Guess(c.toChar(), input, str, CharacterFrequencyScorer.score(str))
        }

        val sorted = guesses.sortedBy { it.score }
        assertEquals("cooking mc's like a pound of bacon", sorted.first().guess.toLowerCase())

    }


    /* Detect single-character XOR
     *
     * One of the 60-character strings in this file has been encrypted by single-character XOR.
     *
     * Find it.
     *
     * (Your code from #3 should help.)
     *
     */
    @Test
    fun challenge4() {
        val data = Utils.readLinesFromClasspathFile("4.txt")

        val best = data.map { l ->
            val inputBytes = Codec.fromHex(l)
            val guesses = (32 until 126).map { c ->

                val str = String(inputBytes.map { b ->
                    (c.xor(b.toInt())).toChar()
                }.toCharArray())

                Guess(c.toChar(), l, str, CharacterFrequencyScorer.score(str))
            }

            guesses.sortedBy { it.score }.first()

        }.sortedBy { it.score }

        assertEquals("Now that the party is jumping\n", best.first().guess)

    }


    /* Implement repeating-key XOR
     * Here is the opening stanza of an important work of the English language:
     *
     * Burning 'em, if you ain't quick and nimble
     * I go crazy when I hear a cymbal
     *
     * Encrypt it, under the key "ICE", using repeating-key XOR.
     * In repeating-key XOR, you'll sequentially apply each byte of the key; the first byte of plaintext will be XOR'd
     * against I, the next C, the next E, then I again for the 4th byte, and so on.
     * It should come out to:
     *
     * 0b3637272a2b2e63622c2e69692a23693a2a3c6324202d623d63343c2a26226324272765272
     * a282b2f20430a652e2c652a3124333a653e2b2027630c692b20283165286326302e27282f
     *
     * Encrypt a bunch of stuff using your repeating-key XOR function. Encrypt your mail. Encrypt your password file.
     * Your .sig file. Get a feel for it. I promise, we aren't wasting your time with this.
     *
     */
    @Test
    fun challenge5() {
        val input = """Burning 'em, if you ain't quick and nimble
                  |I go crazy when I hear a cymbal""".trimMargin()
        val key = "ICE"
        val expected = "0b3637272a2b2e63622c2e69692a23693a2a3c6324202d623d63343c2a26226324272765272a282b2f20430a652e2c652a3124333a653e2b2027630c692b20283165286326302e27282f"
        val xoredbytes = input.mapIndexed { i, c -> (c.toInt() xor key[i % key.length].toInt()) }
        val result = Codec.toHex(xoredbytes)

        assertEquals(expected, result.toLowerCase())
    }


    /* Break repeating-key XOR
     *
     * It is officially on, now.
     *
     * This challenge isn't conceptually hard, but it involves actual error-prone coding. The other challenges in this
     * set are there to bring you up to speed. This one is there to qualify you. If you can do this one, you're probably
     * just fine up to Set 6.
     * There's a file here. It's been base64'd after being encrypted with repeating-key XOR.
     *
     * Decrypt it.
     *
     * Here's how:
     * 1. Let KEYSIZE be the guessed length of the key; try values from 2 to (say) 40.
     *
     * 2. Write a function to compute the edit distance/Hamming distance between two strings. The Hamming distance is
     *    just the number of differing bits. The distance between:
     *
     *    this is a test
     *
     *    and
     *
     *    wokka wokka!!!
     *
     *    is 37. Make sure your code agrees before you proceed.
     *
     * 3. For each KEYSIZE, take the first KEYSIZE worth of bytes, and the second KEYSIZE worth of bytes, and find the
     *    edit distance between them. Normalize this result by dividing by KEYSIZE.
     *
     * 4. The KEYSIZE with the smallest normalized edit distance is probably the key. You could proceed perhaps with the
     *    smallest 2-3 KEYSIZE values. Or take 4 KEYSIZE blocks instead of 2 and average the distances.
     *
     * 5. Now that you probably know the KEYSIZE: break the ciphertext into blocks of KEYSIZE length.
     *
     * 6. Now transpose the blocks: make a block that is the first byte of every block, and a block that is the second
     *    byte of every block, and so on.
     *
     * 7. Solve each block as if it was single-character XOR. You already have code to do this.
     *
     * 8. For each block, the single-byte XOR key that produces the best looking histogram is the repeating-key XOR key
     *    byte for that block. Put them together and you have the key.
     *
     * This code is going to turn out to be surprisingly useful later on. Breaking repeating-key XOR ("Vigenere")
     * statistically is obviously an academic exercise, a "Crypto 101" thing. But more people "know how" to break it
     * than can actually break it, and a similar technique breaks something much more important.
     */
    @Test
    fun challenge6() {

        assertEquals(37, Common.hamming("this is a test", "wokka wokka!!!"))

        val data = Codec.fromBase64(Utils.readLinesFromClasspathFile("6.txt").joinToString(""))

        // Attempt to guess the key size by calculating hamming distances on block sizes between 2 and 40.
        val guessedKeysize = (2 until 40).map{ keysize ->
            // The last byte is dropped from the grouped bytes because it is possible it won't contain the same
            // number of bytes currently being tested.
            val groupedBytesMinusLast = data.splitIntoGroupsOfN(keysize).dropLast(1)

            // Calculate the hamming distance between each block against every other block.
            val distances = groupedBytesMinusLast.mapIndexed { i, bytes1 ->
                groupedBytesMinusLast.drop(i + 1).map {bytes2 -> Common.hamming(bytes1, bytes2) / keysize}
            }.flatten()

            keysize to distances.average()

        }.sortedBy { it.second }.first().first // Sort by the average hamming distance, then only return the best keysize

        // Group bytes into blocks of the guessed key size.
        val groupedBytes = data.splitIntoGroupsOfN(guessedKeysize)

        // Transpose the bytes by pairing them by their position in their blocks.
        val transposedBytes = groupedBytes.first().mapIndexed { i, byte -> groupedBytes.map { if (it.size > i) it[i] else null } }.map {it.filterNotNull()}

        // Attempt to break the single char key for each block.
        val guessedKey = transposedBytes.map { inputBytes ->

            // Test against every ascii character between 32 and 126
            (32 until 126).map { c ->
                // Xor each byte against the character
                val str = String(inputBytes.map { b ->
                    (c.xor(b.toInt())).toChar()
                }.toCharArray())

                // Use the character frequency scorer to score the produced text.
                Guess(c.toChar(), "", str, CharacterFrequencyScorer.score(str))
            }.sortedBy { it.score }.first().character // sort by score, then return the character with ethe best score.
        }.joinToString("")

        assertEquals("Terminator X: Bring the noise", guessedKey)

        val xoredbytes = data.mapIndexed { i, c -> (c.toInt() xor guessedKey[i % guessedKey.length].toInt()) }
        println(xoredbytes.map {it.toChar()}.joinToString(""))

    }


    /* AES in ECB mode
     *
     * The Base64-encoded content in this file has been encrypted via AES-128 in ECB mode under the key
     *
     * "YELLOW SUBMARINE".
     *
     * (case-sensitive, without the quotes; exactly 16 characters; I like "YELLOW SUBMARINE" because it's exactly 16
     * bytes long, and now you do too).
     *
     * Decrypt it. You know the key, after all.
     *
     * Easiest way: use OpenSSL::Cipher and give it AES-128-ECB as the cipher.
     *
     * Do this with code.
     *
     * You can obviously decrypt this using the OpenSSL command-line tool, but we're having you get ECB working in code
     * for a reason. You'll need it a lot later on, and not just for attacking ECB.
     */
    @Test
    fun challenge7() {
        val cipher = Cipher.getInstance("AES")
        val skey = SecretKeySpec("YELLOW SUBMARINE".toByteArray(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, skey);
        val data = Codec.fromBase64(Utils.readLinesFromClasspathFile("7.txt").joinToString("")).map { it.toByte() }.toByteArray()
        val out = cipher.doFinal(data);
        val text = out.map { it.toChar() }.joinToString ("")
        assertTrue(text.startsWith("I'm back and I'm ringin' the bell "))
    }


    /* Detect AES in ECB mode
     *
     * In this file are a bunch of hex-encoded ciphertexts.
     *
     * One of them has been encrypted with ECB.
     *
     * Detect it.
     *
     * Remember that the problem with ECB is that it is stateless and deterministic; the same 16 byte plaintext block
     * will always produce the same 16 byte ciphertext.
     *
     */
    @Test
    fun challenge8() {
        val data = Utils.readLinesFromClasspathFile("8.txt").map {Codec.fromBase64(it)}

        // Since ECB is deterministic and the same input will always produce the same output, this attempts to find two
        // blocks of 16 bytes that are exactly the same in the data. If matches are found, it is assumed it was ECB.
        val detected = data.filter { line ->
            val blocks = line.splitIntoGroupsOfN(16)
            blocks.filterIndexed {i, block ->
                val remaining = blocks.drop(i + 1)
                remaining.any {block.equals(it)}
            }.isNotEmpty()
        }

        assertEquals(1, detected.size)


    }

}