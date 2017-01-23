package fr.upmc.datacenter.software.controllers.performance.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.software.controllers.performance.interfaces.PerformanceControllerManagementI;

public class PerformanceControllerManagementConnector 
extends AbstractConnector
implements PerformanceControllerManagementI
{

	@Override
	public void connectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		( (PerformanceControllerManagementI) this.offering).connectLogicalResourcesProvider(lrppdi);
	}

	@Override
	public void disconnectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		( (PerformanceControllerManagementI) this.offering).disconnectLogicalResourcesProvider(lrppdi);
	}

}
