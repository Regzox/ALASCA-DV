package fr.upmc.datacenter.providers.resources.physical.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderServicesI;

public class PhysicalResourcesProviderServicesConnector 
extends		AbstractConnector
implements	PhysicalResourcesProviderServicesI
{

	@Override
	public void increaseCoreFrenquency(AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderServicesI) this.offering ).increaseCoreFrenquency(ac);
	}

	@Override
	public void decreaseCoreFrenquency(AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderServicesI) this.offering ).decreaseCoreFrenquency(ac);
	}

	@Override
	public void increaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderServicesI) this.offering ).increaseProcessorFrenquency(ac);
	}

	@Override
	public void decreaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderServicesI) this.offering ).decreaseProcessorFrenquency(ac);
	}

	@Override
	public void increaseComputerFrenquency(AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderServicesI) this.offering ).increaseComputerFrenquency(ac);
	}

	@Override
	public void decreaseComputerFrenquency(AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderServicesI) this.offering ).decreaseComputerFrenquency(ac);
	}

	@Override
	public AllocatedCore[] allocateCores(Integer cores) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.offering ).allocateCores(cores);
	}
	
	@Override
	public AllocatedCore[] allocateCores(AllocatedCore[] acs, Integer cores) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.offering ).allocateCores(acs, cores);
	}

	@Override
	public void releaseCores(AllocatedCore[] allocatedCores) throws Exception {
		( (PhysicalResourcesProviderServicesI) this.offering ).releaseCores(allocatedCores);
	}

	
	
}
