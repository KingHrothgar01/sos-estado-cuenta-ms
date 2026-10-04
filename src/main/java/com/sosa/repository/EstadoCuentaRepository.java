package com.sosa.repository;

import java.time.LocalDate;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.sosa.model.EstadoCuenta;
import com.sosa.model.Prestamo;

public interface EstadoCuentaRepository extends PagingAndSortingRepository<EstadoCuenta, Long>{
	
	EstadoCuenta findByFechaInicioPeriodoAndFechaFinPeriodoAndPrestamo(LocalDate fechaInicioPeriodo, LocalDate fechaFinPeriodo, Prestamo prestamo);
}
