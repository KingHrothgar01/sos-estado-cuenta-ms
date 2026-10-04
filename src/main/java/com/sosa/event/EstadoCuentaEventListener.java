package com.sosa.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EstadoCuentaEventListener implements ApplicationListener<EstadoCuentaServiceEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(EstadoCuentaEventListener.class);

	public void onApplicationEvent(EstadoCuentaServiceEvent event) {
		EstadoCuentaServiceEvent personEvent = event;
		LOGGER.info("Estado de Cuenta {} with details : {}", event.getEventType(), personEvent.getEventEstadoCuenta());
	}
}