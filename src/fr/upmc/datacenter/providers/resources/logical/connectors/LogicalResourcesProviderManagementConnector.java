package fr.upmc.datacenter.providers.resources.logical.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;
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

}
