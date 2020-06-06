package com.wfc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wfc.model.Person;
import com.wfc.service.ClientService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client")
public class ClientController {

	@Autowired
	private ClientService service;
	
	@GetMapping 
	public Mono<Person> getPerson() {
		return service.getPerson("Dhananjaya", "9040010697");
	}

}
