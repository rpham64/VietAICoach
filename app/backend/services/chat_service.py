from app.backend.services.claude_service import ClaudeService
from app.backend.services.retrieval_service import RetrievalService


class ChatService:
    def __init__(self):
        self.retrieval_service = RetrievalService()
        self.claude_service = ClaudeService()

    def chat(self, question) -> str:
        prompt = self.retrieval_service.search(question=question)
        response = self.claude_service.ask(prompt=prompt)

        return response