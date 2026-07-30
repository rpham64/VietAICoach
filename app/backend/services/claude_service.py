import os
import textwrap
from typing import cast

from dotenv import load_dotenv

from app.backend.llm.claude_client import ClaudeClient

load_dotenv()
anthropic_api_key = os.getenv("ANTHROPIC_API_KEY")

if anthropic_api_key is None:
    raise RuntimeError("ANTHROPIC_API_KEY is not set. Add it to your .env file.")

SYSTEM_PROMPT = textwrap.dedent(
    """
    You are a friendly Southern Vietnamese (Saigon dialect) language coach.
    When the user writes in English, translate to Southern Vietnamese and explain any regional differences from Northern Vietnamese.
    When they write in Vietnamese, correct grammar/pronunciation gently and explain mistakes.
    Keep responses under 100 words.
    """
).strip()


class ClaudeService:

    def __init__(
        self,
        api_key: str = cast(str, anthropic_api_key)
    ):
        self.client = ClaudeClient(
            api_key=api_key,
            system=SYSTEM_PROMPT
        )

    def ask(self, prompt: str) -> str:
        response = self.client.send(message=prompt)
        return response