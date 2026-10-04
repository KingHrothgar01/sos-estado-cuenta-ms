package com.sosa.service;

import static com.sosa.util.Constants.BUSINESS_MSG_ERR_EC_001;
import static com.sosa.util.Constants.USER_ALTA;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ActiveProfiles;

import com.sosa.exception.HTTP400Exception;
import com.sosa.model.Cliente;
import com.sosa.model.EstadoCuenta;
import com.sosa.model.Prestamo;
import com.sosa.model.dto.DateRangeDTO;
import com.sosa.model.dto.EstadoCuentaDTO;
import com.sosa.model.dto.TasasDTO;
import com.sosa.repository.ClienteRepository;
import com.sosa.repository.EstadoCuentaRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")

class EstadoCuentaServiceTests {
	
	@Mock
	EstadoCuentaRepository estadoCuentaRepository;
	
	@Mock
	ClienteRepository clienteRepository;
	
	@Mock
	TasasService tasasService;
	
	@Mock
	SaldoPromedioService saldoPromedioService;
	
	@Mock
	SaldoInsolutoService saldoInsolutoService;
	
	@Mock
	MovimientosService movimientosService;
	
	@InjectMocks
	EstadoCuentaService estadoCuentaService;
	
	private EstadoCuentaDTO dto;
	
	private EstadoCuenta model;
	
	private Cliente clienteCero;
	private Prestamo prestamoCero;
	private TasasDTO tasaInteresDiciembre;
	
	//private PagingDTO paging;

	@BeforeEach
	void setUp() throws Exception {
		clienteCero = Cliente.builder()
				.idCliente("2020090001")
				.nombre("Jose Juan")
				.apellidoPaterno("Sosa")
				.apellidoMaterno("Romero")
				.rfc("SORJ8204085P0")
				.curp("SORJ820408HDF")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		prestamoCero = Prestamo.builder()
				.idPrestamo("2020090001")
				.monto(120000D)
				.saldoInsoluto(125760.79)
				.cliente(clienteCero)
				.diaCorte(3)
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		tasaInteresDiciembre = TasasDTO.builder()
				.mes((byte)12)
				.anio((short)2022)
				.catSinIva(12.6f)
				.anualOrdinaria(11.97f)
				.anualMoratoria(0)
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		dto = EstadoCuentaDTO.builder()
				.prestamoIdCliente("2020090001")
				.idPrestamo("2020090001")
				.tasaInteresMes((byte)12)
				.tasaInteresAnio((short)2022)
				.build();
		
		ModelMapper mapper = new ModelMapper();
		model = mapper.map(dto, EstadoCuenta.class);
	}
	
	@Test
	@DisplayName("Test para guardar un estado de cuenta.")
	void test_guardar_estado_cuenta() {
		//given
		clienteCero.addPrestamo(prestamoCero);
		
		given(clienteRepository.findById(anyString())).willReturn(Optional.of(clienteCero));
		given(estadoCuentaRepository.findByFechaInicioPeriodoAndFechaFinPeriodoAndPrestamo(any(LocalDate.class), any(LocalDate.class), any(Prestamo.class))).willReturn(null);
		given(tasasService.findSynchTasasByMonthAndYear(anyByte(), anyShort())).willReturn(tasaInteresDiciembre);
		given(saldoPromedioService.calculateMonthlyAverageBalance(anyString(), anyString(), any(DateRangeDTO.class))).willReturn(127871.75);
		given(movimientosService.calculateOrdinaryInterest(anyDouble(), anyDouble())).willReturn(1276.13);
		given(movimientosService.getTax(anyDouble(), anyDouble())).willReturn(75.32);
		given(saldoInsolutoService.calculateCutOffDateBalance(anyString(), anyString(), any(DateRangeDTO.class))).willReturn(127408.49);
		given(movimientosService.calculateFeesCharged(anyString(), anyString(), any(DateRangeDTO.class))).willReturn(0.0);
		given(movimientosService.calculatePurchasesDebits(anyString(), anyString(), any(DateRangeDTO.class))).willReturn(12025.74);
		given(movimientosService.calculatePaymentsCredits(anyString(), anyString(), any(DateRangeDTO.class))).willReturn(10378.04);
		given(estadoCuentaRepository.save(any(EstadoCuenta.class))).willAnswer((invocation) -> invocation.getArgument(0));
		
		//when
		EstadoCuentaDTO estadoCuentaGuardado = estadoCuentaService.saveEstadoCuenta(dto).orElse(null);
		
		//then
		then(estadoCuentaGuardado).isNotNull();
		then(estadoCuentaGuardado.getTasaInteresAnualOrdinaria()).isEqualTo((float)11.97);
		then(estadoCuentaGuardado.getTasaInteresAnualMoratoria()).isEqualTo(0);
		then(estadoCuentaGuardado.getTasaInteresCatSinIva()).isEqualTo((float)12.6);
		then(estadoCuentaGuardado.getFechaInicioPeriodo()).isEqualTo("2022-11-04");
		then(estadoCuentaGuardado.getFechaFinPeriodo()).isEqualTo("2022-12-03");
		then(estadoCuentaGuardado.getFechaCorte()).isEqualTo("2022-12-03");
		then(estadoCuentaGuardado.getFechaLimitePago()).isEqualTo("2023-01-03");
		then(estadoCuentaGuardado.getInteresesOrdinarios()).isEqualTo(1276.13);
		then(estadoCuentaGuardado.getIva()).isEqualTo((float)75.32);
		then(estadoCuentaGuardado.getSaldoInsolutoInicial()).isEqualTo(128759.94);
		then(estadoCuentaGuardado.getSaldoPromedioMes()).isEqualTo(127871.75);
		then(estadoCuentaGuardado.getComisiones()).isEqualTo(0);
		then(estadoCuentaGuardado.getDisposiciones()).isEqualTo(12025.74);
		then(estadoCuentaGuardado.getPagos()).isEqualTo(10378.04);
		then(estadoCuentaGuardado.getInteresesMoratorios()).isEqualTo(0);
		then(estadoCuentaGuardado.getIdPrestamo()).isEqualTo("2020090001");
		then(estadoCuentaGuardado.getNumeroDiasPeriodo()).isEqualTo(30);
		then(estadoCuentaGuardado.getActivo()).isTrue();
	}
	
