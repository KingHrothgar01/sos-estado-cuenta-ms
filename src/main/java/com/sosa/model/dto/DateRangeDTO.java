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
public class DateRangeDTO implements Serializable{

	private static final long serialVersionUID = -2260953493126523728L;
	
	private LocalDate startDate; 
	private LocalDate endDate;

}
