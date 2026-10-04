package com.sosa.repository;

import static com.sosa.util.Constants.USER_ALTA;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.sosa.model.CatalogoMovimiento;
import com.sosa.model.CatalogoOperacion;
import com.sosa.model.Cliente;
import com.sosa.model.EstadoCuenta;
import com.sosa.model.Movimiento;
import com.sosa.model.Prestamo;
import com.sosa.model.TasaInteres;
import com.sosa.model.dto.DateRangeDTO;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
class EstadoCuentaRepositoryTests {
	
	@Autowired
	ClienteRepository clienteRepository;
	
	@Autowired
	PrestamoRepository prestamoRepository;
	
	@Autowired
	CatalogoMovimientoRepository catalogoMovimientoRepository;
	
	@Autowired
	CatalogoOperacionRepository catalogoOperacionRepository;
	
	@Autowired
	MovimientoRepository movimientoRepository;
	
	@Autowired
	TasaRepository tasasRepository;
	
	@Autowired
	public EstadoCuentaRepository estadoCuentaRepository;
	
	private Cliente clienteCero;
	private Prestamo prestamoCero;
	private CatalogoOperacion catOperacionCero;
	private CatalogoMovimiento catMovimientoCero;
	private Movimiento movimientoCero;
	private TasaInteres tasaInteresDiciembre;
	private EstadoCuenta estadoCuentaDiciembre;

	@BeforeEach
	void setUp() throws Exception {
		clienteCero = Cliente.builder()
				.idCliente("0000000001")
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
		clienteRepository.save(clienteCero);
		
		prestamoCero = Prestamo.builder()
				.idPrestamo("SL-001-001")
				.monto(120000D)
				.saldoInsoluto(100000D)
				.cliente(clienteCero)
				.diaCorte(3)
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		prestamoRepository.save(prestamoCero);
		
		catMovimientoCero = CatalogoMovimiento.builder()
				.descripcion("Cargo")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		catalogoMovimientoRepository.save(catMovimientoCero);
		
		catOperacionCero = CatalogoOperacion.builder()
				.descripcion("Comision")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		catalogoOperacionRepository.save(catOperacionCero);
		
		movimientoCero = Movimiento.builder()
				.monto(3000D)
				.catalogoOperacion(catOperacionCero)
				.catalogoMovimiento(catMovimientoCero)
				.prestamo(prestamoCero)
				.fechaMovimiento(LocalDate.of(2022, 11, 12))
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		movimientoRepository.save(movimientoCero);
		
		tasaInteresDiciembre = TasaInteres.builder()
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
		tasasRepository.save(tasaInteresDiciembre);
		
		estadoCuentaDiciembre = EstadoCuenta.builder()
				.fechaInicioPeriodo(LocalDate.of(2022, 11, 04))
				.fechaFinPeriodo(LocalDate.of(2022, 12, 03))
				.fechaLimitePago(LocalDate.of(2023, 01, 03))
				.fechaCorte(LocalDate.of(2022, 12, 03))
				.saldoInsolutoInicial(125760.79)
				.saldoPromedioMes(127871.75)
				.interesesOrdinarios(1276.13)
				.interesesMoratorios(0D)
				.comisiones(0D)
				.iva(75.32)
				.disposiciones(12025.74)
				.pagos(10378.04)
				.prestamo(prestamoCero)
				.tasaInteres(tasaInteresDiciembre)
				.creditoDisponible(431240.06)
				.numeroDiasPeriodo(30)
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
	}
	
	@Test
	@DisplayName("Test para guardar un estado de cuenta.")
	void test_guardar_estado_cuenta() {
		// given
		// estadoCuentaDiciembre

		// when
		EstadoCuenta estadoCuentaGuardado = estadoCuentaRepository.save(estadoCuentaDiciembre);
		
		// then
		assertThat(estadoCuentaGuardado).isNotNull();
		assertThat(estadoCuentaGuardado.getIdEstadoCuenta()).isPositive();
	}
	
	@Test
	@DisplayName("Test para actualizar un estado de cuenta.")
	void test_actualizar_estado_cuenta() {
		Date fechaActualizacion = new Date();
		//given
		estadoCuentaRepository.save(estadoCuentaDiciembre);
		
		//when
		EstadoCuenta estadoCuentaGuardado = estadoCuentaRepository.findById(estadoCuentaDiciembre.getIdEstadoCuenta()).orElse(null);
		estadoCuentaGuardado.setComisiones(3200D);
		estadoCuentaGuardado.setFechaActualizacion(fechaActualizacion);
		estadoCuentaGuardado.setUsuarioActualiza("impersonator");
		EstadoCuenta estadoCuentaActualizado = estadoCuentaRepository.save(estadoCuentaGuardado);
		
		//then
		assertThat(estadoCuentaActualizado.getComisiones()).isNotNull();
		assertThat(estadoCuentaActualizado.getComisiones()).isEqualTo(3200);
		assertThat(estadoCuentaActualizado.getFechaActualizacion()).isEqualTo(fechaActualizacion);
		assertThat(estadoCuentaActualizado.getUsuarioActualiza()).isEqualTo("impersonator");
	}
	
