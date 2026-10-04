package com.sosa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sosa.exception.HTTP404Exception;
import com.sosa.model.Prestamo;
import com.sosa.model.dto.DateRangeDTO;
import com.sosa.repository.ClienteRepository;
import com.sosa.repository.MovimientoRepository;

@Service
public class MovimientosService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MovimientosService.class);
	
	private static final Integer NUMERO_MESES = 12;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private MovimientoRepository movimientoRepository;
	
	public List<Double> calculatePaymentsCredits(String id, DateRangeDTO range) {
		LOGGER.info("+--- Calculating Payments & Credits ---+");
		List<Double> pagos = new ArrayList<>();
		
		clienteRepository.findById(id).get().getPrestamos().stream().forEach(p -> {
			pagos.add(p.getMovimientos().stream().filter(m -> m.getCatalogoOperacion().getIdCatalogoOperacion() == 1).mapToDouble(m -> m.getMonto()).sum());
		});
		return pagos;
	}
	
	public Double calculatePaymentsCredits(String id, String idPrestamo, DateRangeDTO range) {
		LOGGER.info("+--- Calculating Payments & Credits by Loan ---+");
		
		Function<? super Prestamo, ? extends Double> ammount = p -> {
			return movimientoRepository.findMovimientosByDateAndLoan(
					Optional.ofNullable(p), range).stream().filter(m -> m.getCatalogoOperacion().getIdCatalogoOperacion() == 1)
						.mapToDouble(m -> m.getMonto()).sum();
		};
		
		return clienteRepository.findById(id).orElseThrow(() -> new HTTP404Exception("Cliente no existe.")).
			getPrestamos().stream().filter(p -> p.getIdPrestamo().equalsIgnoreCase(idPrestamo)).findFirst().map(ammount).orElse(null);
	}
	
	public List<Double> calculatePurchasesDebits(String id, DateRangeDTO range) {
		LOGGER.info("+--- Calculating Purchases & Debits & Drafts ---+");
		List<Double> pagos = new ArrayList<>();
		
		clienteRepository.findById(id).get().getPrestamos().stream().forEach(p -> {
			pagos.add(p.getMovimientos().stream().filter(m -> m.getCatalogoOperacion().getIdCatalogoOperacion() == 2).mapToDouble(m -> m.getMonto()).sum());
		});
		return pagos;
	}
	
	public Double calculatePurchasesDebits(String id, String idPrestamo, DateRangeDTO range) {
		LOGGER.info("+--- Calculating Purchases & Debits & Drafts ---+");
		
		Function<? super Prestamo, ? extends Double> ammount = p -> {
			return movimientoRepository.findMovimientosByDateAndLoan(Optional.of(p), range).stream().filter(m -> m.getCatalogoOperacion().getIdCatalogoOperacion() == 2)
						.mapToDouble(m -> m.getMonto()).sum();
		};
		
		return clienteRepository.findById(id).orElseThrow(() -> new HTTP404Exception("Cliente no existe.")).
			getPrestamos().stream().filter(p -> p.getIdPrestamo().equalsIgnoreCase(idPrestamo)).findFirst().map(ammount).orElse(null);
	}
	
	public List<Double> calculateFeesCharged(String id, DateRangeDTO range) {
		LOGGER.info("+--- Calculating Fees Charged ---+");
		List<Double> pagos = new ArrayList<>();
		
		clienteRepository.findById(id).get().getPrestamos().stream().forEach(p -> {
			pagos.add(p.getMovimientos().stream().filter(m -> m.getCatalogoOperacion().getIdCatalogoOperacion() == 3).mapToDouble(m -> m.getMonto()).sum());
		});
		return pagos;
	}
	
	public Double calculateFeesCharged(String id, String idPrestamo, DateRangeDTO range) {
		LOGGER.info("+--- Calculating Fees Charged ---+");
		
		Function<? super Prestamo, ? extends Double> ammount = p -> {
			return movimientoRepository.findMovimientosByDateAndLoan(Optional.of(p), range).stream().filter(m -> m.getCatalogoOperacion().getIdCatalogoOperacion() == 3)
						.mapToDouble(m -> m.getMonto()).sum();
		};
		
		
		return clienteRepository.findById(id).orElseThrow(() -> new HTTP404Exception("Cliente no existe.")).
			getPrestamos().stream().filter(p -> p.getIdPrestamo().equalsIgnoreCase(idPrestamo)).findFirst().map(ammount).orElse(null);
	}
	
	public Double calculateOrdinaryInterest(Double saldoPromedioMensual, Double tasaInteresAnualOrdinaria) {
		LOGGER.info("+--- Calculating Interest Charged ---+");
		Double interesOrdinario = saldoPromedioMensual * tasaInteresAnualOrdinaria/(NUMERO_MESES*100);

		return interesOrdinario;
	}
	
	public Double getPercentage(Double incomes, Double tax) {
		LOGGER.info("+--- Calculating Percentage ---+");
		return (tax * 100)/incomes;
	}
	
	public Double getBase(Double incomes, Double percentage) {
		LOGGER.info("+--- Calculating Base ---+");
		return incomes * (percentage/100);
	}
	
	public Double getTax(Double base, Double percentage) {
		LOGGER.info("+--- Calculating Tax ---+");
		return (base * percentage)/100;
	}
}