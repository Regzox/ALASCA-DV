package fr.upmc.datacenter.software.applicationvm.extended.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.applicationvm.extended.interfaces.CoreReleasingI;

public class CoreReleasingConnector 
	extends 
		AbstractConnector
	implements 
		CoreReleasingI
{

	public CoreReleasingConnector() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void releaseCore() throws Exception {
		((CoreReleasingI) this.offering).releaseCore();
	}

	@Override
	public void releaseCores(int cores) throws Exception {
		((CoreReleasingI) this.offering).releaseCores(cores);
	}

	@Override
	public void releaseMaximumCores() throws Exception {
		((CoreReleasingI) this.offering).releaseMaximumCores();
	}
	
}
