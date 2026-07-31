from typing import Literal

from app.backend.models.category import Category
from app.backend.models.phrase import Phrase


class CommonQuestionPhrase(Phrase):
    category: Literal[Category.COMMON_QUESTIONS] = Category.COMMON_QUESTIONS


COMMON_QUESTION_PHRASES = [
    CommonQuestionPhrase(id="question_001", viet_phrase="Bạn tên gì?", english_translation="What's your name?"),
    CommonQuestionPhrase(
        id="question_002", viet_phrase="Bạn khỏe không?", english_translation="How are you?",
        cultural_note="Literally \"are you healthy,\" this is the standard way to ask how someone is doing, and the typical reply is \"khỏe, cảm ơn\" (fine, thanks).",
    ),
    CommonQuestionPhrase(
        id="question_003", viet_phrase="Bạn bao nhiêu tuổi?", english_translation="How old are you?",
        cultural_note="Asking age is common and not considered rude in Vietnam, since Vietnamese is a hierarchical language and speakers need to know relative age to choose the correct pronouns.",
    ),
    CommonQuestionPhrase(id="question_004", viet_phrase="Bạn từ đâu đến?", english_translation="Where are you from?"),
    CommonQuestionPhrase(
        id="question_005", viet_phrase="Cái này giá bao nhiêu?", english_translation="How much is this?",
        cultural_note="Bargaining is common and culturally accepted in markets and with street vendors, though usually not in fixed-price stores.",
    ),
    CommonQuestionPhrase(id="question_006", viet_phrase="Nhà vệ sinh ở đâu?", english_translation="Where is the bathroom?"),
    CommonQuestionPhrase(id="question_007", viet_phrase="Bây giờ là mấy giờ?", english_translation="What time is it now?"),
    CommonQuestionPhrase(id="question_008", viet_phrase="Bạn có nói được tiếng Anh không?", english_translation="Do you speak English?"),
    CommonQuestionPhrase(id="question_009", viet_phrase="Đây là gì?", english_translation="What is this?"),
    CommonQuestionPhrase(id="question_010", viet_phrase="Bạn làm nghề gì?", english_translation="What do you do for a living?"),
]