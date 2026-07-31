from typing import Literal

from app.backend.models.category import Category
from app.backend.models.phrase import Phrase


class FamilyPhrase(Phrase):
    category: Literal[Category.FAMILY] = Category.FAMILY


FAMILY_PHRASES = [
    FamilyPhrase(id="family_001", viet_phrase="gia đình", english_translation="family"),
    FamilyPhrase(
        id="family_002", viet_phrase="bố / ba", english_translation="father",
        cultural_note="\"Bố\" is more common in northern Vietnam while \"ba\" is more common in the south, reflecting regional dialect differences.",
    ),
    FamilyPhrase(id="family_003", viet_phrase="mẹ", english_translation="mother"),
    FamilyPhrase(
        id="family_004", viet_phrase="anh trai", english_translation="older brother",
        cultural_note="Vietnamese kinship terms encode relative age and are also used as pronouns of address between non-relatives to show respect based on perceived seniority.",
    ),
    FamilyPhrase(id="family_005", viet_phrase="chị gái", english_translation="older sister"),
    FamilyPhrase(id="family_006", viet_phrase="em trai", english_translation="younger brother"),
    FamilyPhrase(id="family_007", viet_phrase="em gái", english_translation="younger sister"),
    FamilyPhrase(
        id="family_008", viet_phrase="ông", english_translation="grandfather",
        cultural_note="Also used as a respectful term of address for older men in general, reflecting the importance of age hierarchy in Vietnamese social interaction.",
    ),
    FamilyPhrase(
        id="family_009", viet_phrase="bà", english_translation="grandmother",
        cultural_note="Also used as a respectful term of address for older women in general, paralleling the use of \"ông\" for older men.",
    ),
    FamilyPhrase(id="family_010", viet_phrase="con", english_translation="child"),
]