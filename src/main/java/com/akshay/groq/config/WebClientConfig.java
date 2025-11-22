package com.akshay.groq.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Value("${groq.api.base-url:https://api.groq.com}")
    private String groqBaseUrl;

    @Value("${groq.api.timeout:30}")
    private int timeoutSeconds;

    /**
     * This method creates and configures a WebClient bean for communicating with Groq API.
     *
     * WebClient is a reactive HTTP client provided by Spring WebFlux.
     * It's non-blocking and uses Project Reactor for reactive streams.
     *
     * @return Configured WebClient instance
     */
    @Bean
    public WebClient groqWebClient() {
        // Step 1: Create an HttpClient with timeout and connection settings
        HttpClient httpClient = HttpClient.create()
                // Set connection timeout (time to establish connection)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutSeconds * 1000)
                // Set socket timeout (TCP_NODELAY disables Nagle's algorithm for faster transmission)
                .option(ChannelOption.SO_KEEPALIVE, true)
                // Set read timeout handler - max time to read a response
                .responseTimeout(java.time.Duration.ofSeconds(timeoutSeconds))
                // Configure the Netty pipeline with read/write timeouts
                .doOnConnected(conn ->
                    conn.addHandlerLast(new ReadTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS))
                       .addHandlerLast(new WriteTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS))
                );

        // Step 2: Build WebClient with custom HttpClient and default headers
        return WebClient.builder()
                // Set the base URL for all requests made through this WebClient
                .baseUrl(groqBaseUrl)
                // Use the configured HttpClient with timeout settings
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                // Configure default headers for all requests
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                // Build and return the WebClient instance
                .build();
    }
}

