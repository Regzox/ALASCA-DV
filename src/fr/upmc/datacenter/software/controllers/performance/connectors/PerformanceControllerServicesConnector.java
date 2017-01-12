package fr.upmc.datacenter.software.controllers.performance.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.controllers.performance.interfaces.PerformanceControllerServicesI;

public class PerformanceControllerServicesConnector
extends AbstractConnector
implements PerformanceControllerServicesI
{

	@Override
	public void acceptApplication() throws Exception {
		( (PerformanceControllerServicesI) this.offering).acceptApplication();
	}

}
