from fastapi import FastAPI

from pydantic import BaseModel

from app.backend.services.chat_service import ChatService


class ChatRequest(BaseModel):
    prompt: str

class ChatResponse(BaseModel):
    response: str

app = FastAPI()
chat_service = ChatService()

@app.post("/chat")
def submit_prompt(request: ChatRequest) -> ChatResponse:
    response = chat_service.chat(question=request.prompt)
    return ChatResponse(response=response)