	@Test
	@DisplayName("Test para guardar un estado de cuenta que ha sido guardado previamente - Escenario de error 1.")
	void test_guardar_estado_cuenta_error_1() {
		//given
		given(estadoCuentaRepository.findById(anyLong())).willReturn(Optional.of(model));
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			estadoCuentaService.saveEstadoCuenta(dto);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_EC_001));
		verify(estadoCuentaRepository, never()).save(any(EstadoCuenta.class));
	}
	
//	@Test
//	@DisplayName("Test para obtener estado de cuenta por id.")
//	void test_obtener_cliente() {
//		//given
//		given(clienteRepository.findById(model.getprestamoIdCliente())).willReturn(Optional.of(model));
//		
//		//when
//		Optional<EstadoCuentaDTO> clientes = estadoCuentaService.findCliente(model.getprestamoIdCliente());
//		
//		//then
//		assertThat(clientes).isPresent();
//		assertThat(clientes.get().getprestamoIdCliente()).isEqualTo("000001");
//	}
	
//	@Test
//	@DisplayName("Test para obtener estado de cuenta por id, estado de cuenta no encontrado - Escenario de error 1.")
//	void test_obtener_cliente_error_1() {
//		//given
//		given(clienteRepository.findById(model.getprestamoIdCliente())).willReturn(Optional.empty());
//		
//		//when
//		HTTP404Exception thrown = assertThrows(HTTP404Exception.class, () -> {
//			estadoCuentaService.findCliente("000001");
//		});
//		
//		//then
//		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_EC_003));
//	}
	
//	@Test
//	@DisplayName("Test para eliminar un estado de cuenta por id.")
//	void test_eliminar_cliente() {
//		//given
//		given(clienteRepository.findById(anyString())).willReturn(Optional.of(model));
//		given(clienteRepository.save(any(EstadoCuenta.class))).willReturn(model);
//		
//		//when
//		Optional<EstadoCuentaDTO> clienteBorrado = estadoCuentaService.deleteEstadoCuenta("000001");
//		
//		//then
//		verify(clienteRepository, times(1)).save(any(EstadoCuenta.class));
//		then(clienteBorrado).isNotNull();
//		then(clienteBorrado).isPresent();
//		then(clienteBorrado.get().getActivo()).isFalse();
//	}
	
//	@Test
//	@DisplayName("Test para eliminar un estado de cuenta por id, estado de cuenta no encontrado - Escenario de error 1.")
//	void test_eliminar_cliente_error_1() {
//		//given
//		given(clienteRepository.findById(anyString())).willReturn(Optional.empty());
//		
//		//when
//		HTTP404Exception thrown = assertThrows(HTTP404Exception.class, () -> {
//			estadoCuentaService.deleteCliente("000001");
//		});
//		
//		//then
//		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_EC_009));
//	}

