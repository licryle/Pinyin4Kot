import kotlin.test.Test

import fr.berliat.pinyin4kot.Hanzi2Pinyin

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
        assert(map.getPinyin('你').contentEquals(arrayOf("ni3")))

        try {
            map.getPinyin('〇')
            assert(false)
        } catch (_: IndexOutOfBoundsException) {
            assert(true)
        }

        // First line
        assert(map.getPinyin('一').contentEquals(arrayOf("yi1")))

        // ü is properly returned/handled
        assert(map.getPinyin('侣').contentEquals(arrayOf("lü3")))

                // Last line
        assert(map.getPinyin('龥').contentEquals(arrayOf("yue4")))

        // Multi-pinyin
        assert(map.getPinyin('龜').contentEquals(arrayOf("gui1", "jun1", "qiu1")))
    }

    @Test
    fun testConvertPinyin() {
        assert(map.numberedToTonal("shang1") == "shāng")
        assert(map.numberedToTonal("shang2") == "sháng")
        assert(map.numberedToTonal("shang3") == "shǎng")
        assert(map.numberedToTonal("shang4") == "shàng")
        assert(map.numberedToTonal("shang") == "shang")
        assert(map.numberedToTonal("shang5") == "shang")

        assert(map.numberedToTonal("ao4") == "ào")

        assert(map.numberedToTonal("shao4") == "shào")
        assert(map.numberedToTonal("dui4") == "duì")
        assert(map.numberedToTonal("diu1") == "diū")
        assert(map.numberedToTonal("fou3") == "fǒu")
        assert(map.numberedToTonal("lü3") == "lǚ")
        assert(map.numberedToTonal("lu:3") == "lǔ:") // Actually testing u: isn't treated
        assert(map.numberedToTonal("shoeiuüa2") == "shoeiuüá")
        assert(map.numberedToTonal("sheiuüo2") == "sheiuüó")
        assert(map.numberedToTonal("shieuü2") == "shiéuü")
        assert(map.numberedToTonal("shiuü2") == "shiúü")

        assert(map.tonalToNumbered("shāng") == "shang1")
        assert(map.tonalToNumbered("sháng") == "shang2")
        assert(map.tonalToNumbered("shǎng") == "shang3")
        assert(map.tonalToNumbered("shàng") == "shang4")
        assert(map.tonalToNumbered("shang") == "shang5")
        assert(map.tonalToNumbered("ào") == "ao4")
        assert(map.tonalToNumbered("diū") == "diu1")
        assert(map.tonalToNumbered("lǚ") == "lü3")
        assert(map.tonalToNumbered("lu:") == "lu:5") // Actually testing u: isn't treated
    }

    @Test
    fun testExtractToneless() {
        assert(map.pinyinToToneless("diū") == "diu")
        assert(map.pinyinToToneless("diu3") == "diu")
        assert(map.pinyinToToneless("diu") == "diu")
    }
}