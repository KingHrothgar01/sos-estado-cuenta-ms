package com.sosa.model.dto;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoCuentaDTO extends RepresentationModel<EstadoCuentaDTO> {

	@JsonProperty("id-estado-cuenta")
	@JsonSetter("id-estado-cuenta")
	private Integer idEstadoCuenta;
	
	@JsonProperty("id-cliente")
	@JsonSetter("id-cliente")
	private String prestamoIdCliente;
	
	@JsonProperty("id-prestamo")
	@JsonSetter("id-prestamo")
	private String idPrestamo;
	
	@JsonProperty("mes")
	@JsonSetter("mes")
	private Byte tasaInteresMes;
	
	@JsonProperty("anio")
	@JsonSetter("anio")
	private Short tasaInteresAnio;

	@JsonProperty("fecha-inicio-periodo")
	@JsonSetter("fecha-inicio-periodo")
	private LocalDate fechaInicioPeriodo;
	
	@JsonProperty("fecha-fin-periodo")
	@JsonSetter("fecha-fin-periodo")
	private LocalDate fechaFinPeriodo;
	
	@JsonProperty("fecha-limite-pago")
	@JsonSetter("fecha-limite-pago")
	private LocalDate fechaLimitePago;
	
	@JsonProperty("fecha-corte")
	@JsonSetter("fecha-corte")
	private LocalDate fechaCorte;
	
	@JsonProperty("saldo-insoluto-inicial")
	@JsonSetter("saldo-insoluto-inicial")
	private Double saldoInsolutoInicial;
	
	@JsonProperty("tasa-interes-anual-ordinaria")
	@JsonSetter("tasa-interes-anual-ordinaria")
	private float tasaInteresAnualOrdinaria;
	
	@JsonProperty("tasa-interes-anual-moratoria")
	@JsonSetter("tasa-interes-anual-moratoria")
	private float tasaInteresAnualMoratoria;
	
	@JsonProperty("cat-sin-iva")
	@JsonSetter("cat-sin-iva")
	private Double tasaInteresCatSinIva;
	
	@JsonProperty("saldo-promedio-mes")
	@JsonSetter("saldo-promedio-mes")
	private Double saldoPromedioMes;
	
	@JsonProperty("intereses-ordinarios")
	@JsonSetter("intereses-ordinarios")
	private Double interesesOrdinarios;
	
	@JsonProperty("intereses-moratorios")
	@JsonSetter("intereses-moratorios")
	private Double interesesMoratorios;

	private Double comisiones;
	
	private Float iva;
	
	private Double disposiciones;

	private Double pagos;
	
	@JsonProperty("credito-disponible")
	@JsonSetter("credito-disponible")
	private Double creditoDisponible;
	
	@JsonProperty("numero-dias-periodo")
	@JsonSetter("numero-dias-periodo")
	private Integer numeroDiasPeriodo;
	
	@JsonProperty(value = "fecha-registro", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("fecha-registro")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date fechaRegistro;

	@JsonProperty(value = "fecha-actualizacion", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("fecha-actualizacion")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date fechaActualizacion;

	@JsonProperty(value = "usuario-registra", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("usuario-registra")
	private String usuarioRegistra;

	@JsonProperty(value = "usuario-actualiza", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("usuario-actualiza")
	private String usuarioActualiza;
	
	@JsonProperty("activo")
	@JsonSetter("activo")
	private Boolean activo;
}