//	@Test
//	@DisplayName("Test para actualizar un estado de cuenta por id.")
//	void test_actualiza_cliente() {
//		//given
//		given(clienteRepository.findById(anyString())).willReturn(Optional.of(model));
//		given(clienteRepository.save(any(EstadoCuenta.class))).willReturn(model);
//		
//		//when
//		Optional<EstadoCuentaDTO> tasaActualizada = estadoCuentaService.updateCliente(dto);
//		
//		//then
//		assertThat(tasaActualizada).isPresent();
//	}
	
//	@Test
//	@DisplayName("Test para actualizar un estado de cuenta por id, estado de cuenta no encontrado - Escenario de error 1.")
//	void test_actualiza_cliente_error_1() {
//		//given
//		given(clienteRepository.findById(anyString())).willReturn(Optional.empty());
//		
//		//when
//		assertThrows(HTTP404Exception.class, () -> {
//			estadoCuentaService.updateCliente(dto);
//		});
//		
//		//then
//		verify(clienteRepository, never()).save(any(EstadoCuenta.class));
//	}

//	@Test
//	@DisplayName("Test para listar todos los clientes existentes de forma ascendente.")
//	void test_listar_clientes_ascendente() {
//		//given
//		paging = new PagingDTO();
//		paging.setPage(0);
//		paging.setSize(2);
//		paging.setOrder(Direction.ASC);
//		paging.setProperty("prestamoIdCliente");
//		
//		EstadoCuenta clienteNumberOne = EstadoCuenta.builder()
//				.prestamoIdCliente("000002")
//				.nombre("Sonia")
//				.apellidoPaterno("Sosa")
//				.apellidoMaterno("Romero")
//				.rfc("SORS790926")
//				.curp("SORS790926MDF")
//				.fechaRegistro(new Date())
//				.fechaActualizacion(null)
//				.usuarioRegistra(USER_ALTA)
//				.usuarioActualiza(null)
//				.activo(true)
//				.build();
//		
//		EstadoCuenta clienteNumberTwo = EstadoCuenta.builder()
//				.prestamoIdCliente("000003")
//				.nombre("Maria")
//				.apellidoPaterno("Sosa")
//				.apellidoMaterno("Romero")
//				.rfc("SORG931026")
//				.curp("SORG931026MDF")
//				.fechaRegistro(new Date())
//				.fechaActualizacion(null)
//				.usuarioRegistra(USER_ALTA)
//				.usuarioActualiza(null)
//				.activo(true)
//				.build();
//		
//		PageImpl<EstadoCuenta> pagina = new PageImpl<EstadoCuenta>(
//				List.of(clienteNumberOne, clienteNumberTwo),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()), 3);
//		
//		given(clienteRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()))).willReturn(pagina);
//		
//		//when
//		Page<EstadoCuentaDTO> clientes = estadoCuentaService.findAllEstadoCuenta(paging);
//		
//		//then
//		assertThat(clientes).isNotNull();
//		assertThat(clientes.getNumberOfElements()).isEqualTo(2);
//		assertThat(clientes.getTotalPages()).isEqualTo(2);
//		assertThat(clientes.getNumber()).isZero();
//		assertThat(clientes.getTotalElements()).isEqualTo(3);
//		assertThat(clientes.getSort()).isEqualTo(Sort.by("prestamoIdCliente").ascending());
//	}
	
//	@Test
//	@DisplayName("Test para listar todos los clientes existentes de forma descendente.")
//	void test_listar_clientes_descendente() {
//		//given
//		paging = new PagingDTO();
//		paging.setPage(0);
//		paging.setSize(2);
//		paging.setOrder(Direction.DESC);
//		paging.setProperty("prestamoIdCliente");
//		
//		EstadoCuenta clienteNumberOne = EstadoCuenta.builder()
//				.prestamoIdCliente("000002")
//				.nombre("Sonia")
//				.apellidoPaterno("Sosa")
//				.apellidoMaterno("Romero")
//				.rfc("SORS790926")
//				.curp("SORS790926MDF")
//				.fechaRegistro(new Date())
//				.fechaActualizacion(null)
//				.usuarioRegistra(USER_ALTA)
//				.usuarioActualiza(null)
//				.activo(true)
//				.build();
//		
//		EstadoCuenta clienteNumberTwo = EstadoCuenta.builder()
//				.prestamoIdCliente("000003")
//				.nombre("Maria")
//				.apellidoPaterno("Sosa")
//				.apellidoMaterno("Romero")
//				.rfc("SORG931026")
//				.curp("SORG931026MDF")
//				.fechaRegistro(new Date())
//				.fechaActualizacion(null)
//				.usuarioRegistra(USER_ALTA)
//				.usuarioActualiza(null)
//				.activo(true)
//				.build();
//		
//		PageImpl<EstadoCuenta> pagina = new PageImpl<EstadoCuenta>(
//				List.of(clienteNumberTwo, clienteNumberOne),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()), 3);
//		
//		given(clienteRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()))).willReturn(pagina);
//		
//		//when
//		Page<EstadoCuentaDTO> clientes = estadoCuentaService.findAllEstadoCuenta(paging);
//		
//		//then
//		assertThat(clientes).isNotNull();
//		assertThat(clientes.getNumberOfElements()).isEqualTo(2);
//		assertThat(clientes.getTotalPages()).isEqualTo(2);
//		assertThat(clientes.getNumber()).isZero();
//		assertThat(clientes.getTotalElements()).isEqualTo(3);
//		assertThat(clientes.getSort()).isEqualTo(Sort.by("prestamoIdCliente").descending());
//	}
	
