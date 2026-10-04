package com.sosa.model.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoPromedioDTO implements Serializable{
	
	private static final long serialVersionUID = -5862193689513837377L;

	public LocalDate fecha;
	public Double saldo;
	
}
