package fr.upmc.datacenter.software.applicationvm.extended.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.applicationvm.extended.interfaces.ApplicationVMCoreReleasingI;

public class ApplicationVMCoreReleasingOutboundPort
	extends 
		AbstractOutboundPort
	implements 
		ApplicationVMCoreReleasingI
{
	
	public ApplicationVMCoreReleasingOutboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public ApplicationVMCoreReleasingOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void releaseCore() throws Exception {
		((ApplicationVMCoreReleasingI) this.connector).releaseCore();
	}

	@Override
	public void releaseCores(int cores) throws Exception {
		((ApplicationVMCoreReleasingI) this.connector).releaseCores(cores);
	}

	@Override
	public void releaseMaximumCores() throws Exception {
		((ApplicationVMCoreReleasingI) this.connector).releaseMaximumCores();
	}

}
