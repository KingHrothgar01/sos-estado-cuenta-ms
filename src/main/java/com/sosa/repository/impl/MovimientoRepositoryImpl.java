package com.sosa.repository.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.sosa.model.Movimiento;
import com.sosa.model.Prestamo;
import com.sosa.model.dto.DateRangeDTO;
import com.sosa.repository.MovimientoRepositoryCustom;

@Repository
@Transactional
public class MovimientoRepositoryImpl implements MovimientoRepositoryCustom {
	
	@PersistenceContext
    private EntityManager entityManager;

	@Override
	public List<Movimiento> findMovimientosByDateAndLoan(Optional<Prestamo> prestamo, DateRangeDTO range) {
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Movimiento> query = builder.createQuery(Movimiento.class);
		Root<Movimiento> movimiento = query.from(Movimiento.class);
		
		Path<LocalDate> fechaMovimientoPath = movimiento.<LocalDate>get("fechaMovimiento");
		Path<String> prestamoPath = movimiento.<Prestamo>get("prestamo").<String>get("idPrestamo");
		
		List<Predicate> predicates = new ArrayList<>();
		
		predicates.add(builder.equal(prestamoPath, prestamo.get().getIdPrestamo()));
		predicates.add(builder.greaterThanOrEqualTo(fechaMovimientoPath, range.getStartDate()));
		predicates.add(builder.lessThanOrEqualTo(fechaMovimientoPath, range.getEndDate()));

		query.select(movimiento).where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
		
		return entityManager.createQuery(query).getResultList();
	}

}