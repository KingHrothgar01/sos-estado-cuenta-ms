package com.sosa.util;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Optional;

import com.sosa.exception.HTTP400Exception;
import com.sosa.model.dto.DateRangeDTO;

public abstract class EstadoCuentaUtils {

	public static final Optional<DateRangeDTO> getPeriodo(byte day, byte mes, short year) {
		DateRangeDTO range = null;
		if (year < LocalDate.now().getYear())
			if (mes >= Month.JANUARY.getValue() && mes <= Month.DECEMBER.getValue())
				if (day >= 1 && day <= 31)
					range = getRange(day, mes, year);
				else
					throw new HTTP400Exception("Error en petición de cliente: Day");
			else
				throw new HTTP400Exception("Error en petición de cliente: Month");
		else if (year == LocalDate.now().getYear()) {
			if (mes < LocalDate.now().getMonthValue())
				if (day >= 1 && day <= 31)
					range = getRange(day, mes, year);
				else
					throw new HTTP400Exception("Error en petición de cliente: Day");
			else if (mes == LocalDate.now().getMonthValue())
				if (day >= 1 && day < LocalDate.now().getDayOfMonth())
					range = getRange(day, mes, year);
				else
					throw new HTTP400Exception("Error en petición de cliente: Day");
			else
				throw new HTTP400Exception("Error en petición de cliente: Mes");
		}else {
			throw new HTTP400Exception("Error en petición de cliente: Year");
		}
		return Optional.ofNullable(range);
	}
	
	private static DateRangeDTO getRange(byte day, byte mes, short year) {
		DateRangeDTO range = new DateRangeDTO();
		Month month = Month.of(mes);
		
		Month lastMonth = Month.of(mes).minus(1);
		YearMonth lastYearMonth = YearMonth.of(year, lastMonth);
		
		int daysPeriod = lastYearMonth.lengthOfMonth();
		LocalDate cutoffDate = LocalDate.of(year, month, day);

		range.setEndDate(cutoffDate);
		range.setStartDate(cutoffDate.minusDays(daysPeriod - 1));
		
		return range;
	}
}
