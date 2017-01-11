package fr.upmc.datacenter.providers.resources.physical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.data.interfaces.ComputerPortsDataI;
import fr.upmc.datacenter.data.interfaces.PhysicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderManagementI;

public class PhysicalResourcesProviderManagementOutboundPort
extends		AbstractOutboundPort
implements	PhysicalResourcesProviderManagementI
{

	public PhysicalResourcesProviderManagementOutboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public PhysicalResourcesProviderManagementOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void connectComputer(ComputerPortsDataI cpd) throws Exception {
		( (PhysicalResourcesProviderManagementI) this.connector ).connectComputer(cpd);
	}

	@Override
	public void disconnectComputer(ComputerPortsDataI cpd) throws Exception {
		( (PhysicalResourcesProviderManagementI) this.connector ).disconnectComputer(cpd);
	}

	@Override
	public void connectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppd) throws Exception {
		( (PhysicalResourcesProviderManagementI) this.connector ).connectPhysicalResourcesProvider(prppd);
	}

	@Override
	public void disconnectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppd) throws Exception {
		( (PhysicalResourcesProviderManagementI) this.connector ).disconnectPhysicalResourcesProvider(prppd);
	}

}
