import textwrap


class PromptBuilder:

    @staticmethod
    def build_prompt(question, documents):
        context = "\n\n".join(documents)

        return textwrap.dedent(
            f"""
                    You are an expert Vietnamese language tutor.

                    Only answer using the supplied context.

                    Context
                    -------
                    {context}

                    Question
                    --------
                    {question}

                    Answer:
                """
        ).strip()