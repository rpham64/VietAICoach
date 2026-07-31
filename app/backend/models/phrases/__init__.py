from app.backend.models.phrases.common_questions import COMMON_QUESTION_PHRASES
from app.backend.models.phrases.family import FAMILY_PHRASES
from app.backend.models.phrases.food import FOOD_PHRASES
from app.backend.models.phrases.greetings import GREETING_PHRASES
from app.backend.models.phrases.numbers import NUMBER_PHRASES

phrases = (
    GREETING_PHRASES
    + FOOD_PHRASES
    + NUMBER_PHRASES
    + FAMILY_PHRASES
    + COMMON_QUESTION_PHRASES
)