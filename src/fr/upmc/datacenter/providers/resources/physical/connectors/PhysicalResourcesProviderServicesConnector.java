package fr.upmc.datacenter.providers.resources.physical.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderServicesI;

public class PhysicalResourcesProviderServicesConnector 
extends		AbstractConnector
implements	PhysicalResourcesProviderServicesI
{

	@Override
	public Integer increaseCoreFrenquency(AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.offering ).increaseCoreFrenquency(ac);
	}

	@Override
	public Integer decreaseCoreFrenquency(AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.offering ).decreaseCoreFrenquency(ac);
	}

	@Override
	public Integer[] increaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.offering ).increaseProcessorFrenquency(ac);
	}

	@Override
	public Integer[] decreaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.offering ).decreaseProcessorFrenquency(ac);
	}

	@Override
	public Integer[][] increaseComputerFrenquency(AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.offering ).increaseComputerFrenquency(ac);
	}

	@Override
	public Integer[][] decreaseComputerFrenquency(AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.offering ).decreaseComputerFrenquency(ac);
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
	public AllocatedCore[] releaseCores(AllocatedCore[] allocatedCores) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.offering ).releaseCores(allocatedCores);
	}

	
	
}
