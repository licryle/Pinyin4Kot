package fr.berliat.pinyin4kot

import java.nio.ByteBuffer
import java.nio.charset.Charset

class Hanzi2Pinyin() {
    private val firstUnicode = 0x4E00
    private val lastUnicode = 0x9FA5
    private val data: ByteBuffer
    private val pinyinMaxLength = 7
    private val maxPinyinCount = 8
    private val lineLength = 4 + maxPinyinCount * pinyinMaxLength // 1 UTF8 Character, followed by max 8 pinyin of max length 7
    private val charset = Charset.forName("UTF-8")

    private val toneMap = mapOf(
        'ā' to Pair('a', 1), 'á' to Pair('a', 2),
        'ǎ' to Pair('a', 3), 'à' to Pair('a', 4),
        'ē' to Pair('e', 1), 'é' to Pair('e', 2),
        'ě' to Pair('e', 3), 'è' to Pair('e', 4),
        'ī' to Pair('i', 1), 'í' to Pair('i', 2),
        'ǐ' to Pair('i', 3), 'ì' to Pair('i', 4),
        'ō' to Pair('o', 1), 'ó' to Pair('o', 2),
        'ǒ' to Pair('o', 3), 'ò' to Pair('o', 4),
        'ū' to Pair('u', 1), 'ú' to Pair('u', 2),
        'ǔ' to Pair('u', 3), 'ù' to Pair('u', 4),
        'ǖ' to Pair('ü', 1), 'ǘ' to Pair('ü', 2),
        'ǚ' to Pair('ü', 3), 'ǜ' to Pair('ü', 4)
    )

    // Always place on the last vowel (doing this because of iu and ui being exceptions)
    private val tonePriority = listOf("a", "o", "e", "iu", "ui", "i", "u", "ü")

    init {
        // Load resource into ByteBuffer
        val resourceStream = this::class.java.getResourceAsStream("/Hanzi2Pinyin.txt")
            ?: throw IllegalStateException("Resource not found. Report to dev.")

        val bytes = resourceStream.readAllBytes()

        if (bytes.size != (lastUnicode - firstUnicode + 1) * lineLength)
            throw InternalError("Issue with database file #1. Report to dev.")

        data = ByteBuffer.wrap(bytes)
    }

    fun getPinyin(hanzi: Char): Array<String> {
        val code = hanzi.code
        if (code < firstUnicode || code > lastUnicode) {
            throw IndexOutOfBoundsException("Character $hanzi (U+${code.toString(16)}) out of supported range.")
        }

        val offset = (code - firstUnicode) * lineLength
        val lineBytes = ByteArray(lineLength)
        data.position(offset)
        data.get(lineBytes)

        // Convert line bytes to string, remove trailing newline
        val line = String(lineBytes, charset).trimEnd('\n', '\r')
        if (line.isEmpty()) throw InternalError("Issue with database file #2. Report to dev.")

        val hanziFromLine = line[0]
        if (hanziFromLine != hanzi) throw InternalError("Issue with database file #3. Report to dev.")

        return parsePinyin(line.substring(1)) // skip first char (HanZi)
    }

    fun pinyinToToneless(pinyin: String): String {
        return tonalToNumbered(pinyin).trim().filter { it !in '1' .. '5' }
    }

    fun tonalToNumbered(tonalPinyin: String): String {
        var tone = 5 // default neutral
        val chars = tonalPinyin.map { c ->
            toneMap[c]?.let { (base, t) ->
                tone = t
                base
            } ?: c
        }.joinToString("")
        return chars + tone.toString()
    }

    fun numberedToTonal(numberedPinyin: String): String {
        if (numberedPinyin.isEmpty()) return numberedPinyin
        val last = numberedPinyin.last()
        val tone = when (last) {
            in '1'..'4' -> last.digitToInt()
            '5' -> 5
            else -> 5
        }

        // Remove the tone number if it’s 1..5
        val core = if (last in '1'..'5') numberedPinyin.dropLast(1) else numberedPinyin

        if (tone == 5) return core // neutral → no accent

        // Build a (Prio, Position) Pair to we can take the lowest prio and accent char at position
        val idx = tonePriority.withIndex()
            .mapNotNull { (prio, v) ->
                val pos = core.indexOf(v)
                if (pos >= 0) Pair(prio, pos + v.length - 1) else null
            }
            // Min by first is first in order of prio
            .minByOrNull { it.first }?.second
        if (idx == null) return core

        val accented = toneMap.entries
            .find { it.value.first == core[idx] && it.value.second == tone }
            ?.key

        return core.substring(0, idx) + accented + core.substring(idx + 1)
    }

    private fun parsePinyin(line: String): Array<String> {
        return line.chunked(pinyinMaxLength)
            .map{ it.trim() }
            .filter { it.isNotEmpty() }
            .toTypedArray()
    }
}