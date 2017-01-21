package fr.upmc.datacenter.providers.resources.logical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.data.interfaces.PerformanceControllerPortsDataI;
import fr.upmc.datacenter.data.interfaces.PhysicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderManagementI;

public class LogicalResourcesProviderManagementOutboundPort 
extends AbstractOutboundPort
implements LogicalResourcesProviderManagementI
{

	public LogicalResourcesProviderManagementOutboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public LogicalResourcesProviderManagementOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void connectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppdi) throws Exception {
		( (LogicalResourcesProviderManagementI) this.connector).connectPhysicalResourcesProvider(prppdi);
	}

	@Override
	public void disconnectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppdi) throws Exception {
		( (LogicalResourcesProviderManagementI) this.connector).disconnectPhysicalResourcesProvider(prppdi);
	}

	@Override
	public void connectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		( (LogicalResourcesProviderManagementI) this.connector).connectLogicalResourcesProvider(lrppdi);
	}

	@Override
	public void disconnectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		( (LogicalResourcesProviderManagementI) this.connector).disconnectLogicalResourcesProvider(lrppdi);
	}
	
	@Override
	public void connectPerformanceController(PerformanceControllerPortsDataI pcpdi) throws Exception {
		( (LogicalResourcesProviderManagementI) this.connector).connectPerformanceController(pcpdi);
		
	}

	@Override
	public void disconnectPerformanceController(PerformanceControllerPortsDataI pcpdi) throws Exception {
		( (LogicalResourcesProviderManagementI) this.connector).disconnectPerformanceController(pcpdi);
	}
	
	@Override
	public void connectLogicalResourcesProviderNotifyBack(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		( (LogicalResourcesProviderManagementI) this.connector).connectLogicalResourcesProviderNotifyBack(lrppdi);		
	}

	@Override
	public void disconnectLogicalResourcesProviderNotifyBack(LogicalResourcesProviderPortsDataI lrppdi)
			throws Exception {
		( (LogicalResourcesProviderManagementI) this.connector).disconnectLogicalResourcesProviderNotifyBack(lrppdi);		
	}
}
