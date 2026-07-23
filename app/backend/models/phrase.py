from typing import Optional

from pydantic import BaseModel, ConfigDict

from app.backend.models.category import Category


class Phrase(BaseModel):
    model_config = ConfigDict(frozen=True)

    id: str
    viet_phrase: str
    english_translation: str
    category: Category
    cultural_note: Optional[str] = None

phrases = [
    # Greetings
    Phrase(id="greeting_001", viet_phrase="xin chào", english_translation="hello", category=Category.GREETINGS),
    Phrase(
        id="greeting_002", viet_phrase="chào buổi sáng", english_translation="good morning", category=Category.GREETINGS,
        cultural_note="Vietnamese doesn't rely on time-of-day greetings as much as English; \"xin chào\" alone is used at any hour and sounds equally natural.",
    ),
    Phrase(id="greeting_003", viet_phrase="chào buổi tối", english_translation="good evening", category=Category.GREETINGS),
    Phrase(id="greeting_004", viet_phrase="tạm biệt", english_translation="goodbye", category=Category.GREETINGS),
    Phrase(id="greeting_005", viet_phrase="hẹn gặp lại", english_translation="see you again", category=Category.GREETINGS),
    Phrase(
        id="greeting_006", viet_phrase="cảm ơn", english_translation="thank you", category=Category.GREETINGS,
        cultural_note="Vietnamese culture often responds to thanks with \"không có gì\" (it's nothing), downplaying the favor rather than saying \"you're welcome.\"",
    ),
    Phrase(
        id="greeting_007", viet_phrase="không có gì", english_translation="you're welcome / it's nothing", category=Category.GREETINGS,
        cultural_note="This is the standard reply to \"cảm ơn.\" Literally \"there is nothing,\" it reflects a cultural preference for modesty over formal acknowledgment.",
    ),
    Phrase(
        id="greeting_008", viet_phrase="xin lỗi", english_translation="sorry / excuse me", category=Category.GREETINGS,
        cultural_note="Used both to apologize and to politely get someone's attention (e.g., before asking a stranger a question), similar to \"excuse me\" in English.",
    ),
    Phrase(id="greeting_009", viet_phrase="làm ơn", english_translation="please", category=Category.GREETINGS),
    Phrase(
        id="greeting_010", viet_phrase="chúc mừng", english_translation="congratulations", category=Category.GREETINGS,
        cultural_note="Commonly heard around holidays like Tết (\"chúc mừng năm mới\" = happy new year) and life events such as weddings or graduations.",
    ),

    # Food
    Phrase(
        id="food_001", viet_phrase="cơm", english_translation="rice / cooked rice", category=Category.FOOD,
        cultural_note="Rice is central to Vietnamese meals; \"ăn cơm chưa?\" (have you eaten rice yet?) is a common everyday greeting, similar to \"how are you?\"",
    ),
    Phrase(id="food_002", viet_phrase="phở", english_translation="pho (noodle soup)", category=Category.FOOD),
    Phrase(id="food_003", viet_phrase="bánh mì", english_translation="Vietnamese sandwich / bread", category=Category.FOOD),
    Phrase(
        id="food_004", viet_phrase="nước mắm", english_translation="fish sauce", category=Category.FOOD,
        cultural_note="Fish sauce is a staple condiment used in nearly every savory dish and is considered central to Vietnamese culinary identity.",
    ),
    Phrase(id="food_005", viet_phrase="rau", english_translation="vegetables", category=Category.FOOD),
    Phrase(id="food_006", viet_phrase="thịt", english_translation="meat", category=Category.FOOD),
    Phrase(id="food_007", viet_phrase="cá", english_translation="fish", category=Category.FOOD),
    Phrase(id="food_008", viet_phrase="trái cây", english_translation="fruit", category=Category.FOOD),
    Phrase(
        id="food_009", viet_phrase="cà phê", english_translation="coffee", category=Category.FOOD,
        cultural_note="Vietnamese coffee is typically strong and brewed with a phin filter, often served with sweetened condensed milk (cà phê sữa đá).",
    ),
    Phrase(id="food_010", viet_phrase="trà", english_translation="tea", category=Category.FOOD),

    # Numbers
    Phrase(id="number_001", viet_phrase="không", english_translation="zero", category=Category.NUMBERS),
    Phrase(id="number_002", viet_phrase="một", english_translation="one", category=Category.NUMBERS),
    Phrase(id="number_003", viet_phrase="hai", english_translation="two", category=Category.NUMBERS),
    Phrase(id="number_004", viet_phrase="ba", english_translation="three", category=Category.NUMBERS),
    Phrase(id="number_005", viet_phrase="bốn", english_translation="four", category=Category.NUMBERS),
    Phrase(id="number_006", viet_phrase="năm", english_translation="five", category=Category.NUMBERS),
    Phrase(id="number_007", viet_phrase="sáu", english_translation="six", category=Category.NUMBERS),
    Phrase(id="number_008", viet_phrase="bảy", english_translation="seven", category=Category.NUMBERS),
    Phrase(id="number_009", viet_phrase="tám", english_translation="eight", category=Category.NUMBERS),
    Phrase(id="number_010", viet_phrase="chín", english_translation="nine", category=Category.NUMBERS),

    # Family terms
    Phrase(id="family_001", viet_phrase="gia đình", english_translation="family", category=Category.FAMILY),
    Phrase(
        id="family_002", viet_phrase="bố / ba", english_translation="father", category=Category.FAMILY,
        cultural_note="\"Bố\" is more common in northern Vietnam while \"ba\" is more common in the south, reflecting regional dialect differences.",
    ),
    Phrase(id="family_003", viet_phrase="mẹ", english_translation="mother", category=Category.FAMILY),
    Phrase(
        id="family_004", viet_phrase="anh trai", english_translation="older brother", category=Category.FAMILY,
        cultural_note="Vietnamese kinship terms encode relative age and are also used as pronouns of address between non-relatives to show respect based on perceived seniority.",
    ),
    Phrase(id="family_005", viet_phrase="chị gái", english_translation="older sister", category=Category.FAMILY),
    Phrase(id="family_006", viet_phrase="em trai", english_translation="younger brother", category=Category.FAMILY),
    Phrase(id="family_007", viet_phrase="em gái", english_translation="younger sister", category=Category.FAMILY),
    Phrase(
        id="family_008", viet_phrase="ông", english_translation="grandfather", category=Category.FAMILY,
        cultural_note="Also used as a respectful term of address for older men in general, reflecting the importance of age hierarchy in Vietnamese social interaction.",
    ),
    Phrase(
        id="family_009", viet_phrase="bà", english_translation="grandmother", category=Category.FAMILY,
        cultural_note="Also used as a respectful term of address for older women in general, paralleling the use of \"ông\" for older men.",
    ),
    Phrase(id="family_010", viet_phrase="con", english_translation="child", category=Category.FAMILY),

    # Common questions
    Phrase(id="question_001", viet_phrase="Bạn tên gì?", english_translation="What's your name?", category=Category.COMMON_QUESTIONS),
    Phrase(
        id="question_002", viet_phrase="Bạn khỏe không?", english_translation="How are you?", category=Category.COMMON_QUESTIONS,
        cultural_note="Literally \"are you healthy,\" this is the standard way to ask how someone is doing, and the typical reply is \"khỏe, cảm ơn\" (fine, thanks).",
    ),
    Phrase(
        id="question_003", viet_phrase="Bạn bao nhiêu tuổi?", english_translation="How old are you?", category=Category.COMMON_QUESTIONS,
        cultural_note="Asking age is common and not considered rude in Vietnam, since Vietnamese is a hierarchical language and speakers need to know relative age to choose the correct pronouns.",
    ),
    Phrase(id="question_004", viet_phrase="Bạn từ đâu đến?", english_translation="Where are you from?", category=Category.COMMON_QUESTIONS),
    Phrase(
        id="question_005", viet_phrase="Cái này giá bao nhiêu?", english_translation="How much is this?", category=Category.COMMON_QUESTIONS,
        cultural_note="Bargaining is common and culturally accepted in markets and with street vendors, though usually not in fixed-price stores.",
    ),
    Phrase(id="question_006", viet_phrase="Nhà vệ sinh ở đâu?", english_translation="Where is the bathroom?", category=Category.COMMON_QUESTIONS),
    Phrase(id="question_007", viet_phrase="Bây giờ là mấy giờ?", english_translation="What time is it now?", category=Category.COMMON_QUESTIONS),
    Phrase(id="question_008", viet_phrase="Bạn có nói được tiếng Anh không?", english_translation="Do you speak English?", category=Category.COMMON_QUESTIONS),
    Phrase(id="question_009", viet_phrase="Đây là gì?", english_translation="What is this?", category=Category.COMMON_QUESTIONS),
    Phrase(id="question_010", viet_phrase="Bạn làm nghề gì?", english_translation="What do you do for a living?", category=Category.COMMON_QUESTIONS),
]