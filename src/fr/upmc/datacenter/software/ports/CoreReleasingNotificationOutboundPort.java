package fr.upmc.datacenter.software.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.interfaces.CoreReleasingNotificationI;

public class CoreReleasingNotificationOutboundPort 
	extends
		AbstractOutboundPort
	implements
		CoreReleasingNotificationI
{

	public CoreReleasingNotificationOutboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public CoreReleasingNotificationOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void notifyCoreReleasing(String avmURI) throws Exception {
		((CoreReleasingNotificationI) this.connector).notifyCoreReleasing(avmURI);
	}

}
