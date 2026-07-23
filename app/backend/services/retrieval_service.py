from app.backend.db.chroma_client import collection
from app.backend.services.embedding_service import EmbeddingService


class RetrievalService:
    def __init__(self):
        self.embedding_service = EmbeddingService()

    def search(self, question) -> str:
        embedding = self.embedding_service.embed(input=question)
        results = collection.query(
            query_texts=[embedding],
            n_results=3,
            include=[
                "documents",
                "metadatas",
                "distances",
            ]
        )

        return results["documents"][0][0]