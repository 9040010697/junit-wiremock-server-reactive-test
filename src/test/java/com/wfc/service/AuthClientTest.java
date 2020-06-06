package com.wfc.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.wfc.utils.Constants;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("dev")
public class AuthClientTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private AuthClient authClient;

    @BeforeAll
    static void setUpBeforeAll() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
    }

    @AfterAll
    static void tearDownAll() {
        wireMockServer.shutdown();
    }

    @Test
    void shouldReturnSuccessWithValidInput() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/token/generate"))
                .withHeader("x-auth-role", WireMock.equalTo(Constants.USER_NAME))
                .withHeader("x-auth-user", WireMock.equalTo(Constants.USER_ROLE))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withBody(Constants.ACCESS_TOKEN)));

        Mono<String> authToken = authClient.getAuthToken(Constants.USER_NAME, Constants.USER_ROLE);

        StepVerifier.create(authToken)
                .expectSubscription()
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWithInValidInput() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/token/generate"))
                .withHeader("x-auth-user", WireMock.equalTo("INVALID_USER"))
                .withHeader("x-auth-role", WireMock.equalTo(Constants.USER_ROLE))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.BAD_REQUEST.value())));

        Mono<String> authToken = authClient.getAuthToken("INVALID_USER", Constants.USER_ROLE);

        StepVerifier.create(authToken)
                .expectSubscription()
                .expectErrorMatches(RuntimeException.class::isInstance)
                .verify();
    }

}
