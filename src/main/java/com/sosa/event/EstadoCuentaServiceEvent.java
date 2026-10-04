package com.sosa.event;

import org.springframework.context.ApplicationEvent;

import com.sosa.model.dto.EstadoCuentaDTO;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class EstadoCuentaServiceEvent extends ApplicationEvent {

	private static final long serialVersionUID = -6121624308782503383L;
	
	private EstadoCuentaDTO eventEstadoCuenta;
	private String eventType;

	public EstadoCuentaServiceEvent(Object source, String eventType, EstadoCuentaDTO estadoCuenta) {
		super(source);
		this.eventType = eventType;
		this.eventEstadoCuenta = estadoCuenta;
	}

	@Override
	public String toString() {
		return String.format("EstadoCuentaServiceEvent [evento = {}, eventType = {}]", eventEstadoCuenta, eventType);
	}
}
