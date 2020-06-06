package com.wfc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.wfc.exceptions.AppException;
import com.wfc.model.Person;

import reactor.core.publisher.Mono;

@Service
public class PersonClient {
	
	@Autowired
	@Qualifier("personWebClient")
	private WebClient webClient;

	public Mono<Person> getPerson(String name, String mobile, String authToken) {
		return webClient.get()
	            .uri(builder -> builder.path("/person/search").build())
	            .header("personName", name)
	            .header("mobile", mobile)
	            .header(HttpHeaders.AUTHORIZATION, authToken)
	            .retrieve()
	            .onStatus(HttpStatus.UNAUTHORIZED::equals, response ->
            		Mono.error(new AppException(response.statusCode(), "Token is not valid")))
	            .onStatus(HttpStatus.NOT_FOUND::equals, response ->
	            	Mono.error(new AppException(response.statusCode(), "Person not found")))
	            .onStatus(HttpStatus::is5xxServerError, response ->
	            	Mono.error(new AppException(response.statusCode(),"Server error occurred")))
	            .bodyToMono(Person.class)
	            .switchIfEmpty(Mono.empty());
	}

}
