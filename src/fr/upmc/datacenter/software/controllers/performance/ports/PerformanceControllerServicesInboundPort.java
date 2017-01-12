package fr.upmc.datacenter.software.controllers.performance.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.controllers.performance.PerformanceController;
import fr.upmc.datacenter.software.controllers.performance.interfaces.PerformanceControllerServicesI;

public class PerformanceControllerServicesInboundPort 
extends AbstractInboundPort
implements PerformanceControllerServicesI
{
	private static final long serialVersionUID = -1516855061028818165L;

	public PerformanceControllerServicesInboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public PerformanceControllerServicesInboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void acceptApplication() throws Exception {
		final PerformanceController pc = (PerformanceController) this.owner;

		pc.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				pc.acceptApplication();
				return null;
			}

		});		
	}
	
}
