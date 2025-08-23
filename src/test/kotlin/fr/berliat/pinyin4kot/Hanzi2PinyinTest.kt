import kotlin.test.Test
import kotlin.test.assertEquals

import fr.berliat.pinyin4kot.Hanzi2Pinyin

class Hanzi2PinyinTest {

    @Test
    fun testGetPinyin() {
        val map = Hanzi2Pinyin()
        assertEquals(map.getPinyin('你'), listOf("ni3"))

        try {
            map.getPinyin('〇')
            assert(false)
        } catch (_: IndexOutOfBoundsException) {
            assert(true)
        }

        // First line
        assertEquals(map.getPinyin('一'), listOf("yi1"))

        // Last line
        assertEquals(map.getPinyin('龥'), listOf("yue4"))

        // Multi-pinyin
        assertEquals(map.getPinyin('龜'), listOf("gui1", "jun1", "qiu1"))
    }
}