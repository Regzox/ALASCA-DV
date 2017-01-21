package fr.upmc.datacenter.providers.resources.physical.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderRequestingI;

public class PhysicalResourcesProviderRequestingConnector
extends 	AbstractConnector
implements	PhysicalResourcesProviderRequestingI
{

	@Override
	public boolean isLocal(Object o) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.offering ).isLocal(o);
	}
	
	@Override
	public Integer increaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.offering ).increaseCoreFrenquency(requesterUri, ac);
	}

	@Override
	public Integer decreaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.offering ).decreaseCoreFrenquency(requesterUri, ac);
	}

	@Override
	public Integer[] increaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.offering ).increaseProcessorFrenquency(requesterUri, ac);
	}

	@Override
	public Integer[] decreaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.offering ).decreaseProcessorFrenquency(requesterUri, ac);
	}

	@Override
	public Integer[][] increaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.offering ).increaseComputerFrenquency(requesterUri, ac);
	}

	@Override
	public Integer[][] decreaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.offering ).decreaseComputerFrenquency(requesterUri, ac);
	}

	@Override
	public AllocatedCore[] allocateCores(String requesterUri, AllocatedCore[] acs, Integer cores) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.offering ).allocateCores(requesterUri, acs, cores);
	}

	@Override
	public AllocatedCore[] releaseCores(String requesterUri, AllocatedCore[] allocatedCores) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.offering ).releaseCores(requesterUri, allocatedCores);
	}

}
