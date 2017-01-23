package fr.upmc.datacenter.software.controllers.performance.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.software.controllers.performance.PerformanceController;
import fr.upmc.datacenter.software.controllers.performance.interfaces.PerformanceControllerManagementI;

public class PerformanceControllerManagementInboundPort
extends AbstractInboundPort
implements PerformanceControllerManagementI
{
	private static final long serialVersionUID = 7340791489633983798L;

	public PerformanceControllerManagementInboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public PerformanceControllerManagementInboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void connectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		final PerformanceController pc = (PerformanceController) this.owner;

		pc.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				pc.connectLogicalResourcesProvider(lrppdi);
				return null;
			}

		});
	}

	@Override
	public void disconnectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		final PerformanceController pc = (PerformanceController) this.owner;

		pc.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				pc.disconnectLogicalResourcesProvider(lrppdi);
				return null;
			}

		});
	}

}
