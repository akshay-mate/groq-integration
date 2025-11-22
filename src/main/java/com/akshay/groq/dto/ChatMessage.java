package com.akshay.groq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single message in the conversation.
 *
 * This DTO is used both in requests and responses when communicating with Groq API.
 * It follows the OpenAI API format that Groq API is compatible with.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    /**
     * The role of the message author.
     *
     * Possible values:
     * - "system": Instructions for the AI's behavior (sets context/personality)
     * - "user": Messages from the user asking questions
     * - "assistant": Responses from the AI model
     *
     * Example flow:
     * 1. System message: "You are a helpful assistant"
     * 2. User message: "What is Java?"
     * 3. Assistant message: "Java is a programming language..."
     */
    @JsonProperty("role")
    private String role;

    /**
     * The actual content/text of the message.
     *
     * - For "system" role: Sets behavior instructions (e.g., "You are an expert programmer")
     * - For "user" role: The user's question or prompt
     * - For "assistant" role: The AI's response
     */
    @JsonProperty("content")
    private String content;
}

