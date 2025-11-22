package com.akshay.groq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents the request payload sent to Groq's chat/completions endpoint.
 *
 * This DTO maps to the OpenAI-compatible API format that Groq API uses.
 * The Groq API follows OpenAI's Chat Completion API specification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatCompletionRequest {

    /**
     * The AI model to use for generating responses.
     *
     * Common Groq models:
     * - "mixtral-8x7b-32768": A fast, capable mixture-of-experts model
     * - "llama2-70b-4096": Meta's LLaMA 2 model (70 billion parameters)
     * - "gemma-7b-it": Google's Gemma model
     *
     * This value typically comes from application.properties (groq.api.model)
     */
    @JsonProperty("model")
    private String model;

    /**
     * A list of messages representing the conversation history.
     *
     * The order matters! The API processes messages from first to last.
     *
     * Typical structure:
     * 1. System message: Instructions for the AI
     * 2. User message: The actual question/prompt
     * 3. (Optional) Assistant message: Previous response (for context)
     * 4. (Optional) User message: Follow-up question
     *
     * Example:
     * [
     *   {"role": "system", "content": "You are a Java expert"},
     *   {"role": "user", "content": "What is Spring Boot?"}
     * ]
     */
    @JsonProperty("messages")
    private List<ChatMessage> messages;

    /**
     * Controls randomness/creativity of responses (0.0 to 2.0).
     *
     * - 0.0: Deterministic (always same response for same input)
     * - 0.7: Balanced (default, good for most use cases)
     * - 1.5-2.0: Very creative (good for brainstorming)
     *
     * Lower values = more focused, predictable responses
     * Higher values = more varied, creative responses
     */
    @JsonProperty("temperature")
    @Builder.Default
    private Double temperature = 0.7;

    /**
     * Maximum number of tokens in the response.
     *
     * Token = roughly 4 characters of English text
     *
     * Example: "Hello world" = ~3 tokens
     *
     * Setting limits:
     * - Prevents unexpectedly long responses
     * - Controls costs (Groq charges per token)
     * - Ensures response fits your use case
     *
     * Leave as null for maximum allowed by model.
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * Nucleus sampling parameter (0.0 to 1.0).
     *
     * Filters output to only top P% most likely tokens.
     *
     * - 0.9: Consider top 90% likely tokens (balanced)
     * - 0.95: More tokens considered (more varied)
     * - 0.5: Fewer tokens considered (more focused)
     *
     * Works together with temperature for better control.
     */
    @JsonProperty("top_p")
    @Builder.Default
    private Double topP = 1.0;

    /**
     * Frequency penalty (0.0 to 2.0).
     *
     * Reduces likelihood of repeating tokens.
     *
     * - 0.0: No penalty (can repeat freely)
     * - 1.0: Penalize repetition (moderate)
     * - 2.0: Heavily penalize repetition
     *
     * Useful for generating diverse, non-repetitive text.
     */
    @JsonProperty("frequency_penalty")
    @Builder.Default
    private Double frequencyPenalty = 0.0;

    /**
     * Presence penalty (0.0 to 2.0).
     *
     * Penalizes topics already mentioned in the conversation.
     *
     * - 0.0: No penalty
     * - 1.0: Moderate penalty (encourage new topics)
     * - 2.0: Heavy penalty (force new topics)
     *
     * Different from frequency_penalty: focuses on presence, not frequency.
     */
    @JsonProperty("presence_penalty")
    @Builder.Default
    private Double presencePenalty = 0.0;
}

