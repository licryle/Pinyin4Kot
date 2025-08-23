# Pinyin4Kot

Pinyin4Kot is a tiny library for  very efficient (o(1) retrieval) Chinese Character to PinYin lookup.

## How to install
Either:
- Drop the JAR in yours libs
- Submodule this git

## How to use
```
import fr.berliat.pinyin4kot.Hanzi2PinYin

    val map = Hanzi2PinYin()
    map.getPinyin('你')
```

## How to build
The data comes from [pinyin4net](https://github.com/YangKuang/pinyin4net/blob/master/src/Pinyin4net/Resources/unicode_to_hanyu_pinyin.xml),
and then transformed into a fixed-padded file "database" for extra efficient read.

### Assemble database
```
$ cd src/main/python
$ python3 .\gen_padded_file.py
$ cp Hanzi2Pinyin.txt ../../resources
```

### Build the .jar
```
$ ./gradlew jar
```
It will be in build/libs