import os

from dotenv import load_dotenv
from fastapi import FastAPI

from app.server.claude_client import ClaudeClient
from app.server.model.chat_request import ChatRequest, ChatResponse

load_dotenv()
api_key = os.getenv("ANTHROPIC_API_KEY")

if api_key is None:
    raise RuntimeError("ANTHROPIC_API_KEY is not set. Add it to your .env file.")

client = ClaudeClient(
    api_key=api_key
)
app = FastAPI()

@app.post("/chat")
def submitPrompt(request: ChatRequest) -> ChatResponse:
    return ChatResponse(response=client.send(message=request.message))