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

    init {
        // Load resource into ByteBuffer
        val resourceStream = this::class.java.getResourceAsStream("/Hanzi2PinYin.txt")
            ?: throw IllegalStateException("Resource not found")

        val bytes = resourceStream.readAllBytes()

        if (bytes.size != (lastUnicode - firstUnicode) * lineLength)
            throw InternalError("Issue with database file #1. Report to dev.")

        data = ByteBuffer.wrap(bytes)
    }

    fun getPinyin(hanzi: Char): List<String> {
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

    private fun parsePinyin(line: String): List<String> {
        return line.chunked(pinyinMaxLength).map{ it.trim() }.filter { it.isNotEmpty() }
    }
}