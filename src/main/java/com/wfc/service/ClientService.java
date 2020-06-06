package com.wfc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wfc.model.Person;

import reactor.core.publisher.Mono;

@Service
public class ClientService {

	@Autowired
	private AuthClient authClient;
	
	@Autowired
	private PersonClient personClient;

	
	public Mono<Person> getPerson(String name, String mobile){
		return authClient.getAuthToken("ADMIN", "ADMIN")
				.flatMap(authToken-> personClient.getPerson(name, mobile, authToken));
	}

}
