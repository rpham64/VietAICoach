from anthropic import Anthropic
from anthropic.types import MessageParam


class ClaudeClient:
    def __init__(
        self,
        api_key: str,
        model: str = "claude-haiku-4-5",
        temperature: float = 0.0,
        system: str = ""
    ):
        self.client = Anthropic(api_key=api_key)
        self.model = model
        self.messages: list[MessageParam] = []
        self.temperature = temperature
        self.system = system

    def send(
        self,
        message: str,
        max_tokens: int = 200
    ) -> str:
        self.messages.append(
            {
                "role": "user",
                "content": message
            }
        )

        try:
            response = self.client.messages.create(
                model=self.model,
                max_tokens=max_tokens,
                messages=self.messages,
                system=self.system,
                temperature=self.temperature
            )
            reply = next(
                (block.text for block in response.content if block.type == "text"),
                None
            )

            if reply is None:
                raise RuntimeError("No text block in response")

            self.messages.append(
                {
                    "role": "assistant",
                    "content": reply
                }
            )

            return reply
        except Exception:
            self.messages.pop()  # Undo last user message to keep history consistent
            raise  # Propagate the real error instead of masking it as an empty string
