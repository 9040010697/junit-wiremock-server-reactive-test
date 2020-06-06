package com.wfc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class AuthClient {
	
	@Autowired
	@Qualifier("authWebClient")
	private WebClient webClient;
	
	public Mono<String> getAuthToken(String userName, String role){
		return webClient.get()
	            .uri(builder -> builder.path("/token/generate").build())
	            .header("x-auth-role", role)
	            .header("x-auth-user", userName)
	            .retrieve()
	            .onStatus(HttpStatus::is4xxClientError, response ->
	            	Mono.error(new RuntimeException("Invalid input")))
	            .onStatus(HttpStatus::is5xxServerError, response ->
	            	Mono.error(new RuntimeException("Server error occurred")))
	            .bodyToMono(String.class)
	            .switchIfEmpty(Mono.empty());
	}

}
