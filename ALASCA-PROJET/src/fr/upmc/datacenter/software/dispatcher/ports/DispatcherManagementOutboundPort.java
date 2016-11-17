package fr.upmc.datacenter.software.dispatcher.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherManagementI;

public 	class DispatcherManagementOutboundPort 
		extends 
			AbstractOutboundPort
		implements 
			DispatcherManagementI
{

	public DispatcherManagementOutboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public DispatcherManagementOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public String connectToRequestGenerator(String rnipURI) throws Exception {
		return ((DispatcherManagementI) this.connector).connectToRequestGenerator(rnipURI);
	}

	@Override
	public void disconnectFromRequestGenerator() throws Exception {
		((DispatcherManagementI) this.connector).disconnectFromRequestGenerator();
	}

	@Override
	public String connectToApplicationVM(String rsipURI) throws Exception {
		return ((DispatcherManagementI) this.connector).connectToApplicationVM(rsipURI);
	}

	@Override
	public void disconnectFromApplicationVM() throws Exception {
		((DispatcherManagementI) this.connector).disconnectFromApplicationVM();
	}

}
