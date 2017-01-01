package fr.upmc.datacenter.software.applicationvm.extended.connectors;

import fr.upmc.datacenter.software.applicationvm.extended.interfaces.ApplicationVMManagementI;

public class ApplicationVMManagementConnector 
	extends fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector
	implements ApplicationVMManagementI
{

	@Override
	public void releaseCore() throws Exception {
		((ApplicationVMManagementI) this.offering).releaseCore();
	}

	@Override
	public void releaseCores(int cores) throws Exception {
		((ApplicationVMManagementI) this.offering).releaseCore();
	}

	@Override
	public void releaseMaximumCores() throws Exception {
		((ApplicationVMManagementI) this.offering).releaseMaximumCores();
	}
	
}
