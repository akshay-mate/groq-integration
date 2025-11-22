package com.akshay.groq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents token usage information from the API response.
 *
 * Tokens are the basic units that AI models process.
 * Roughly: 1 token ≈ 4 characters of English text
 *
 * Groq API charges based on tokens consumed.
 * This helps track costs and optimize your usage.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenUsage {

    /**
     * Number of tokens in the user's request/prompt.
     *
     * Example:
     * Request: "What is Java?"
     * Tokens: ~4 (approximately)
     */
    @JsonProperty("prompt_tokens")
    private Integer promptTokens;

    /**
     * Number of tokens generated in the AI's response.
     *
     * Example:
     * Response: "Java is a programming language..."
     * Tokens: ~50 (approximately)
     */
    @JsonProperty("completion_tokens")
    private Integer completionTokens;

    /**
     * Total tokens used = promptTokens + completionTokens
     *
     * This is what you're typically charged for.
     *
     * Example:
     * Total: 4 + 50 = 54 tokens
     * Cost: 54 * (price per token) = your bill
     */
    @JsonProperty("total_tokens")
    private Integer totalTokens;
}

