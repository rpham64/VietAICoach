import os

import anthropic
from dotenv import load_dotenv
from fastapi import FastAPI

load_dotenv()
api_key = os.getenv("ANTHROPIC_API_KEY")

if api_key is None:
    raise RuntimeError("ANTHROPIC_API_KEY is not set. Add it to your .env file.")

client = anthropic.Anthropic()
app = FastAPI()

@app.post("/chat")
def test():
    return {
        "response": "This is a hardcoded response"
    }