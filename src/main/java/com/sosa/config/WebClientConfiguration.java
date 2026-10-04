package com.sosa.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfiguration {
	
	private static final String BASE_URL = "http://192.168.100.65:8090";
	
	private static final int TIMEOUT = 10000;
	
	@Bean
	public WebClient webClientWithTimeout() {
		HttpClient httpClient = HttpClient
				.create()
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
				.responseTimeout(Duration.ofMillis(TIMEOUT))
				.doOnConnected(connection -> {
					connection.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
					connection.addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
				});
		
		return WebClient
				.builder()
				.baseUrl(BASE_URL)
				.defaultCookie("cookie-name", "cookie-value")
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();
	}
}
