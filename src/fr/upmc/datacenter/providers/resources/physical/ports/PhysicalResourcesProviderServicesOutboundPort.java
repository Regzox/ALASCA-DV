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
	public void increaseCoreFrenquency(AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderServicesI) this.connector ).increaseCoreFrenquency(ac);
	}

	@Override
	public void decreaseCoreFrenquency(AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderServicesI) this.connector ).decreaseCoreFrenquency(ac);
	}

	@Override
	public void increaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderServicesI) this.connector ).increaseProcessorFrenquency(ac);
	}

	@Override
	public void decreaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderServicesI) this.connector ).decreaseProcessorFrenquency(ac);
	}

	@Override
	public void increaseComputerFrenquency(AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderServicesI) this.connector ).increaseComputerFrenquency(ac);
	}

	@Override
	public void decreaseComputerFrenquency(AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderServicesI) this.connector ).decreaseComputerFrenquency(ac);
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
	public void releaseCores(AllocatedCore[] allocatedCores) throws Exception {
		( (PhysicalResourcesProviderServicesI) this.connector ).releaseCores(allocatedCores);
	}

}
