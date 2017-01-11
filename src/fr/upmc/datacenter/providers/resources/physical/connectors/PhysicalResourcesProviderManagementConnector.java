package fr.upmc.datacenter.providers.resources.physical.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.data.interfaces.ComputerPortsDataI;
import fr.upmc.datacenter.data.interfaces.PhysicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderManagementI;

public class PhysicalResourcesProviderManagementConnector
extends		AbstractConnector
implements	PhysicalResourcesProviderManagementI
{

	@Override
	public void connectComputer(ComputerPortsDataI cpd) throws Exception {
		( (PhysicalResourcesProviderManagementI) this.offering ).connectComputer(cpd);
		
	}

	@Override
	public void disconnectComputer(ComputerPortsDataI cpd) throws Exception {
		( (PhysicalResourcesProviderManagementI) this.offering ).disconnectComputer(cpd);
	}

	@Override
	public void connectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppd) throws Exception {
		( (PhysicalResourcesProviderManagementI) this.offering ).connectPhysicalResourcesProvider(prppd);
	}

	@Override
	public void disconnectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppd) throws Exception {
		( (PhysicalResourcesProviderManagementI) this.offering ).disconnectPhysicalResourcesProvider(prppd);
	}

}
