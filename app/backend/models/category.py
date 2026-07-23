from enum import Enum


class Category(str, Enum):
    GREETINGS = "greetings"
    FOOD = "food"
    NUMBERS = "numbers"
    FAMILY = "family"
    COMMON_QUESTIONS = "common_questions"