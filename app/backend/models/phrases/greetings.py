from typing import Literal

from app.backend.models.category import Category
from app.backend.models.phrase import Phrase


class GreetingPhrase(Phrase):
    category: Literal[Category.GREETINGS] = Category.GREETINGS


GREETING_PHRASES = [
    GreetingPhrase(id="greeting_001", viet_phrase="xin chào", english_translation="hello"),
    GreetingPhrase(
        id="greeting_002", viet_phrase="chào buổi sáng", english_translation="good morning",
        cultural_note="Vietnamese doesn't rely on time-of-day greetings as much as English; \"xin chào\" alone is used at any hour and sounds equally natural.",
    ),
    GreetingPhrase(id="greeting_003", viet_phrase="chào buổi tối", english_translation="good evening"),
    GreetingPhrase(id="greeting_004", viet_phrase="tạm biệt", english_translation="goodbye"),
    GreetingPhrase(id="greeting_005", viet_phrase="hẹn gặp lại", english_translation="see you again"),
    GreetingPhrase(
        id="greeting_006", viet_phrase="cảm ơn", english_translation="thank you",
        cultural_note="Vietnamese culture often responds to thanks with \"không có gì\" (it's nothing), downplaying the favor rather than saying \"you're welcome.\"",
    ),
    GreetingPhrase(
        id="greeting_007", viet_phrase="không có gì", english_translation="you're welcome / it's nothing",
        cultural_note="This is the standard reply to \"cảm ơn.\" Literally \"there is nothing,\" it reflects a cultural preference for modesty over formal acknowledgment.",
    ),
    GreetingPhrase(
        id="greeting_008", viet_phrase="xin lỗi", english_translation="sorry / excuse me",
        cultural_note="Used both to apologize and to politely get someone's attention (e.g., before asking a stranger a question), similar to \"excuse me\" in English.",
    ),
    GreetingPhrase(id="greeting_009", viet_phrase="làm ơn", english_translation="please"),
    GreetingPhrase(
        id="greeting_010", viet_phrase="chúc mừng", english_translation="congratulations",
        cultural_note="Commonly heard around holidays like Tết (\"chúc mừng năm mới\" = happy new year) and life events such as weddings or graduations.",
    ),
]