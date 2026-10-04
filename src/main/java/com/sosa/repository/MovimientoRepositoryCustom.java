package com.sosa.repository;

import java.util.List;
import java.util.Optional;

import com.sosa.model.Movimiento;
import com.sosa.model.Prestamo;
import com.sosa.model.dto.DateRangeDTO;

public interface MovimientoRepositoryCustom {
	
	List<Movimiento> findMovimientosByDateAndLoan(Optional<Prestamo> prestamo, DateRangeDTO range);

}
