package com.akshay.groq.controller;

import com.akshay.groq.dto.ChatCompletionRequest;
import com.akshay.groq.dto.ChatCompletionResponse;
import com.akshay.groq.dto.ChatMessage;
import com.akshay.groq.service.GroqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Groq AI Chat API.
 *
 * This controller layer handles:
 * 1. HTTP request routing (@GetMapping, @PostMapping, etc.)
 * 2. Receiving HTTP request bodies (deserialization)
 * 3. Calling service methods (business logic)
 * 4. Formatting HTTP responses (serialization)
 * 5. Setting appropriate HTTP status codes
 *
 * Architecture: Controller ← Service ← WebClient ← API
 *
 * REST Endpoints created:
 * - POST /api/chat/simple      : Simple one-message chat
 * - POST /api/chat/with-history: Multi-turn conversation
 * - GET  /api/chat/health      : Health check endpoint
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class GroqController {

    /**
     * Service bean injected via constructor.
     *
     * Spring automatically injects the GroqService bean.
     * This is where all business logic is delegated to.
     */
    private final GroqService groqService;

    /**
     * Simple chat endpoint - Single message to AI.
     *
     * HTTP Method: POST
     * URL: http://localhost:8080/api/chat/simple
     *
     * Request Body (JSON):
     * {
     *   "message": "What is Java?"
     * }
     *
     * Response Body (JSON):
     * {
     *   "success": true,
     *   "response": "Java is a programming language...",
     *   "tokensUsed": 54,
     *   "timestamp": "2024-01-23T10:30:00Z"
     * }
     *
     * @param request The incoming HTTP request body containing "message" field
     * @return ResponseEntity with:
     *         - Status: 200 OK (success) or error status
     *         - Body: Response DTO with AI's answer and metadata
     */
    @PostMapping("/simple")
    public Mono<ResponseEntity<Map<String, Object>>> simpleChat(
            @RequestBody ChatRequest request) {

        // Log incoming request
        log.info("Received simple chat request. User message: {}", request.getMessage());

        // Validate input
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            log.warn("Invalid message received: null or empty");

            // Return error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Message cannot be empty");

            return Mono.just(ResponseEntity
                    .badRequest()  // 400 Bad Request
                    .body(errorResponse));
        }

        // Call service to get AI response
        // This returns a Mono (async/non-blocking)
        return groqService.chatWithGroq(request.getMessage())

                // Transform the ChatCompletionResponse into a response DTO
                // map() applies this transformation to the value when it arrives
                .map(response -> {
                    log.info("Successfully got response from AI");

                    // Build response DTO
                    Map<String, Object> successResponse = new HashMap<>();
                    successResponse.put("success", true);
                    successResponse.put("response", response.getResponseText());  // AI's answer
                    successResponse.put("tokensUsed", response.getUsage().getTotalTokens());
                    successResponse.put("model", response.getModel());
                    successResponse.put("finishReason", response.getChoices().get(0).getFinishReason());
                    successResponse.put("timestamp", System.currentTimeMillis());

                    // Return HTTP 200 OK with response body
                    return ResponseEntity.ok(successResponse);
                })

                // Error handling: If service throws exception, catch it here
                .onErrorResume(error -> {
                    log.error("Error in simpleChat endpoint", error);

                    // Build error response
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("error", error.getMessage());

                    // Return HTTP 500 Internal Server Error
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(errorResponse));
                });
    }

    /**
     * Multi-turn conversation endpoint with history.
     *
     * HTTP Method: POST
     * URL: http://localhost:8080/api/chat/with-history
     *
     * Request Body (JSON):
     * {
     *   "message": "Tell me about its features",
     *   "history": [
     *     {"role": "user", "content": "What is Spring Boot?"},
     *     {"role": "assistant", "content": "Spring Boot is a framework..."}
     *   ]
     * }
     *
     * Response: Same as simpleChat endpoint
     *
     * @param request Contains current message and conversation history
     * @return Mono<ResponseEntity> with AI response
     */
    @PostMapping("/with-history")
    public Mono<ResponseEntity<Map<String, Object>>> chatWithHistory(
            @RequestBody ChatHistoryRequest request) {

        log.info("Received chat request with history. Message: {}, History size: {}",
                 request.getMessage(),
                 request.getHistory() != null ? request.getHistory().size() : 0);

        // Validate input
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            log.warn("Invalid message received");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Message cannot be empty");

            return Mono.just(ResponseEntity
                    .badRequest()
                    .body(errorResponse));
        }

        // Get conversation history (default to empty list if not provided)
        List<ChatMessage> history = request.getHistory() != null ?
                                    request.getHistory() :
                                    List.of();

        // Call service with history
        return groqService.chatWithHistory(request.getMessage(), history)

                // Transform response
                .map(response -> {
                    log.info("Successfully got response with history context");

                    Map<String, Object> successResponse = new HashMap<>();
                    successResponse.put("success", true);
                    successResponse.put("response", response.getResponseText());
                    successResponse.put("tokensUsed", response.getUsage().getTotalTokens());
                    successResponse.put("model", response.getModel());
                    successResponse.put("finishReason", response.getChoices().get(0).getFinishReason());
                    successResponse.put("timestamp", System.currentTimeMillis());

                    return ResponseEntity.ok(successResponse);
                })

                // Error handling
                .onErrorResume(error -> {
                    log.error("Error in chatWithHistory endpoint", error);

                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("error", error.getMessage());

                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(errorResponse));
                });
    }

    /**
     * Health check endpoint.
     *
     * HTTP Method: GET
     * URL: http://localhost:8080/api/chat/health
     *
     * Response: {"status": "UP"}
     *
     * Use case: Monitoring, load balancers, or simple connectivity checks
     *
     * @return Simple status response
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {

        log.debug("Health check called");

        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Groq Integration API");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));

        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint to verify API is working.
     *
     * HTTP Method: GET
     * URL: http://localhost:8080/api/chat/test
     *
     * This is a simple blocking endpoint (not reactive).
     * Useful for quick testing without sending actual requests to Groq.
     *
     * @return Static test message
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {

        log.info("Test endpoint called");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Groq Integration API is running!");
        response.put("endpoints", "POST /api/chat/simple, POST /api/chat/with-history, GET /api/chat/health");

        return ResponseEntity.ok(response);
    }
}

/**
 * Request DTO for simple chat endpoint.
 *
 * Maps incoming JSON to Java object.
 *
 * Example JSON:
 * {
 *   "message": "What is Java?"
 * }
 */
class ChatRequest {
    private String message;

    public ChatRequest() {}

    public ChatRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

/**
 * Request DTO for chat with history endpoint.
 *
 * Maps incoming JSON with history to Java object.
 *
 * Example JSON:
 * {
 *   "message": "Tell me more",
 *   "history": [
 *     {"role": "user", "content": "What is Java?"},
 *     {"role": "assistant", "content": "Java is..."}
 *   ]
 * }
 */
class ChatHistoryRequest {
    private String message;
    private List<ChatMessage> history;

    public ChatHistoryRequest() {}

    public ChatHistoryRequest(String message, List<ChatMessage> history) {
        this.message = message;
        this.history = history;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ChatMessage> getHistory() {
        return history;
    }

    public void setHistory(List<ChatMessage> history) {
        this.history = history;
    }
}

