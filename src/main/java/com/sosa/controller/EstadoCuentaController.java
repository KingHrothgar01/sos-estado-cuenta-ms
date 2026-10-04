package com.sosa.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sosa.event.AbstractRestHandler;
import com.sosa.event.EstadoCuentaServiceEvent;
import com.sosa.exception.HTTP500Exception;
import com.sosa.model.dto.EstadoCuentaDTO;
import com.sosa.service.EstadoCuentaService;

import static com.sosa.util.Constants.*;

@RestController
public class EstadoCuentaController extends AbstractRestHandler {
	
	@Autowired
	private EstadoCuentaService estadoCuentaService;
	
	@PostMapping(value = "/prestamos/v1/estadocuenta", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public EstadoCuentaDTO createEstadoCuenta(@Valid @RequestBody EstadoCuentaDTO dto, HttpServletRequest request, HttpServletResponse response) {
		dto = (this.estadoCuentaService.saveEstadoCuenta(dto)).orElseThrow(
				() -> new HTTP500Exception(BUSINESS_MSG_ERR_EC_012));
		eventPublisher.publishEvent(new EstadoCuentaServiceEvent(this, "EstadoCuentaCreado", dto));
		response.setHeader("Location", request.getRequestURL().append("/").append(dto.getIdEstadoCuenta()).toString());
		return dto;
	}

	@GetMapping(value = "/prestamos/v1/estadocuenta", consumes = { "application/json", "application/xml" }, produces =  "application/hal+json")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody PagedModel<EstadoCuentaDTO> readAllEstadosCuenta(
			@RequestParam(value = "page", required = true, defaultValue = DEFAULT_PAGE_NUM) Integer page,
			@RequestParam(value = "size", required = true, defaultValue = DEFAULT_PAGE_SIZE) Integer size,
			@RequestParam(value = "property", required = true, defaultValue = DEFAULT_PAGE_PROPERTY) String prop,
			@RequestParam(value = "sortOrder", required = true, defaultValue = DEFAULT_PAGE_ORDER) String sortOrder,
			HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	@GetMapping(value = "/prestamos/v1/estadocuenta/{id}", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Optional<EstadoCuentaDTO> readEstadoCuenta(@PathVariable("id") Long id, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

	@PutMapping(value = "/prestamos/v1/estadocuenta/{id}", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public EstadoCuentaDTO updateEstadoCuenta(@PathVariable("id") Long id, @Valid @RequestBody EstadoCuentaDTO dto, HttpServletRequest request,
			HttpServletResponse response) {
		return null;
	}

	@DeleteMapping(value = "/prestamos/v1/estadocuenta/{id}", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public EstadoCuentaDTO deleteEstadoCuenta(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		return null;
	}
}
