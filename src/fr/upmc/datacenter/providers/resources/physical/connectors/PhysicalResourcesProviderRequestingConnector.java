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
	public void increaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderRequestingI) this.offering ).increaseCoreFrenquency(requesterUri, ac);
	}

	@Override
	public void decreaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderRequestingI) this.offering ).decreaseCoreFrenquency(requesterUri, ac);
	}

	@Override
	public void increaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderRequestingI) this.offering ).increaseProcessorFrenquency(requesterUri, ac);
	}

	@Override
	public void decreaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderRequestingI) this.offering ).decreaseProcessorFrenquency(requesterUri, ac);
	}

	@Override
	public void increaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderRequestingI) this.offering ).increaseComputerFrenquency(requesterUri, ac);
	}

	@Override
	public void decreaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderRequestingI) this.offering ).decreaseComputerFrenquency(requesterUri, ac);
	}

	@Override
	public AllocatedCore[] allocateCores(String requesterUri, AllocatedCore[] acs, Integer cores) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.offering ).allocateCores(requesterUri, acs, cores);
	}

	@Override
	public void releaseCores(String requesterUri, AllocatedCore[] allocatedCores) throws Exception {
		( (PhysicalResourcesProviderRequestingI) this.offering ).releaseCores(requesterUri, allocatedCores);
	}

}
