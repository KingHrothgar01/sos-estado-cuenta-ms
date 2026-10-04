package com.sosa.model.dto;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

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
public class TasasDTO extends RepresentationModel<TasasDTO> implements Serializable {

	private static final long serialVersionUID = 4846719084685423276L;

	@JsonProperty("id-tasa-interes")
	@JsonSetter("id-tasa-interes")
	private long idTasaInteres;
	
	@NotNull(message = "El mes es requerido.")
	@JsonProperty("mes")
	@JsonSetter("mes")
	private byte mes;
	
	@NotNull(message = "El anio es requerido.")
	@JsonProperty("anio")
	@JsonSetter("anio")
	private short anio;
	
	@NotNull(message = "El CAT sin I.V.A. es requerido.")
	@JsonProperty("cat-sin-iva")
	@JsonSetter("cat-sin-iva")
	private float catSinIva;
	
	@NotNull(message = "La tasa de interés anual ordinaria es requerida.")
	@JsonProperty("anual-ordinaria")
	@JsonSetter("anual-ordinaria")
	private float anualOrdinaria;
	
	@JsonProperty("anual-moratoria")
	@JsonSetter("anual-moratoria")
	private float anualMoratoria;
	
	@JsonProperty(value = "fecha-registro", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("fecha-registro")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date fechaRegistro;

	@JsonProperty(value = "fecha-actualizacion", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("fecha-actualizacion")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	//@FutureOrPresent(message = "La fecha de actualización tiene que ser mayor o igual a hoy.")
	private Date fechaActualizacion;

	@JsonProperty(value = "usuario-registra", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("usuario-registra")
	private String usuarioRegistra;

	@JsonProperty(value = "usuario-actualiza", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("usuario-actualiza")
	private String usuarioActualiza;
	
	@JsonProperty(value = "activo", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("activo")
	private Boolean activo;

}
