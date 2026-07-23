from chromadb.utils.embedding_functions import SentenceTransformerEmbeddingFunction

# Responsible for converting input text into vectors
class EmbeddingService:
    def __init__(self):
        self.embedding_function = SentenceTransformerEmbeddingFunction(
            model_name="paraphrase-multilingual-MiniLM-L12-v2"
        )

    def embed(self, input: str):
        embeddings = self.embedding_function(input=[input])
        return embeddings[0]