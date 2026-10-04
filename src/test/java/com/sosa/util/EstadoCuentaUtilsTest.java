package com.sosa.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sosa.exception.HTTP400Exception;
import com.sosa.model.dto.DateRangeDTO;

class EstadoCuentaUtilsTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void get_range_before_now() {
		// 03/03/2022
		Optional<DateRangeDTO> range = EstadoCuentaUtils.getPeriodo((byte)3, (byte)3, (short)2022);
		DateRangeDTO dto = range.get();
		
		assertNotNull(dto);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Date.from(dto.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		
		assertEquals(2022, calendar.get(Calendar.YEAR));
		assertEquals(1, calendar.get(Calendar.MONTH));
		assertEquals(4, calendar.get(Calendar.DAY_OF_MONTH));
		
		calendar.setTime(Date.from(dto.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
        assertEquals(2022, calendar.get(Calendar.YEAR));
		assertEquals(2, calendar.get(Calendar.MONTH));
		assertEquals(3, calendar.get(Calendar.DAY_OF_MONTH));
	}
	
	@Test
	void get_range_equalsyear() {
		// 03/06/2023
		Optional<DateRangeDTO> range = EstadoCuentaUtils.getPeriodo((byte)3, (byte)6, (short)2023);
		DateRangeDTO dto = range.get();
		
		assertNotNull(dto);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Date.from(dto.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		
		assertEquals(2023, calendar.get(Calendar.YEAR));
		assertEquals(4, calendar.get(Calendar.MONTH));
		assertEquals(4, calendar.get(Calendar.DAY_OF_MONTH));
		
        calendar.setTime(Date.from(dto.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
        assertEquals(2023, calendar.get(Calendar.YEAR));
		assertEquals(5, calendar.get(Calendar.MONTH));
		assertEquals(3, calendar.get(Calendar.DAY_OF_MONTH));
	}
	
	@Test
	void get_range_equalsyear_equalsmonth() {
		// 03/07/2023
		Optional<DateRangeDTO> range = EstadoCuentaUtils.getPeriodo((byte)3, (byte)7, (short)2023);
		DateRangeDTO dto = range.get();
		
		assertNotNull(dto);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Date.from(dto.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		
		assertEquals(2023, calendar.get(Calendar.YEAR));
		assertEquals(5, calendar.get(Calendar.MONTH));
		assertEquals(4, calendar.get(Calendar.DAY_OF_MONTH));
		
		calendar.setTime(Date.from(dto.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
        assertEquals(2023, calendar.get(Calendar.YEAR));
		assertEquals(6, calendar.get(Calendar.MONTH));
		assertEquals(3, calendar.get(Calendar.DAY_OF_MONTH));
	}
	
	@Test
	void get_range_equalsyear_equalsmonth_equalsday() {
		//given
		// 09/07/2023
		//Date is in the future 
		
		//when
		Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			EstadoCuentaUtils.getPeriodo((byte)9, (byte)7, (short)2023);
		});
		
		//then
		assertEquals("Error en petición de cliente: Day", thrown.getMessage());
	}
	
	@Test
	@DisplayName("Test para obtener periodo futuro.")
	void get_range_year_future_exception() {
		//given
		// 03/06/2025
		//Date is in the future 
		
		//when
		Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			EstadoCuentaUtils.getPeriodo((byte)3, (byte)6, (short)2025);
		});
		
		//then
		assertEquals("Error en petición de cliente: Year", thrown.getMessage());
	}

}
