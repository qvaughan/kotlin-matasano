package scorer

object CharacterFrequencyScorer {
    val chars = " abcdefghijklmnopqrstuvwxyz"
    val charFrequencies = mapOf(
            ' ' to 0.17000F,
            'e' to 0.12702F,
            't' to 0.09056F,
            'a' to 0.08167F,
            'o' to 0.07507F,
            'i' to 0.06966F,
            'n' to 0.06749F,
            's' to 0.06327F,
            'h' to 0.06094F,
            'r' to 0.05987F,
            'd' to 0.04253F,
            'l' to 0.04025F,
            'c' to 0.02782F,
            'u' to 0.02758F,
            'm' to 0.02406F,
            'w' to 0.02361F,
            'f' to 0.02228F,
            'g' to 0.02015F,
            'y' to 0.01974F,
            'p' to 0.01929F,
            'b' to 0.01492F,
            'v' to 0.00978F,
            'k' to 0.00772F,
            'j' to 0.00153F,
            'x' to 0.00150F,
            'q' to 0.00095F,
            'z' to 0.00074F)

    fun score(input: String) : Float {
        val groupedFrequencies = input.toLowerCase().filter { chars.indexOf(it) > -1 }.groupBy { it }.map { it.key to it.value.size / input.length.toFloat() }
        val freq = chars.map { c ->
            groupedFrequencies.firstOrNull() { f -> f.first == c } ?: c to 0f
        }.toMap()

        return chars.map {
            val expected = charFrequencies[it]!!
            val actual = freq[it]!!
            Math.abs(expected - actual)
        }.sum()
    }

}