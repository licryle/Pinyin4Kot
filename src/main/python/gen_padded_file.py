import xml.etree.ElementTree as ET

FIRST_UNICODE = 0x4E00
LAST_UNICODE = 0x9FA5

def parse_xml(filename: str):
    tree = ET.parse(filename)
    root = tree.getroot()

    hanzi_map = {}

    for elem in root.iter("item"):
        # get unicode hex value
        hex_val = elem.get("unicode")
        hanyu_val = elem.get("hanyu")

        if not hex_val:
            continue

        codepoint = int(hex_val, 16)
        # 🔒 skip outside range
        if codepoint < FIRST_UNICODE or codepoint > LAST_UNICODE:
            continue

        try:
            char = chr(int(hex_val, 16))
        except ValueError:
            continue

        if hanyu_val:
            pinyins = [p.strip() for p in hanyu_val.split(",") if p.strip()]
        else:
            pinyins = []  # keep empty if no hanyu

        hanzi_map[char] = pinyins

    return hanzi_map


def process_map(hanzi_map, output_file="output.txt"):
    # 1. maximum number of pinyins
    max_size = max(len(pinyins) for pinyins in hanzi_map.values())

    # 2. longest pinyin length
    longest_pinyin = max((len(p) for pinyins in hanzi_map.values() for p in pinyins), default=0)

    print(f"Max size of pinyin list: {max_size}")
    print(f"Longest pinyin length: {longest_pinyin}")

    hanzi_items = list(hanzi_map.items())
    total = len(hanzi_items)

    with open(output_file, "w", encoding="utf-8") as f:
        for idx, (hanzi, pinyins) in enumerate(hanzi_items):
            line_parts = [hanzi]

            # add each pinyin + padding
            for p in pinyins:
                spaces = " " * (longest_pinyin - len(p)) # no separator
                line_parts.append(p + spaces)

            # fill up missing slots if less than max_size
            remaining = max_size - len(pinyins)
            if remaining > 0:
                line_parts.append(" " * (remaining * longest_pinyin))

            # join and add padding for fixed-width
            line = "".join(line_parts)
            f.write(line + "\n")


def check_consistency(xml_file, hanzi_map, output_file):
    # 1. Count XML items
    tree = ET.parse(xml_file)
    root = tree.getroot()
    total_items = sum(1 for _ in root.iter("item"))

    # 2. count items inside range
    in_range_items = 0
    for elem in root.findall(".//item"):
        hex_val = elem.get("unicode")
        if not hex_val:
            continue
        codepoint = int(hex_val, 16)
        if FIRST_UNICODE <= codepoint <= LAST_UNICODE:
            in_range_items += 1

    # 3. Count map size
    mapped_count = len(hanzi_map)

    # 4. Count output file lines
    with open(output_file, "r", encoding="utf-8") as f:
        output_lines = sum(1 for _ in f)

    print("\n=== Consistency Check ===")
    print(f"Total <item> entries in XML : {total_items}")
    print(f"<item> entries inside valid range : {in_range_items}")
    print(f"Characters successfully mapped: {mapped_count}")
    print(f"Lines in output file         : {output_lines}")

if __name__ == "__main__":
    xml_file = "./pinyin4net/src/Pinyin4net/Resources/unicode_to_hanyu_pinyin.xml"
    output_file = "../resources/Hanzi2Pinyin.txt"

    hanzi_map = parse_xml(xml_file)
    process_map(hanzi_map, output_file)
    check_consistency(xml_file, hanzi_map, output_file)
