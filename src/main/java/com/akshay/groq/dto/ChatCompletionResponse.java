package com.akshay.groq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents the complete response from Groq's chat/completions endpoint.
 *
 * This is what we get back when we make a request to the AI.
 * The API returns JSON which Jackson automatically deserializes into this Java object.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatCompletionResponse {

    /**
     * Unique identifier for this API response/request.
     *
     * Useful for logging and tracking requests in production.
     * Example: "chatcmpl-8L8N8n8N8n8N8n8N8n8N"
     */
    @JsonProperty("id")
    private String id;

    /**
     * Type of object returned by the API.
     *
     * Always "chat.completion" for chat endpoints.
     */
    @JsonProperty("object")
    private String object;

    /**
     * Unix timestamp (seconds since Jan 1, 1970) when the response was created.
     *
     * Example: 1705934400 = January 23, 2024
     */
    @JsonProperty("created")
    private Long created;

    /**
     * The model used to generate this response.
     *
     * Should match the model we requested.
     * Example: "mixtral-8x7b-32768"
     */
    @JsonProperty("model")
    private String model;

    /**
     * List of choices (completions) returned by the API.
     *
     * Usually contains 1 choice (index 0) unless you specifically request multiple.
     * Each choice contains:
     * - The AI's message
     * - Why generation stopped (finish_reason)
     */
    @JsonProperty("choices")
    private List<ChatChoice> choices;

    /**
     * Information about token usage for this request.
     *
     * Contains:
     * - promptTokens: Tokens used in the request
     * - completionTokens: Tokens generated in the response
     * - totalTokens: Sum of both
     *
     * Important for cost tracking (Groq charges per token).
     */
    @JsonProperty("usage")
    private TokenUsage usage;

    /**
     * Convenience method to get the AI's response text easily.
     *
     * Instead of: response.getChoices().get(0).getMessage().getContent()
     * You can use: response.getResponseText()
     *
     * @return The content of the first choice's message, or null if not available
     */
    public String getResponseText() {
        if (this.choices != null && !this.choices.isEmpty()) {
            ChatChoice choice = this.choices.get(0);
            if (choice.getMessage() != null) {
                return choice.getMessage().getContent();
            }
        }
        return null;
    }
}

