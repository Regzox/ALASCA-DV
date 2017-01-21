package fr.upmc.datacenter.providers.resources.logical.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.data.interfaces.PerformanceControllerPortsDataI;
import fr.upmc.datacenter.data.interfaces.PhysicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderManagementI;

public class LogicalResourcesProviderManagementConnector 
extends AbstractConnector
implements LogicalResourcesProviderManagementI
{

	@Override
	public void connectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppdi) throws Exception {
		( (LogicalResourcesProviderManagementI) this.offering).connectPhysicalResourcesProvider(prppdi);
	}

	@Override
	public void disconnectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppdi) throws Exception {
		( (LogicalResourcesProviderManagementI) this.offering).disconnectPhysicalResourcesProvider(prppdi);
	}

	@Override
	public void connectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		( (LogicalResourcesProviderManagementI) this.offering).connectLogicalResourcesProvider(lrppdi);
	}

	@Override
	public void disconnectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		( (LogicalResourcesProviderManagementI) this.offering).disconnectLogicalResourcesProvider(lrppdi);
	}

	@Override
	public void connectPerformanceController(PerformanceControllerPortsDataI pcpdi) throws Exception {
		( (LogicalResourcesProviderManagementI) this.offering).connectPerformanceController(pcpdi);
		
	}

	@Override
	public void disconnectPerformanceController(PerformanceControllerPortsDataI pcpdi) throws Exception {
		( (LogicalResourcesProviderManagementI) this.offering).disconnectPerformanceController(pcpdi);
	}

	@Override
	public void connectLogicalResourcesProviderNotifyBack(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		( (LogicalResourcesProviderManagementI) this.offering).connectLogicalResourcesProviderNotifyBack(lrppdi);		
	}

	@Override
	public void disconnectLogicalResourcesProviderNotifyBack(LogicalResourcesProviderPortsDataI lrppdi)
			throws Exception {
		( (LogicalResourcesProviderManagementI) this.offering).disconnectLogicalResourcesProviderNotifyBack(lrppdi);		
	}


}
