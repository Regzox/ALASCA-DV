package fr.upmc.datacenter.hardware.computer.extended.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.computer.extended.interfaces.ComputerCoreReleasingI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public class ComputerCoreReleasingOutboundPort
	extends
		AbstractOutboundPort
	implements
		ComputerCoreReleasingI
{

	public ComputerCoreReleasingOutboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public ComputerCoreReleasingOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void releaseCore(AllocatedCore allocatedCore) throws Exception {
		((ComputerCoreReleasingI) this.connector).releaseCore(allocatedCore);
	}

	@Override
	public void releaseCores(AllocatedCore[] allocatedCores) throws Exception {
		((ComputerCoreReleasingI) this.connector).releaseCores(allocatedCores);
	}
	
	
}
