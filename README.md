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