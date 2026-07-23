import os

from dotenv import load_dotenv

from app.backend.llm.claude_client import ClaudeClient

load_dotenv()
api_key = os.getenv("ANTHROPIC_API_KEY")

if api_key is None:
    raise RuntimeError("ANTHROPIC_API_KEY is not set. Add it to your .env file.")

client = ClaudeClient(
    api_key=api_key,
    system="""
        You are a friendly Southern Vietnamese (Saigon dialect) language coach. 
        When the user writes in English, translate to Southern Vietnamese and explain any regional differences from Northern Vietnamese. 
        When they write in Vietnamese, correct grammar/pronunciation gently and explain mistakes. 
        Keep responses under 100 words.
    """
)

class ClaudeService:

    def ask(self, prompt):
        response = client.send(message=prompt)
        return response