	@Test
	@DisplayName("Test para eliminar un estado de cuenta.")
	void test_eliminar_estado_cuenta() {
		Date fechaActualizacion = new Date();
		//given
		estadoCuentaRepository.save(estadoCuentaDiciembre);
		
		//when
		EstadoCuenta estadoCuentaGuardado = estadoCuentaRepository.findById(estadoCuentaDiciembre.getIdEstadoCuenta()).orElse(null);
		estadoCuentaGuardado.setActivo(false);
		estadoCuentaGuardado.setFechaActualizacion(fechaActualizacion);
		estadoCuentaGuardado.setUsuarioActualiza("impersonator");
		EstadoCuenta estadoCuentaActualizado = estadoCuentaRepository.save(estadoCuentaGuardado);
		
		//then
		assertThat(estadoCuentaActualizado).isNotNull();
		assertThat(estadoCuentaActualizado.getActivo()).isFalse();
		assertThat(estadoCuentaActualizado.getFechaActualizacion()).isEqualTo(fechaActualizacion);
		assertThat(estadoCuentaActualizado.getUsuarioActualiza()).isEqualTo("impersonator");
	}
	
	@Test
	@DisplayName("Test para listar los estadoCuentas.")
	void test_listar_estadoCuentas() {
		//given
		TasaInteres tasaInteresEnero = TasaInteres.builder()
				.mes((byte)6)
				.anio((short)2022)
				.catSinIva(13.2f)
				.anualOrdinaria(12.49f)
				.anualMoratoria(0)
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		tasasRepository.save(tasaInteresEnero);
		
		EstadoCuenta estadoCuentaEnero = EstadoCuenta.builder()
				.fechaInicioPeriodo(LocalDate.of(2022, 11, 04))
				.fechaFinPeriodo(LocalDate.of(2022, 12, 03))
				.fechaLimitePago(LocalDate.of(2023, 01, 03))
				.fechaCorte(LocalDate.of(2022, 12, 03))
				.saldoInsolutoInicial(125760.79)
				.saldoPromedioMes(127871.75)
				.interesesOrdinarios(1276.13)
				.interesesMoratorios(0D)
				.comisiones(0D)
				.iva(75.32)
				.disposiciones(12025.74)
				.pagos(10378.04)
				.prestamo(prestamoCero)
				.tasaInteres(tasaInteresEnero)
				.creditoDisponible(431240.06)
				.numeroDiasPeriodo(30)
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		TasaInteres tasaInteresFebrero = TasaInteres.builder()
				.mes((byte)6)
				.anio((short)2022)
				.catSinIva(13.5f)
				.anualOrdinaria(12.77f)
				.anualMoratoria(0)
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		tasasRepository.save(tasaInteresFebrero);
		
		EstadoCuenta estadoCuentaFebrero = EstadoCuenta.builder()
				.fechaInicioPeriodo(LocalDate.of(2022, 11, 04))
				.fechaFinPeriodo(LocalDate.of(2022, 12, 03))
				.fechaLimitePago(LocalDate.of(2023, 01, 03))
				.fechaCorte(LocalDate.of(2022, 12, 03))
				.saldoInsolutoInicial(125760.79)
				.saldoPromedioMes(127871.75)
				.interesesOrdinarios(1276.13)
				.interesesMoratorios(0D)
				.comisiones(0D)
				.iva(75.32)
				.disposiciones(12025.74)
				.pagos(10378.04)
				.prestamo(prestamoCero)
				.tasaInteres(tasaInteresFebrero)
				.creditoDisponible(431240.06)
				.numeroDiasPeriodo(30)
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra(USER_ALTA)
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		estadoCuentaRepository.save(estadoCuentaDiciembre);
		estadoCuentaRepository.save(estadoCuentaEnero);
		estadoCuentaRepository.save(estadoCuentaFebrero);
		
		//when
		Page<EstadoCuenta> listaEstadosCuenta = estadoCuentaRepository.findAll(PageRequest.of(0, 2, Sort.by("idEstadoCuenta").ascending()));
		
		//then
		assertThat(listaEstadosCuenta).isNotNull();
		assertThat(listaEstadosCuenta.getSize()).isEqualTo(2);
		assertThat(listaEstadosCuenta.getNumber()).isZero();
		assertThat(listaEstadosCuenta.getNumberOfElements()).isEqualTo(2);
		assertThat(listaEstadosCuenta.getSort().toString()).hasToString("idEstadoCuenta: ASC");
		assertThat(listaEstadosCuenta.getTotalElements()).isEqualTo(3);
		assertThat(listaEstadosCuenta.getTotalPages()).isEqualTo(2);
		assertThat(listaEstadosCuenta.getContent().get(0).getIdEstadoCuenta()).isPositive();
	}
	
	@Test
	@DisplayName("Test para obtener un estado de cuenta.")
	void test_obtener_estado_cuenta_por_id() {
		//given
		estadoCuentaRepository.save(estadoCuentaDiciembre);
		
		//when
		EstadoCuenta estadoCuentaGuardado = estadoCuentaRepository.findById(estadoCuentaDiciembre.getIdEstadoCuenta()).orElse(null);
		
		//then
		assertThat(estadoCuentaGuardado).isNotNull();
		assertThat(estadoCuentaGuardado.getIdEstadoCuenta()).isPositive();
		assertThat(estadoCuentaGuardado.getActivo()).isTrue();
	}
	
	@Test
	@DisplayName("Test para obtener un movimiento por prestamo y fecha.")
	void test_obtener_movimiento_por_prestamo_fecha() {
		//given
		DateRangeDTO range = new DateRangeDTO();
		range.setEndDate(LocalDate.of(2022, 12, 03));
		range.setStartDate(LocalDate.of(2022, 11, 04));
		estadoCuentaRepository.save(estadoCuentaDiciembre);
		
		//when
		List<Movimiento> listaMovimientos = movimientoRepository.findMovimientosByDateAndLoan(Optional.of(prestamoCero), range);
		
		//then
		assertThat(listaMovimientos).isNotNull();
		assertThat(listaMovimientos.get(0)).isNotNull();
		assertThat(listaMovimientos.get(0).getIdMovimiento()).isPositive();
	}
}
