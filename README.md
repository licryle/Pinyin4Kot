# Pinyin4Kot

Pinyin4Kot is a tiny library for  very efficient (o(1) retrieval) Chinese Character to PinYin lookup.

## Limitations
Only support the most 20902 characters from 一(Unicode 0x4E00) to 龥(Unicode 0x9FA5).
For a detailed explanation of unicode ranges, this [stack overflow thread is best](https://stackoverflow.com/questions/1366068/whats-the-complete-range-for-chinese-characters-in-unicode).

## How to install
Either:
- Drop the JAR in yours libs
- Submodule this git

## How to use
```
import fr.berliat.pinyin4kot.Hanzi2PinYin

val map = Hanzi2PinYin()
map.getPinyin('你') // ["nǐ"]

map.tonalToNumbered("diū") // "diu1"
map.numberedToTonal("fou3") // "fǒu"

map.pinyinToToneless("diu3") == "diu"
map.pinyinToToneless("diū") == "diu"
```

## How to build
The data comes from [pinyin4net](https://github.com/YangKuang/pinyin4net/blob/master/src/Pinyin4net/Resources/unicode_to_hanyu_pinyin.xml),
and then transformed into a fixed-padded file "database" for extra efficient read.

### Assemble database
```
$ cd src/main/python
$ python3 .\gen_padded_file.py
```

### Build the .jar
```
$ ./gradlew jar
```
It will be in build/libs
