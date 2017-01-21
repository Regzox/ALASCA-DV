package fr.upmc.datacenter.providers.resources.physical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderServicesI;

public class PhysicalResourcesProviderServicesOutboundPort 
extends		AbstractOutboundPort
implements	PhysicalResourcesProviderServicesI
{
	public PhysicalResourcesProviderServicesOutboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public PhysicalResourcesProviderServicesOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public Integer increaseCoreFrenquency(AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.connector ).increaseCoreFrenquency(ac);
	}

	@Override
	public Integer decreaseCoreFrenquency(AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.connector ).decreaseCoreFrenquency(ac);
	}

	@Override
	public Integer[] increaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.connector ).increaseProcessorFrenquency(ac);
	}

	@Override
	public Integer[] decreaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.connector ).decreaseProcessorFrenquency(ac);
	}

	@Override
	public Integer[][] increaseComputerFrenquency(AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.connector ).increaseComputerFrenquency(ac);
	}

	@Override
	public Integer[][] decreaseComputerFrenquency(AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.connector ).decreaseComputerFrenquency(ac);
	}

	@Override
	public AllocatedCore[] allocateCores(Integer cores) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.connector ).allocateCores(cores);
	}
	
	@Override
	public AllocatedCore[] allocateCores(AllocatedCore[] acs, Integer cores) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.connector ).allocateCores(acs, cores);
	}

	@Override
	public AllocatedCore[] releaseCores(AllocatedCore[] allocatedCores) throws Exception {
		return ( (PhysicalResourcesProviderServicesI) this.connector ).releaseCores(allocatedCores);
	}

}
