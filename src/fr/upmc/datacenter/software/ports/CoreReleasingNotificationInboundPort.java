package fr.upmc.datacenter.software.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.interfaces.CoreReleasingNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.CoreReleasingNotificationI;

public class CoreReleasingNotificationInboundPort 
	extends 
		AbstractInboundPort
	implements
		CoreReleasingNotificationI
{

	private static final long serialVersionUID = 3870502592298107851L;

	public CoreReleasingNotificationInboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public CoreReleasingNotificationInboundPort(String uri, Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void notifyCoreReleasing(String avmURI) throws Exception {
		final CoreReleasingNotificationHandlerI crh =	(CoreReleasingNotificationHandlerI) this.owner ;
		
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						crh.acceptCoreReleasing(avmURI);
						return null;
					}
				}) ;
	}

}
