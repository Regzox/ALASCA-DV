package fr.upmc.datacenter.software.applicationvm.extended.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.applicationvm.extended.interfaces.CoreReleasingI;

public class CoreReleasingOutboundPort
	extends 
		AbstractOutboundPort
	implements 
		CoreReleasingI
{
	
	public CoreReleasingOutboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public CoreReleasingOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void releaseCore() throws Exception {
		((CoreReleasingI) this.connector).releaseCore();
	}

	@Override
	public void releaseCores(int cores) throws Exception {
		((CoreReleasingI) this.connector).releaseCores(cores);
	}

	@Override
	public void releaseMaximumCores() throws Exception {
		((CoreReleasingI) this.connector).releaseMaximumCores();
	}

}
