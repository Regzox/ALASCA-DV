package fr.upmc.datacenter.providers.resources.logical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderCoreReleasingNotificationI;

public class LogicalResourcesProviderCoreReleasingNotificationOutboundPort
	extends
		AbstractOutboundPort
	implements
		LogicalResourcesProviderCoreReleasingNotificationI
{

	public LogicalResourcesProviderCoreReleasingNotificationOutboundPort(Class<?> implementedInterface,
			ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public LogicalResourcesProviderCoreReleasingNotificationOutboundPort(String uri, Class<?> implementedInterface,
			ComponentI owner) throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void notifyCoreReleasing(AllocatedApplicationVM aavm) throws Exception {
		( (LogicalResourcesProviderCoreReleasingNotificationI) this.connector ).notifyCoreReleasing(aavm);
	}

}
