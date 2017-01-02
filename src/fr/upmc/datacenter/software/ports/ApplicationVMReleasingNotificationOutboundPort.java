package fr.upmc.datacenter.software.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.interfaces.ApplicationVMReleasingNotificationI;

public class ApplicationVMReleasingNotificationOutboundPort
	extends 
		AbstractOutboundPort
	implements
		ApplicationVMReleasingNotificationI
{
	
	public ApplicationVMReleasingNotificationOutboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public ApplicationVMReleasingNotificationOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}
	
	@Override
	public void notifyApplicationVMReleasing(String dispatcherURI, String rsopURI, String rnipURI) throws Exception {
		((ApplicationVMReleasingNotificationI) this.connector).notifyApplicationVMReleasing(dispatcherURI, rsopURI, rnipURI);
	}

}
