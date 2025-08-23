import kotlin.test.Test

import fr.berliat.pinyin4kot.Hanzi2Pinyin

class Hanzi2PinyinTest {

    @Test
    fun testGetPinyin() {
        val map = Hanzi2Pinyin()
        assert(map.getPinyin('你').contentEquals(arrayOf("ni3")))

        try {
            map.getPinyin('〇')
            assert(false)
        } catch (_: IndexOutOfBoundsException) {
            assert(true)
        }

        // First line
        assert(map.getPinyin('一').contentEquals(arrayOf("yi1")))

        // Last line
        assert(map.getPinyin('龥').contentEquals(arrayOf("yue4")))

        // Multi-pinyin
        assert(map.getPinyin('龜').contentEquals(arrayOf("gui1", "jun1", "qiu1")))
    }
}