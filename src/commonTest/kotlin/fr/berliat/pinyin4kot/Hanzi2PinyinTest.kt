package fr.berliat.pinyin4kot

import kotlin.test.*

class Hanzi2PinyinTest {
    private val map = Hanzi2Pinyin()

    @Test
    fun testGetPinyin() {
        // Standard case
        assertContentEquals(arrayOf("ni3"), map.getPinyin('你'))

        // First and last supported characters (Boundaries)
        assertContentEquals(arrayOf("yi1"), map.getPinyin('\u4E00'))
        assertContentEquals(arrayOf("yue4"), map.getPinyin('\u9FA5'))

        // ü handling
        assertContentEquals(arrayOf("lü3"), map.getPinyin('侣'))
        assertContentEquals(arrayOf("nü3", "ru3"), map.getPinyin('女'))

        // Multi-pinyin
        assertContentEquals(arrayOf("gui1", "jun1", "qiu1"), map.getPinyin('龜'))
    }

    @Test
    fun testGetPinyinEmpty() {
        // Characters in range but with no pinyin in database
        assertContentEquals(emptyArray(), map.getPinyin('丆'))
        assertContentEquals(emptyArray(), map.getPinyin('丷'))
    }

    @Test
    fun testGetPinyinOutOfRange() {
        // Just outside range (Lower)
        assertFailsWith<IndexOutOfBoundsException> {
            map.getPinyin((Hanzi2Pinyin.firstUnicode - 1).toChar())
        }
        // Just outside range (Upper)
        assertFailsWith<IndexOutOfBoundsException> {
            map.getPinyin((Hanzi2Pinyin.lastUnicode + 1).toChar())
        }
        // Way outside (Special case '〇' is often mistaken for a Hanzi but is U+3007)
        assertFailsWith<IndexOutOfBoundsException> { map.getPinyin('〇') }
    }

    @Test
    fun testNumberedToTonal() {
        // Standard tones
        assertEquals("shāng", map.numberedToTonal("shang1"))
        assertEquals("sháng", map.numberedToTonal("shang2"))
        assertEquals("shǎng", map.numberedToTonal("shang3"))
        assertEquals("shàng", map.numberedToTonal("shang4"))
        
        // Neutral tone
        assertEquals("shang", map.numberedToTonal("shang"))
        assertEquals("shang", map.numberedToTonal("shang5"))

        // Vowel combinations (Priority rules)
        assertEquals("ào", map.numberedToTonal("ao4"))
        assertEquals("shào", map.numberedToTonal("shao4"))
        assertEquals("fǒu", map.numberedToTonal("fou3"))
        
        // iu / ui exceptions (tone on the second vowel)
        assertEquals("duì", map.numberedToTonal("dui4"))
        assertEquals("diū", map.numberedToTonal("diu1"))
        assertEquals("qiū", map.numberedToTonal("qiu1"))
        assertEquals("xuě", map.numberedToTonal("xue3"))
        
        // ü
        assertEquals("lǚ", map.numberedToTonal("lü3"))
        
        // Complex / Edge cases
        assertEquals("", map.numberedToTonal(""))
        assertEquals("m", map.numberedToTonal("m2")) // No vowels to accent
        assertEquals("lǔ:", map.numberedToTonal("lu:3")) // colon preserved, u accented
        
        // Verification of complex priority logic
        assertEquals("shoeiuüá", map.numberedToTonal("shoeiuüa2"))
        assertEquals("sheiuüó", map.numberedToTonal("sheiuüo2"))
        assertEquals("shiéuü", map.numberedToTonal("shieuü2"))
        assertEquals("shiúü", map.numberedToTonal("shiuü2"))
    }

    @Test
    fun testTonalToNumbered() {
        assertEquals("shang1", map.tonalToNumbered("shāng"))
        assertEquals("shang2", map.tonalToNumbered("sháng"))
        assertEquals("shang3", map.tonalToNumbered("shǎng"))
        assertEquals("shang4", map.tonalToNumbered("shàng"))
        assertEquals("shang5", map.tonalToNumbered("shang"))
        
        assertEquals("ao4", map.tonalToNumbered("ào"))
        assertEquals("diu1", map.tonalToNumbered("diū"))
        assertEquals("lü3", map.tonalToNumbered("lǚ"))
        assertEquals("lu:5", map.tonalToNumbered("lu:"))

        // Known quirk: appending tone even if one exists (ni3 -> ni35)
        assertEquals("ni35", map.tonalToNumbered("ni3"))
    }

    @Test
    fun testPinyinToToneless() {
        assertEquals("diu", map.pinyinToToneless("diū"))
        assertEquals("diu", map.pinyinToToneless("diu3"))
        assertEquals("diu", map.pinyinToToneless("diu"))
        
        // Combined cases
        assertEquals("lü", map.pinyinToToneless("lǚ"))
        assertEquals("shang", map.pinyinToToneless("shàng"))
    }
}
