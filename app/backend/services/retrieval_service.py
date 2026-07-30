from app.backend.db.chroma_client import collection
from app.backend.services.prompt_builder import PromptBuilder


class RetrievalService:
    def __init__(self):
        self.prompt_builder = PromptBuilder()

    def search(self, question: str) -> str:
        results = collection.query(
            query_texts=[question],
            n_results=3,
            include=[
                "documents",
                "metadatas",
                "distances",
            ]
        )
        documents = results["documents"][0]
        prompt = self.prompt_builder.build_prompt(question, documents)

        return prompt