package fr.upmc.datacenter.software.applicationvm.extended.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.applicationvm.extended.interfaces.ApplicationVMCoreReleasingI;

public class ApplicationVMCoreReleasingConnector 
	extends 
		AbstractConnector
	implements 
		ApplicationVMCoreReleasingI
{

	public ApplicationVMCoreReleasingConnector() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void releaseCore() throws Exception {
		((ApplicationVMCoreReleasingI) this.offering).releaseCore();
	}

	@Override
	public void releaseCores(int cores) throws Exception {
		((ApplicationVMCoreReleasingI) this.offering).releaseCores(cores);
	}

	@Override
	public void releaseMaximumCores() throws Exception {
		((ApplicationVMCoreReleasingI) this.offering).releaseMaximumCores();
	}
	
}
