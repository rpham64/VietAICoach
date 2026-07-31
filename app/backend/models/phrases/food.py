from typing import Literal

from app.backend.models.category import Category
from app.backend.models.phrase import Phrase


class FoodPhrase(Phrase):
    category: Literal[Category.FOOD] = Category.FOOD


FOOD_PHRASES = [
    FoodPhrase(
        id="food_001", viet_phrase="cơm", english_translation="rice / cooked rice",
        cultural_note="Rice is central to Vietnamese meals; \"ăn cơm chưa?\" (have you eaten rice yet?) is a common everyday greeting, similar to \"how are you?\"",
    ),
    FoodPhrase(id="food_002", viet_phrase="phở", english_translation="pho (noodle soup)"),
    FoodPhrase(id="food_003", viet_phrase="bánh mì", english_translation="Vietnamese sandwich / bread"),
    FoodPhrase(
        id="food_004", viet_phrase="nước mắm", english_translation="fish sauce",
        cultural_note="Fish sauce is a staple condiment used in nearly every savory dish and is considered central to Vietnamese culinary identity.",
    ),
    FoodPhrase(id="food_005", viet_phrase="rau", english_translation="vegetables"),
    FoodPhrase(id="food_006", viet_phrase="thịt", english_translation="meat"),
    FoodPhrase(id="food_007", viet_phrase="cá", english_translation="fish"),
    FoodPhrase(id="food_008", viet_phrase="trái cây", english_translation="fruit"),
    FoodPhrase(
        id="food_009", viet_phrase="cà phê", english_translation="coffee",
        cultural_note="Vietnamese coffee is typically strong and brewed with a phin filter, often served with sweetened condensed milk (cà phê sữa đá).",
    ),
    FoodPhrase(id="food_010", viet_phrase="trà", english_translation="tea"),
]