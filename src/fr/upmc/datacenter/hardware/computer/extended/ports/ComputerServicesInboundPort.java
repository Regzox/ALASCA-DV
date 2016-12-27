package fr.upmc.datacenter.hardware.computer.extended.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.datacenter.hardware.computer.extended.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public class ComputerServicesInboundPort 
	extends fr.upmc.datacenter.hardware.computers.ports.ComputerServicesInboundPort
	implements ComputerServicesI
{
	private static final long serialVersionUID = -3624868577803325701L;

	public ComputerServicesInboundPort(ComponentI owner) throws Exception {
		super(owner);
	}

	public ComputerServicesInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}

	@Override
	public void releaseCore(AllocatedCore allocatedCore) throws Exception {
		((Computer)this.owner).releaseCore(allocatedCore);	
	}

	@Override
	public void releaseCores(AllocatedCore[] allocatedCores) throws Exception {
		((Computer)this.owner).releaseCores(allocatedCores);		
	}

}
