package fr.upmc.datacenter.providers.resources.logical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderCoreReleasingNotifyBackHandlerI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderCoreReleasingNotifyBackI;

public class LogicalResourcesProviderCoreReleasingNotifyBackInboundPort
	extends
		AbstractInboundPort
	implements
		LogicalResourcesProviderCoreReleasingNotifyBackI
{
	private static final long serialVersionUID = 1795308874307716851L;

	public LogicalResourcesProviderCoreReleasingNotifyBackInboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public LogicalResourcesProviderCoreReleasingNotifyBackInboundPort(String uri, Class<?> implementedInterface,
			ComponentI owner) throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void notifyBackCoreReleasing(String requesterUri, String answererUri, AllocatedApplicationVM aavm, AllocatedCore ac) throws Exception {
		final LogicalResourcesProviderCoreReleasingNotifyBackHandlerI lrpcrnbhi = (LogicalResourcesProviderCoreReleasingNotifyBackHandlerI) this.owner ;
		
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						lrpcrnbhi.acceptBackCoreReleasing(requesterUri, answererUri, aavm, ac);
						return null;
					}
				}) ;
	}

}
