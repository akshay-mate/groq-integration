package com.akshay.groq.service;

import com.akshay.groq.dto.ChatCompletionRequest;
import com.akshay.groq.dto.ChatCompletionResponse;
import com.akshay.groq.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Service layer for interacting with Groq API.
 *
 * This class handles:
 * 1. Building API requests
 * 2. Making HTTP calls via WebClient
 * 3. Parsing responses
 * 4. Error handling and logging
 * 5. Business logic for chat completions
 *
 * Architecture: Service layer sits between Controller (HTTP) and WebClient (API calls).
 * It encapsulates all Groq API interaction logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroqService {

    /**
     * WebClient bean injected from WebClientConfig.
     *
     * @Autowired annotation is implicit with @RequiredArgsConstructor on constructor parameter.
     * Spring automatically injects the WebClient bean we created in WebClientConfig.
     */
    private final WebClient webClient;

    /**
     * Groq API key read from application.properties (groq.api.key).
     *
     * @Value("${groq.api.key}") reads from environment variable GROQ_API_KEY.
     * This is secure - not hardcoded in source code.
     */
    @Value("${groq.api.key}")
    private String groqApiKey;

    /**
     * Default model to use if not specified.
     * Read from application.properties (groq.api.model).
     */
    @Value("${groq.api.model}")
    private String defaultModel;

    /**
     * Main method to send a chat message to Groq API and get a response.
     *
     * This is a REACTIVE method - it returns a Mono instead of blocking.
     * Mono is from Project Reactor and represents a single asynchronous value that may or may not exist.
     *
     * Why Reactive?
     * - Non-blocking: Thread isn't blocked waiting for API response
     * - Scalable: Handle thousands of concurrent requests with few threads
     * - Efficient: Better resource utilization
     *
     * @param userMessage The user's question/prompt
     * @return Mono<ChatCompletionResponse> - Will eventually contain the AI's response
     *
     * Example usage in Controller:
     *   service.chatWithGroq("What is Java?")
     *          .subscribe(response -> log.info("Got response: " + response.getResponseText()))
     */
    public Mono<ChatCompletionResponse> chatWithGroq(String userMessage) {

        log.info("Received user message: {}", userMessage);

        // Step 1: Build the request DTO
        ChatCompletionRequest request = buildChatRequest(userMessage);

        log.debug("Built request with model: {}, messages count: {}",
                  request.getModel(), request.getMessages().size());

        // Step 2: Make the API call and return response
        // If an error occurs, handle it gracefully
        return makeApiCall(request)
                .doOnNext(response -> log.info("Received response from Groq API. Tokens used: {}",
                                                response.getUsage().getTotalTokens()))
                .doOnError(error -> log.error("Error calling Groq API", error));
    }

    /**
     * Builds a ChatCompletionRequest DTO from a user message.
     *
     * This method:
     * 1. Creates a System message (sets AI behavior)
     * 2. Creates a User message (the actual question)
     * 3. Combines them into a request
     * 4. Sets model and parameters
     *
     * @param userMessage The user's question
     * @return ChatCompletionRequest ready to be serialized to JSON and sent to API
     */
    private ChatCompletionRequest buildChatRequest(String userMessage) {

        // Create a list of messages
        // Note: We're using Java's List.of() which creates an immutable list
        java.util.List<ChatMessage> messages = java.util.List.of(
            // System message: Instructions for the AI's behavior
            ChatMessage.builder()
                    .role("system")
                    .content("You are a helpful, professional assistant. "
                            + "Provide clear, concise, and accurate answers. "
                            + "If you don't know something, say so honestly.")
                    .build(),

            // User message: The actual question/prompt
            ChatMessage.builder()
                    .role("user")
                    .content(userMessage)
                    .build()
        );

        // Build and return the complete request using Builder pattern
        return ChatCompletionRequest.builder()
                .model(defaultModel)              // Use model from config
                .messages(messages)               // Messages we just created
                .temperature(0.7)                 // Balanced creativity
                .maxTokens(1000)                  // Max response length
                .topP(0.9)                        // Consider top 90% likely tokens
                .frequencyPenalty(0.0)            // Don't penalize repetition
                .presencePenalty(0.0)             // Don't penalize presence
                .build();
    }

    /**
     * Makes the actual HTTP POST request to Groq API.
     *
     * This method:
     * 1. Sends POST request to /chat/completions endpoint
     * 2. Includes Authorization header with API key
     * 3. Converts response JSON to ChatCompletionResponse DTO
     * 4. Handles errors with retry logic
     *
     * Reactive flow:
     * - webClient.post() = prepares a POST request
     * - .uri("/chat/completions") = endpoint path (combined with baseUrl from config)
     * - .header(...) = adds Authorization header
     * - .bodyValue(request) = sends request body as JSON
     * - .retrieve() = sends the request and gets response
     * - .bodyToMono(...) = converts response JSON to Java object
     * - .onErrorMap(...) = handles errors
     * - .retry(3) = automatically retry up to 3 times on failure
     *
     * @param request The ChatCompletionRequest to send
     * @return Mono<ChatCompletionResponse> containing the response (or error)
     */
    private Mono<ChatCompletionResponse> makeApiCall(ChatCompletionRequest request) {

        return webClient
                // HTTP METHOD: POST
                .post()

                // ENDPOINT: /chat/completions
                // Full URL: https://api.groq.com/openai/v1/chat/completions
                // (baseUrl from config + this URI)
                .uri("/chat/completions")

                // HEADERS: Add Authorization header with Bearer token
                // Format: "Authorization: Bearer <api_key>"
                // This authenticates our request to Groq API
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)

                // BODY: Serialize ChatCompletionRequest to JSON and send
                // Spring/Jackson automatically converts the DTO to JSON
                // The JSON will look like:
                // {
                //   "model": "mixtral-8x7b-32768",
                //   "messages": [...],
                //   "temperature": 0.7,
                //   ...
                // }
                .bodyValue(request)

                // RETRIEVE: Send the request and get back the response
                // This triggers the actual HTTP call
                .retrieve()

                // ERROR HANDLING: Handle HTTP error responses (4xx, 5xx)
                .onStatus(
                        // Condition: If status is 4xx (client error)
                        httpStatus -> httpStatus.is4xxClientError(),
                        // Handler: Log and convert to meaningful exception
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("4xx Client Error - Status: {}, Body: {}",
                                              response.statusCode(), body);
                                    return Mono.error(new RuntimeException(
                                        "Client error: " + response.statusCode() + " - " + body
                                    ));
                                })
                )

                // ERROR HANDLING: Handle HTTP error responses (5xx)
                .onStatus(
                        // Condition: If status is 5xx (server error)
                        httpStatus -> httpStatus.is5xxServerError(),
                        // Handler: Log and convert to meaningful exception
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("5xx Server Error - Status: {}, Body: {}",
                                              response.statusCode(), body);
                                    return Mono.error(new RuntimeException(
                                        "Server error: " + response.statusCode() + " - " + body
                                    ));
                                })
                )

                // RESPONSE BODY: Convert JSON response to ChatCompletionResponse DTO
                // Jackson automatically deserializes JSON based on DTO fields
                // The response JSON fields map to Java fields via @JsonProperty annotations
                .bodyToMono(ChatCompletionResponse.class)

                // ERROR HANDLING: Catch any other exceptions (network, serialization, etc.)
                .onErrorMap(throwable -> {
                    // Log the error
                    log.error("Error during Groq API call", throwable);

                    // If it's already a RuntimeException, return as-is
                    if (throwable instanceof RuntimeException) {
                        return throwable;
                    }

                    // Otherwise wrap it in a RuntimeException for consistent error handling
                    return new RuntimeException("Failed to call Groq API: " + throwable.getMessage(), throwable);
                })

                // RETRY: Retry up to 3 times if request fails
                // This handles transient network issues
                // If all 3 retries fail, returns the error to caller
                .retry(3);
    }

    /**
     * Alternative method: Send a chat message WITH conversation history.
     *
     * This is useful for multi-turn conversations where context matters.
     *
     * Example:
     * User: "What is Spring Boot?"
     * AI: "Spring Boot is a framework..."
     * User: "Tell me about its features" <- Previous response is included as context
     *
     * @param userMessage Current user message
     * @param conversationHistory Previous messages in this conversation
     * @return Mono<ChatCompletionResponse> containing the response
     */
    public Mono<ChatCompletionResponse> chatWithHistory(
            String userMessage,
            java.util.List<ChatMessage> conversationHistory) {

        log.info("Chat with history. User message: {}, History size: {}",
                 userMessage, conversationHistory.size());

        // Build messages: System + History + New User Message
        java.util.List<ChatMessage> allMessages = new java.util.ArrayList<>();

        // Add system instruction
        allMessages.add(ChatMessage.builder()
                .role("system")
                .content("You are a helpful, professional assistant. "
                        + "Provide clear, concise, and accurate answers.")
                .build());

        // Add conversation history
        allMessages.addAll(conversationHistory);

        // Add current user message
        allMessages.add(ChatMessage.builder()
                .role("user")
                .content(userMessage)
                .build());

        // Build request with all messages
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(defaultModel)
                .messages(allMessages)
                .temperature(0.7)
                .maxTokens(1000)
                .topP(0.9)
                .frequencyPenalty(0.0)
                .presencePenalty(0.0)
                .build();

        return makeApiCall(request)
                .doOnNext(response -> log.info("Response generated. Tokens: {}",
                                                response.getUsage().getTotalTokens()))
                .doOnError(error -> log.error("Error in chatWithHistory", error));
    }
}

