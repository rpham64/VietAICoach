import chromadb
from chromadb.utils.embedding_functions import SentenceTransformerEmbeddingFunction

from app.backend.models.phrase import phrases

chroma_client = chromadb.PersistentClient()
embedding_function = SentenceTransformerEmbeddingFunction(
    model_name="paraphrase-multilingual-MiniLM-L12-v2"
)
collection = chroma_client.get_or_create_collection(
    name="vietnamese_language_collection",
    embedding_function=embedding_function
)

collection.add(
    ids=[phrase.id for phrase in phrases],
    documents=[phrase.viet_phrase for phrase in phrases],
    metadatas=[
        {
            "vietnamese": phrase.viet_phrase,
            "english": phrase.english_translation,
            "category": phrase.category.value,
            **({"cultural_note": phrase.cultural_note} if phrase.cultural_note else {}),
        }
        for phrase in phrases
    ],
)