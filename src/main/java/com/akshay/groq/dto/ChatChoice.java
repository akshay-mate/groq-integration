package com.akshay.groq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single choice/response from the Groq API.
 *
 * The API can return multiple choices (completions), though typically we use index 0.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatChoice {

    /**
     * Index of this choice in the response array.
     *
     * If we request 1 completion (default), this will be 0.
     * If we request 3 completions, we'd get choices with indices 0, 1, 2.
     */
    @JsonProperty("index")
    private Integer index;

    /**
     * The actual message/response from the AI model.
     *
     * This is a ChatMessage object with:
     * - role: "assistant" (always)
     * - content: The AI's response text
     */
    @JsonProperty("message")
    private ChatMessage message;

    /**
     * Reason why the API stopped generating tokens.
     *
     * Possible values:
     * - "stop": Reached natural completion or stop token
     * - "length": Reached max_tokens limit
     * - "content_filter": Response was filtered for safety
     */
    @JsonProperty("finish_reason")
    private String finishReason;
}