//	@Test
//	@DisplayName("Test para listar todos los clientes existentes de forma ascendente con numero de pagina mayor a 50.")
//	void test_listar_clientes_ascendente_large_page() {
//		//given
//		paging = new PagingDTO();
//		paging.setPage(0);
//		paging.setSize(52);
//		paging.setOrder(Direction.ASC);
//		paging.setProperty("prestamoIdCliente");
//		
//		EstadoCuenta clienteNumberOne = EstadoCuenta.builder()
//				.prestamoIdCliente("000002")
//				.nombre("Sonia")
//				.apellidoPaterno("Sosa")
//				.apellidoMaterno("Romero")
//				.rfc("SORS790926")
//				.curp("SORS790926MDF")
//				.fechaRegistro(new Date())
//				.fechaActualizacion(null)
//				.usuarioRegistra(USER_ALTA)
//				.usuarioActualiza(null)
//				.activo(true)
//				.build();
//		
//		EstadoCuenta clienteNumberTwo = EstadoCuenta.builder()
//				.prestamoIdCliente("000003")
//				.nombre("Maria")
//				.apellidoPaterno("Sosa")
//				.apellidoMaterno("Romero")
//				.rfc("SORG931026")
//				.curp("SORG931026MDF")
//				.fechaRegistro(new Date())
//				.fechaActualizacion(null)
//				.usuarioRegistra(USER_ALTA)
//				.usuarioActualiza(null)
//				.activo(true)
//				.build();
//		
//		PageImpl<EstadoCuenta> pagina = new PageImpl<EstadoCuenta>(
//				List.of(clienteNumberOne, clienteNumberTwo),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()), 2);
//		
//		given(clienteRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()))).willReturn(pagina);
//		
//		//when
//		Page<EstadoCuentaDTO> clientes = estadoCuentaService.findAllEstadoCuenta(paging);
//		
//		//then
//		assertThat(clientes).isNotNull();
//		assertThat(clientes.getNumberOfElements()).isEqualTo(2);
//		assertThat(clientes.getTotalPages()).isEqualTo(1);
//		assertThat(clientes.getNumber()).isZero();
//		assertThat(clientes.getTotalElements()).isEqualTo(2);
//		assertThat(clientes.getSort()).isEqualTo(Sort.by("prestamoIdCliente").ascending());
//	}
	
//	@Test
//	@DisplayName("Test para listar todos los clientes existentes, lista vacía - Escenario de error 1.")
//	void test_listar_clientes_error_1() {
//		//given
//		paging = new PagingDTO();
//		paging.setPage(0);
//		paging.setSize(2);
//		paging.setOrder(Direction.DESC);
//		paging.setProperty("prestamoIdCliente");
//		
//		PageImpl<EstadoCuenta> pagina = new PageImpl<EstadoCuenta>(
//				List.of(),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()), 0);
//		
//		given(clienteRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()))).willReturn(pagina);
//		
//		//when
//		Page<EstadoCuentaDTO> clientes = estadoCuentaService.findAllEstadoCuenta(paging);
//		
//		//then
//		assertThat(clientes).isNotNull();
//		assertThat(clientes).isEmpty();
//		assertThat(clientes.getNumberOfElements()).isZero();
//		assertThat(clientes.getTotalPages()).isZero();
//		assertThat(clientes.getNumber()).isZero();
//		assertThat(clientes.getTotalElements()).isZero();
//		assertThat(clientes.getSort()).isEqualTo(Sort.by("prestamoIdCliente").descending());
//	}

}