package com.sosa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sosa.exception.HTTP404Exception;
import com.sosa.model.dto.DateRangeDTO;
import com.sosa.model.dto.PrestamoDTO;
import com.sosa.repository.ClienteRepository;
import com.sosa.repository.MovimientoRepository;

@Service
public class SaldoInsolutoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SaldoInsolutoService.class);

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private MovimientoRepository movimientoRepository;

	public List<PrestamoDTO> calculateCutOffDateBalance(String idClient, DateRangeDTO range) {
		LOGGER.info("+--- Calculating Cut Off Date Balance by id Client ---+");
		List<PrestamoDTO> prestamos = new ArrayList<>();

		clienteRepository.findById(idClient).orElseThrow(() -> new HTTP404Exception("Cliente no existe."))
				.getPrestamos().stream().forEach(p -> {
					PrestamoDTO prestamoDTO = new PrestamoDTO();
					prestamoDTO.setIdPrestamo(p.getIdPrestamo());
					prestamoDTO.setSaldoInsoluto(p.getSaldoInsoluto()
							+ movimientoRepository.findMovimientosByDateAndLoan(Optional.ofNullable(p), range).stream()
									.mapToDouble(m -> m.getMonto()).sum());
					prestamos.add(prestamoDTO);
				});

		return prestamos;
	}

	public Double calculateCutOffDateBalance(String idClient, String idPrestamo, DateRangeDTO range) {
		LOGGER.info("+--- Calculating Cut Off Date Balance by id Loan ---+");

		return clienteRepository.findById(idClient).orElseThrow(() -> new HTTP404Exception("MSG-ERR-C-002"))
				.getPrestamos().stream().filter(p -> p.getIdPrestamo().equalsIgnoreCase(idPrestamo)).findFirst()
				.map(p -> movimientoRepository.findMovimientosByDateAndLoan(Optional.of(p), range).stream()
						.mapToDouble(m -> m.getMonto()).sum() + p.getSaldoInsoluto())
				.orElse(null);
	}
}