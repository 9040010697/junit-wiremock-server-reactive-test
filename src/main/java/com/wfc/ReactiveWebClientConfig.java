package com.wfc;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
@Slf4j
public class ReactiveWebClientConfig {


  @Value("${api.idle.read-timeout:10000}")
  private long readIdleTimeout;
  @Value("${api.idle.write-timeout:10000}")
  private long writeIdleTimeout;
  @Value("${api.idle.all-idle-timeout:10000}")
  private long allIdleTimeout;

 
  @Bean
  public WebClient personWebClient(@Value("${api.person.url}") String url,
                                     @Value("${api.person.read-timeout}") int readTimeOut,
                                     @Value("${api.person.write-timeout}") int writeTimeOut,
                                     @Value("${api.person.connection-timeout}")
                                             int connectionTimeOut) {
    
    return this.createWebClient(readTimeOut, writeTimeOut, connectionTimeOut, url);
  }
  
  @Bean
  public WebClient authWebClient(@Value("${api.auth.url}") String url,
                                     @Value("${api.auth.read-timeout}") int readTimeOut,
                                     @Value("${api.auth.write-timeout}") int writeTimeOut,
                                     @Value("${api.auth.connection-timeout}")
                                             int connectionTimeOut) {
    
    return this.createWebClient(readTimeOut, writeTimeOut, connectionTimeOut, url);
  }


  private WebClient createWebClient(int readTimeOut, int writeTimeOut, int connectionTimeOut,
                                    String url) {
    HttpClient httpClient = this.configureHttpClient(readTimeOut, writeTimeOut, connectionTimeOut);
    return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .baseUrl(url)
            .filter(logRequest())
            .filter(logResponse())
            .build();

  }

  @SneakyThrows
  private HttpClient configureHttpClient(int readTimeOut, int writeTimeOut,
                                         int connectionTimeOut) {

    ConnectionProvider fixedPool = ConnectionProvider.create("fixedPool",1000);

    SslContext sslContext = SslContextBuilder.forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build();


    return HttpClient.create(fixedPool)
            .secure(t -> t.sslContext(sslContext))
            .tcpConfiguration(client -> client
                    .doOnConnected(conn -> conn
                            .addHandlerLast(
                                    new ReadTimeoutHandler(readTimeOut, TimeUnit.MILLISECONDS))
                            .addHandlerLast(
                                    new WriteTimeoutHandler(writeTimeOut, TimeUnit.MILLISECONDS))
                            .addHandlerLast(
                                    new IdleStateHandler(readIdleTimeout, writeIdleTimeout,
                                    allIdleTimeout, TimeUnit.MILLISECONDS)))
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeOut));
  }


  private ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      log.info("Request {} {} ", clientRequest.method(), clientRequest.url().getRawPath());
      return Mono.just(clientRequest);
    });
  }

  private ExchangeFilterFunction logResponse() {
    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
      log.info("Response status code  {}", clientResponse.statusCode());
      return Mono.just(clientResponse);
    });
  }
}
