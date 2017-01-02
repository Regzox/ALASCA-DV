package fr.upmc.datacenter.software.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.interfaces.ApplicationVMReleasingNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.ApplicationVMReleasingNotificationI;

public class ApplicationVMReleasingNotificationInboundPort
	extends 
		AbstractInboundPort
	implements
		ApplicationVMReleasingNotificationI
{
	private static final long serialVersionUID = 7835919177302002006L;

	public ApplicationVMReleasingNotificationInboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public ApplicationVMReleasingNotificationInboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void notifyApplicationVMReleasing(String dispatcherURI, String rsopURI, String rnipURI) throws Exception {
		final ApplicationVMReleasingNotificationHandlerI avmrnh =	(ApplicationVMReleasingNotificationHandlerI) this.owner ;
		
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						avmrnh.acceptApplicationVMReleasing(dispatcherURI, rsopURI, rnipURI);
						return null;
					}
				}) ;
	}
	
}
