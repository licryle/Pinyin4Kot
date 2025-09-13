import kotlin.test.Test

import fr.berliat.pinyin4kot.Hanzi2Pinyin
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Hanzi2PinyinTest {
    val map = Hanzi2Pinyin()

    @Test
    fun test() {
        testGetPinyin()
        testConvertPinyin()
        testExtractToneless()
    }

    @Test
    fun testGetPinyin() {
        assertContentEquals(map.getPinyin('你'), arrayOf("ni3"))

        try {
            map.getPinyin('〇')
            assertTrue(false)
        } catch (_: IndexOutOfBoundsException) {
            assertTrue(true)
        }

        // First line
        assertContentEquals(map.getPinyin('一'), arrayOf("yi1"))

        // ü is properly returned/handled
        assertContentEquals(map.getPinyin('侣'), arrayOf("lü3"))

        // ü is properly returned/handled
        assertContentEquals(map.getPinyin('女'), arrayOf("nü3", "ru3"))

                // Last line
        assertContentEquals(map.getPinyin('龥'), arrayOf("yue4"))

        // Multi-pinyin
        assertContentEquals(map.getPinyin('龜'), arrayOf("gui1", "jun1", "qiu1"))
    }

    @Test
    fun testConvertPinyin() {
        assertEquals(map.numberedToTonal("shang1"), "shāng")
        assertEquals(map.numberedToTonal("shang2"), "sháng")
        assertEquals(map.numberedToTonal("shang3"), "shǎng")
        assertEquals(map.numberedToTonal("shang4"), "shàng")
        assertEquals(map.numberedToTonal("shang"), "shang")
        assertEquals(map.numberedToTonal("shang5"), "shang")

        assertEquals(map.numberedToTonal("ao4"), "ào")

        assertEquals(map.numberedToTonal("shao4"), "shào")
        assertEquals(map.numberedToTonal("dui4"), "duì")
        assertEquals(map.numberedToTonal("diu1"), "diū")
        assertEquals(map.numberedToTonal("fou3"), "fǒu")
        assertEquals(map.numberedToTonal("lü3"), "lǚ")
        assertEquals(map.numberedToTonal("lu:3"), "lǔ:") // Actually testing u: isn't treated
        assertEquals(map.numberedToTonal("shoeiuüa2"), "shoeiuüá")
        assertEquals(map.numberedToTonal("sheiuüo2"), "sheiuüó")
        assertEquals(map.numberedToTonal("shieuü2"), "shiéuü")
        assertEquals(map.numberedToTonal("shiuü2"), "shiúü")

        assertEquals(map.tonalToNumbered("shāng"), "shang1")
        assertEquals(map.tonalToNumbered("sháng"), "shang2")
        assertEquals(map.tonalToNumbered("shǎng"), "shang3")
        assertEquals(map.tonalToNumbered("shàng"), "shang4")
        assertEquals(map.tonalToNumbered("shang"), "shang5")
        assertEquals(map.tonalToNumbered("ào"), "ao4")
        assertEquals(map.tonalToNumbered("diū"), "diu1")
        assertEquals(map.tonalToNumbered("lǚ"), "lü3")
        assertEquals(map.tonalToNumbered("lu:"), "lu:5") // Actually testing u: isn't treated
    }

    @Test
    fun testExtractToneless() {
        assertEquals(map.pinyinToToneless("diū"), "diu")
        assertEquals(map.pinyinToToneless("diu3"), "diu")
        assertEquals(map.pinyinToToneless("diu"), "diu")
    }
}