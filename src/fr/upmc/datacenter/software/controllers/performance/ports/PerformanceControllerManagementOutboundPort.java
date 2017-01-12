package fr.upmc.datacenter.software.controllers.performance.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.data.interfaces.PerformanceControllerPortsDataI;
import fr.upmc.datacenter.software.controllers.performance.interfaces.PerformanceControllerManagementI;

public class PerformanceControllerManagementOutboundPort 
extends AbstractOutboundPort
implements PerformanceControllerManagementI
{
	
	public PerformanceControllerManagementOutboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public PerformanceControllerManagementOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void connectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		( (PerformanceControllerManagementI) this.connector).connectLogicalResourcesProvider(lrppdi);
	}

	@Override
	public void disconnectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		( (PerformanceControllerManagementI) this.connector).disconnectLogicalResourcesProvider(lrppdi);
	}

	@Override
	public void connectPerformanceController(PerformanceControllerPortsDataI cpdi) throws Exception {
		( (PerformanceControllerManagementI) this.connector).connectPerformanceController(cpdi);
	}

	@Override
	public void disconnectPerformanceController(PerformanceControllerPortsDataI cpdi) throws Exception {
		( (PerformanceControllerManagementI) this.connector).disconnectPerformanceController(cpdi);		
	}
}
