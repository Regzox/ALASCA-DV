package fr.upmc.datacenter.providers.resources.logical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderCoreReleasingNotificationHandlerI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderCoreReleasingNotificationI;

public class LogicalResourcesProviderCoreReleasingNotificationInboundPort 
	extends 
		AbstractInboundPort
	implements
		LogicalResourcesProviderCoreReleasingNotificationI
{

	private static final long serialVersionUID = -6318481708455270855L;

	public LogicalResourcesProviderCoreReleasingNotificationInboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public LogicalResourcesProviderCoreReleasingNotificationInboundPort(String uri, Class<?> implementedInterface,
			ComponentI owner) throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void notifyCoreReleasing(AllocatedApplicationVM aavm) throws Exception {
		final LogicalResourcesProviderCoreReleasingNotificationHandlerI lrpcrh = (LogicalResourcesProviderCoreReleasingNotificationHandlerI) this.owner ;
		
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						lrpcrh.acceptCoreReleasingNotification(aavm);
						return null;
					}
				}) ;
	}

}
