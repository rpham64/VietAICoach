from typing import Literal

from app.backend.models.category import Category
from app.backend.models.phrase import Phrase


class NumberPhrase(Phrase):
    category: Literal[Category.NUMBERS] = Category.NUMBERS


NUMBER_PHRASES = [
    NumberPhrase(id="number_001", viet_phrase="không", english_translation="zero"),
    NumberPhrase(id="number_002", viet_phrase="một", english_translation="one"),
    NumberPhrase(id="number_003", viet_phrase="hai", english_translation="two"),
    NumberPhrase(id="number_004", viet_phrase="ba", english_translation="three"),
    NumberPhrase(id="number_005", viet_phrase="bốn", english_translation="four"),
    NumberPhrase(id="number_006", viet_phrase="năm", english_translation="five"),
    NumberPhrase(id="number_007", viet_phrase="sáu", english_translation="six"),
    NumberPhrase(id="number_008", viet_phrase="bảy", english_translation="seven"),
    NumberPhrase(id="number_009", viet_phrase="tám", english_translation="eight"),
    NumberPhrase(id="number_010", viet_phrase="chín", english_translation="nine"),
]