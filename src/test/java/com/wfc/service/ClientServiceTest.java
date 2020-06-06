package com.wfc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wfc.model.Person;
import com.wfc.utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;

import static com.wfc.utils.Constants.*;

@SpringBootTest
@ActiveProfiles("dev")
public class ClientServiceTest {

    @Mock
    private AuthClient authClient;

    @Mock
    private PersonClient personClient;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldSuccessResponseWhenGetPersonTest() throws IOException {
        Mockito.when(authClient.getAuthToken(USER_NAME, USER_ROLE))
                .thenReturn(Mono.just(ACCESS_TOKEN));

        String content = new String(Files.readAllBytes(Constants.SEARCH_RESPONSE_PATH));
        Person person = new ObjectMapper().readValue(content, Person.class);
        Mockito.when(personClient.getPerson(PERSON_NAME,MOBILE_NUMBER, ACCESS_TOKEN))
                .thenReturn(Mono.just(person));

        Mono<Person> responsePerson = clientService.getPerson(PERSON_NAME, MOBILE_NUMBER);

        StepVerifier.create(responsePerson)
        .expectSubscription()
        .assertNext(Assertions::assertNotNull)
        .verifyComplete();
    }

    @Test
    void shouldErrorResponseWhenGetPersonTest() throws IOException {
        Mockito.when(authClient.getAuthToken(USER_NAME, USER_ROLE))
                .thenReturn(Mono.error(new RuntimeException("Invalid input")));
        Mono<Person> responsePerson = clientService.getPerson(PERSON_NAME, MOBILE_NUMBER);

        StepVerifier.create(responsePerson)
                .expectSubscription()
                .expectErrorMatches(RuntimeException.class::isInstance)
                .verify();

    }

}
