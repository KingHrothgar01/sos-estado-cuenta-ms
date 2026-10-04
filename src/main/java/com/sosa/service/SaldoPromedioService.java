package com.sosa.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sosa.exception.HTTP404Exception;
import com.sosa.model.Movimiento;
import com.sosa.model.Prestamo;
import com.sosa.model.dto.DateRangeDTO;
import com.sosa.model.dto.PrestamoDTO;
import com.sosa.model.dto.SaldoPromedioDTO;
import com.sosa.repository.ClienteRepository;
import com.sosa.repository.MovimientoRepository;

@Service
public class SaldoPromedioService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SaldoPromedioService.class);
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private MovimientoRepository movimientoRepository;
	
	public List<PrestamoDTO> calculateMonthlyAverageBalance(String idCliente, DateRangeDTO range) {
		LOGGER.info("+--- Calculating Monthly Average Balance ---+");
		
		List<PrestamoDTO> prestamos = new ArrayList<>();
		LocalDate fechaLocalInicio = range.getStartDate();
		LocalDate fechaLocalFin = range.getEndDate();
		int periodo = (int) ChronoUnit.DAYS.between(fechaLocalInicio, fechaLocalFin) + 1;
		
		clienteRepository.findById(idCliente).get().getPrestamos().stream().forEach(p -> {
			Double saldoInsolutoInicial = p.getSaldoInsoluto();
			
			List<Movimiento> movimientos = movimientoRepository.findMovimientosByDateAndLoan(Optional.ofNullable(p), range);
			
			List<SaldoPromedioDTO> saldo = new ArrayList<>(periodo);
			for (int i = 0; i < periodo; i++) {
				LocalDate fecha = fechaLocalInicio.plusDays(i);
				SaldoPromedioDTO saldoPromedio = new SaldoPromedioDTO();
				saldoPromedio.setFecha(fecha);
				saldoInsolutoInicial += movimientos.stream()
						   .filter(m -> m.getFechaMovimiento().isEqual(fecha))
						   .mapToDouble(m -> m.getMonto())
						   .sum();
				saldoPromedio.setSaldo(saldoInsolutoInicial);
				saldo.add(saldoPromedio);
			}
			
			PrestamoDTO prestamoDTO = new  PrestamoDTO();
			prestamoDTO.setIdPrestamo(p.getIdPrestamo());
			//prestamoDTO.setSaldoPromedioMensual((saldo.stream().mapToDouble(s -> s.getSaldo()).sum())/saldo.size());
			prestamos.add(prestamoDTO);
		});
		return prestamos;
	}
	
	public Double calculateMonthlyAverageBalance(String idCliente, String idPrestamo, DateRangeDTO range) {
		LOGGER.info("+--- Calculating Monthly Average Balance by Loan ---+");
		
		Function<? super Prestamo, ? extends Double> avg = p -> {
			LocalDate fechaLocalInicio = range.getStartDate();
			LocalDate fechaLocalFin = range.getEndDate();
			int periodo = (int) ChronoUnit.DAYS.between(fechaLocalInicio, fechaLocalFin) + 1;
			
			Double saldoInsolutoInicial = p.getSaldoInsoluto();
			List<Movimiento> movimientos = movimientoRepository.findMovimientosByDateAndLoan(Optional.of(p), range);
			
			List<SaldoPromedioDTO> saldo = new ArrayList<>(periodo);
			for (int i = 0; i < periodo; i++) {
				LocalDate fecha = fechaLocalInicio.plusDays(i);
				SaldoPromedioDTO saldoPromedio = new SaldoPromedioDTO();
				saldoPromedio.setFecha(fecha);
				saldoInsolutoInicial += movimientos.stream()
						   .filter(m -> m.getFechaMovimiento().isEqual(fecha))
						   .mapToDouble(m -> m.getMonto())
						   .sum();
				saldoPromedio.setSaldo(saldoInsolutoInicial);
				saldo.add(saldoPromedio);
			}
			
			return ((saldo.stream().mapToDouble(s -> s.getSaldo()).sum())/saldo.size());
		};
		
		return clienteRepository.findById(idCliente).orElseThrow(
				() -> new HTTP404Exception("Cliente no existe.")).getPrestamos().stream().filter(
						p -> p.getIdPrestamo().equalsIgnoreCase(idPrestamo)).findFirst().map(avg).orElse(null);
	}
}