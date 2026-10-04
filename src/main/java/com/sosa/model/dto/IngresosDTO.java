package com.sosa.model.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngresosDTO implements Serializable{

	private static final long serialVersionUID = 6740069125172742321L;
	
	private Double base1;
	private Double base2;
	private Double base3;
	private Double base4;
	
	private Double inpuestos;
	private Double ingresos;
	
}
