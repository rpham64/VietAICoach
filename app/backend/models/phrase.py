from typing import Optional

from pydantic import BaseModel, ConfigDict


class Phrase(BaseModel):
    model_config = ConfigDict(frozen=True)

    id: str
    viet_phrase: str
    english_translation: str
    cultural_note: Optional[str] = None