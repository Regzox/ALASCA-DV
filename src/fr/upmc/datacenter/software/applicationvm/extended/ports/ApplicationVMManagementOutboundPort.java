package fr.upmc.datacenter.software.applicationvm.extended.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.datacenter.software.applicationvm.extended.interfaces.ApplicationVMManagementI;

public class ApplicationVMManagementOutboundPort
	extends fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort
	implements ApplicationVMManagementI
{

	public ApplicationVMManagementOutboundPort(ComponentI owner) throws Exception {
		super(owner);
	}

	public ApplicationVMManagementOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}

	@Override
	public void releaseCore() throws Exception {
		((ApplicationVMManagementI) this.connector).releaseCore();
	}

	@Override
	public void releaseCores(int cores) throws Exception {
		((ApplicationVMManagementI) this.connector).releaseCores(cores);		
	}

	@Override
	public void releaseMaximumCores() throws Exception {
		((ApplicationVMManagementI) this.connector).releaseMaximumCores();
	}

}
