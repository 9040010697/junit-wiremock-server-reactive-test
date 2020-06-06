package com.wfc.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.wfc.model.Person;
import com.wfc.utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("dev")
public class PersonClientTest {


    private static WireMockServer wireMockServer;

    @Autowired
    private PersonClient personClient;

    @BeforeAll
    static void setUpBeforeAll() {
        wireMockServer = new WireMockServer(8090);
        wireMockServer.start();
    }

    static void tearDownAll() {
        wireMockServer.shutdown();
    }

    @Test
    void shouldReturnSuccessWithValidInput() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/person/search"))
                .withHeader("personName", WireMock.equalTo(Constants.PERSON_NAME))
                .withHeader("mobile", WireMock.equalTo(Constants.MOBILE_NUMBER))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("clientResponse.json")));

        Mono<Person> responsePerson = personClient.getPerson(Constants.PERSON_NAME,
                Constants.MOBILE_NUMBER, Constants.ACCESS_TOKEN);

        StepVerifier.create(responsePerson)
                .expectSubscription()
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWithInValidInput() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/person/search"))
                .withHeader("personName", WireMock.equalTo("test"))
                .withHeader("mobile", WireMock.equalTo(Constants.MOBILE_NUMBER))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.NOT_FOUND.value())));

        Mono<Person> responsePerson = personClient.getPerson("test",
                Constants.MOBILE_NUMBER, Constants.ACCESS_TOKEN);

        StepVerifier.create(responsePerson)
                .expectSubscription()
                .expectErrorMatches(RuntimeException.class::isInstance)
                .verify();
    }
}
