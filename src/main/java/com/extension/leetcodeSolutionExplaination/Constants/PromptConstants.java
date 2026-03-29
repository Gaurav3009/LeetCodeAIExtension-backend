package com.extension.leetcodeSolutionExplaination.Constants;

public class PromptConstants {
    public static final String SYSTEM_ROLE =
            "You are a MAANG-level Data Structures and Algorithms expert and technical writer.";

    public static final String USER_PROMPT_TEMPLATE = """
            Generate a HIGH-QUALITY LeetCode discussion post in MARKDOWN format.

            Follow this STRICT structure:

            # {TITLE}

            ## Intuition
            - Explain the core idea simply

            ## ⚡ Approach
            - Step-by-step explanation

            ## Dry Run
            - Use a sample input

            ## ⏱ Complexity
            - Time Complexity
            - Space Complexity

            ## Code
            ```%s
            %s
            ```

            ## Edge Cases
            - Mention important edge cases

            Keep it beginner-friendly and clean.

            Problem:
            %s
            """;
}
