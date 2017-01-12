package fr.upmc.datacenter.software.controllers.performance.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.controllers.performance.interfaces.PerformanceControllerServicesI;

public class PerformanceControllerServicesOutboundPort
extends AbstractOutboundPort
implements PerformanceControllerServicesI
{
	
	public PerformanceControllerServicesOutboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public PerformanceControllerServicesOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void acceptApplication() throws Exception {
		( (PerformanceControllerServicesI) this.connector).acceptApplication();
	}
	
}
