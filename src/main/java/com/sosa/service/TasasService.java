package com.sosa.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.sosa.exception.HTTP404Exception;
import com.sosa.exception.HTTP500Exception;
import com.sosa.model.dto.TasasDTO;

import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@NoArgsConstructor
public class TasasService {

	@Autowired
	private WebClient webClient;

	public Mono<TasasDTO> findAsynchTasasByMonthAndYear(byte mes, short anio) {
		Map<String, Object> mapa = new HashMap<>();
		mapa.put("mes", mes);
		mapa.put("anio", anio);

		Mono<TasasDTO> entityMono = webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/salesandservices/loans/v1/rates/{mes}/{anio}").build(mapa))
				.exchangeToMono(this::handleResponse);

		return entityMono;
	}
	
	public TasasDTO findSynchTasasByMonthAndYear(byte mes, short anio) {
		Map<String, Object> mapa = new HashMap<>();
		mapa.put("mes", mes);
		mapa.put("anio", anio);

		return webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/salesandservices/loans/v1/rates/{mes}/{anio}").build(mapa))
				.retrieve()
				.bodyToMono(TasasDTO.class)
				.block();
	}

	private Mono<TasasDTO> handleResponse(ClientResponse response) {

		if (response.statusCode().is2xxSuccessful()) {
			return response.bodyToMono(TasasDTO.class);
		} else if (response.statusCode().is4xxClientError()) {
			// Handle client errors (e.g., 404 Not Found)
			return Mono.error(new HTTP404Exception("Employee not found"));
		} else if (response.statusCode().is5xxServerError()) {
			// Handle server errors (e.g., 500 Internal Server Error)
			return Mono.error(new HTTP500Exception("Server error"));
		} else {
			// Handle other status codes as needed
			return Mono.error(new RuntimeException("Unexpected error"));
		}
	}

}
