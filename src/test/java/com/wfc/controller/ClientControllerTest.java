package com.wfc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wfc.exceptions.AppException;
import com.wfc.model.Person;
import com.wfc.service.ClientService;
import com.wfc.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;

@SpringBootTest
@ActiveProfiles("dev")
public class ClientControllerTest {

    @Autowired
    private ApplicationContext context;

    @MockBean
    private ClientService service;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        this.webTestClient = WebTestClient.bindToApplicationContext(context)
                .configureClient().baseUrl("/client").build();
    }

    @Test
    void shouldReturnSuccessWhenGetPersonTest() throws IOException {

        String content = new String(Files.readAllBytes(Constants.SEARCH_RESPONSE_PATH));

        Mockito.when(service.getPerson(Constants.PERSON_NAME, Constants.MOBILE_NUMBER))
                .thenReturn(Mono.just(new ObjectMapper().readValue(content, Person.class)));

        webTestClient.get()
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody().json(content);
    }


    @Test
    void shouldErrorNotFoundWhenGetPersonTest() {
        Mockito.when(service.getPerson(Constants.PERSON_NAME, Constants.MOBILE_NUMBER))
                .thenThrow(new AppException(HttpStatus.NOT_FOUND, "Person Not found"));

        webTestClient.get()
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    void shouldErrorUnAuthorizedWhenGetPersonTest() {
        Mockito.when(service.getPerson(Constants.PERSON_NAME, Constants.MOBILE_NUMBER))
                .thenThrow(new AppException(HttpStatus.UNAUTHORIZED, "Token is not valid"));

        webTestClient.get()
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

}
