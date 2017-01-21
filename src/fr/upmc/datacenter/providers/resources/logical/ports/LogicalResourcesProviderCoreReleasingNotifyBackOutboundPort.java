package fr.upmc.datacenter.providers.resources.logical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderCoreReleasingNotifyBackI;

public class LogicalResourcesProviderCoreReleasingNotifyBackOutboundPort 
	extends
		AbstractOutboundPort
	implements
		LogicalResourcesProviderCoreReleasingNotifyBackI
{

	public LogicalResourcesProviderCoreReleasingNotifyBackOutboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public LogicalResourcesProviderCoreReleasingNotifyBackOutboundPort(String uri, Class<?> implementedInterface,
			ComponentI owner) throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void notifyBackCoreReleasing(String requesterUri, String answererUri, AllocatedApplicationVM aavm, AllocatedCore ac) throws Exception {
		((LogicalResourcesProviderCoreReleasingNotifyBackI) this.connector).notifyBackCoreReleasing(requesterUri, answererUri, aavm, ac);
	}
	
}
