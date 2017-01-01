package fr.upmc.datacenter.hardware.computer.extended.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computer.extended.interfaces.ComputerCoreReleasingI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public class ComputerCoreReleasingConnector
	extends 
		AbstractConnector
	implements
		ComputerCoreReleasingI
{

	@Override
	public void releaseCore(AllocatedCore allocatedCore) throws Exception {
		((ComputerCoreReleasingI) this.offering).releaseCore(allocatedCore);
	}

	@Override
	public void releaseCores(AllocatedCore[] allocatedCores) throws Exception {
		((ComputerCoreReleasingI) this.offering).releaseCores(allocatedCores);
	}
	
}
