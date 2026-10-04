package com.sosa.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.sosa.model.TasaInteres;

@Repository
public interface TasaRepository extends PagingAndSortingRepository<TasaInteres, Long>{

	TasaInteres findByMesAndAnio(byte month, short year);

}
