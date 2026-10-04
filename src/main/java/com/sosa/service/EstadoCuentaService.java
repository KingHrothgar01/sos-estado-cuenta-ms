package com.sosa.service;

import static com.sosa.util.Constants.BUSINESS_MSG_ERR_EC_001;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_EC_002;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_EC_003;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_EC_004;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_EC_017;
import static com.sosa.util.Constants.USER_ALTA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sosa.exception.HTTP404Exception;
import com.sosa.model.EstadoCuenta;
import com.sosa.model.Prestamo;
import com.sosa.model.TasaInteres;
import com.sosa.model.dto.DateRangeDTO;
import com.sosa.model.dto.EstadoCuentaDTO;
import com.sosa.repository.ClienteRepository;
import com.sosa.repository.EstadoCuentaRepository;
import com.sosa.util.EstadoCuentaUtils;

@Service
public class EstadoCuentaService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EstadoCuentaService.class);

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private EstadoCuentaRepository estadoCuentaRepository;

	@Autowired
	private SaldoInsolutoService saldoInsolutoService;

	@Autowired
	private SaldoPromedioService saldoPromedioService;

	@Autowired
	private TasasService tasasService;

	@Autowired
	private MovimientosService movimientosService;

	public Optional<EstadoCuentaDTO> saveEstadoCuenta(EstadoCuentaDTO dto) {
		Prestamo prestamo = clienteRepository
				            .findById(dto.getPrestamoIdCliente()).orElseThrow(() -> new HTTP404Exception(BUSINESS_MSG_ERR_EC_002))
							.getPrestamos().stream().filter(p -> p.getIdPrestamo().equalsIgnoreCase(dto.getIdPrestamo()))
							.findFirst().orElseThrow(() -> new HTTP404Exception(BUSINESS_MSG_ERR_EC_003));
		
		DateRangeDTO range = EstadoCuentaUtils.getPeriodo(prestamo.getDiaCorte().byteValue(), dto.getTasaInteresMes(), dto.getTasaInteresAnio())
				.orElseThrow(() -> new HTTP404Exception(BUSINESS_MSG_ERR_EC_004));
		
		if (estadoCuentaRepository.findByFechaInicioPeriodoAndFechaFinPeriodoAndPrestamo(range.getStartDate(), range.getEndDate(), prestamo) == null) {
			LocalDate fechaActual = LocalDate.now();
				
			if (fechaActual.isAfter(range.getEndDate())) {
				EstadoCuenta estadoCuenta = new EstadoCuenta();

				ModelMapper mapper = new ModelMapper();
				estadoCuenta.setTasaInteres(mapper.map(tasasService.findSynchTasasByMonthAndYear(dto.getTasaInteresMes(), dto.getTasaInteresAnio()), TasaInteres.class));
				
				Double monthlyAverageBalance = saldoPromedioService.calculateMonthlyAverageBalance(dto.getPrestamoIdCliente(), dto.getIdPrestamo(), range);
				Double interes = movimientosService.calculateOrdinaryInterest(monthlyAverageBalance, (double)estadoCuenta.getTasaInteres().getAnualOrdinaria());
				Double iva = movimientosService.getTax(interes, 16D);
	
				estadoCuenta.setFechaInicioPeriodo(range.getStartDate());
				estadoCuenta.setFechaFinPeriodo(range.getEndDate());
				estadoCuenta.setFechaCorte(range.getEndDate());
				estadoCuenta.setFechaLimitePago(range.getEndDate().plusMonths(1));
				estadoCuenta.setInteresesOrdinarios(interes);
				estadoCuenta.setIva(iva);
				estadoCuenta.setSaldoInsolutoInicial(new BigDecimal(saldoInsolutoService.calculateCutOffDateBalance(dto.getPrestamoIdCliente(), dto.getIdPrestamo(), range) + interes + iva).setScale(2, RoundingMode.HALF_UP).doubleValue());
				estadoCuenta.setSaldoPromedioMes(monthlyAverageBalance);
				estadoCuenta.setComisiones(movimientosService.calculateFeesCharged(dto.getPrestamoIdCliente(), dto.getIdPrestamo(), range));
				estadoCuenta.setDisposiciones(movimientosService.calculatePurchasesDebits(dto.getPrestamoIdCliente(), dto.getIdPrestamo(), range));
				estadoCuenta.setPagos(movimientosService.calculatePaymentsCredits(dto.getPrestamoIdCliente(), dto.getIdPrestamo(), range));
				estadoCuenta.setInteresesMoratorios(0D);
				estadoCuenta.setPrestamo(prestamo);
				estadoCuenta.setFechaRegistro(new Date());
				estadoCuenta.setUsuarioRegistra(USER_ALTA);
				estadoCuenta.setCreditoDisponible(0D);
				estadoCuenta.setNumeroDiasPeriodo((int) ChronoUnit.DAYS.between(range.getStartDate(), range.getEndDate()) + 1);
				estadoCuenta.setActivo(true);
				
				prestamo.setSaldoInsoluto(estadoCuenta.getSaldoInsolutoInicial());
				prestamo.setFechaActualizacion(new Date());
				prestamo.setUsuarioActualiza(USER_ALTA);
				
				return Optional.ofNullable(mapper.map(estadoCuentaRepository.save(estadoCuenta), EstadoCuentaDTO.class));
			}else
				throw new HTTP404Exception(BUSINESS_MSG_ERR_EC_017);
		}else
			throw new HTTP404Exception(BUSINESS_MSG_ERR_EC_001);
	}

//	public Optional<EstadoCuenta> getEstadoCuenta(long id) {
//		return estadoCuentaRepository.findById(id);
//	}
//
//	public void updateEstadoCuenta(EstadoCuenta estadoCuenta) {
//		estadoCuentaRepository.save(estadoCuenta);
//	}
//
//	public void deleteEstadoCuenta(EstadoCuenta estadoCuenta) {
//		estadoCuentaRepository.delete(estadoCuenta);
//	}
//
//	public Page<EstadoCuenta> getAllEstadosCuenta(Integer page, Integer size) {
//		Page<EstadoCuenta> pageOfPersons = estadoCuentaRepository.findAll(PageRequest.of(page, size));
//		// example of adding to the /metrics
//
//		if (size > 50) {
//			LOGGER.info("Large Page Size for getAllPersons");
//		}
//		return pageOfPersons;
//	}
}
