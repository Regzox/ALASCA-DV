package fr.upmc.datacenter.providers.resources.physical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderRequestingI;

public class PhysicalResourcesProviderRequestingOutboundPort
extends 	AbstractOutboundPort
implements	PhysicalResourcesProviderRequestingI
{
		
	public PhysicalResourcesProviderRequestingOutboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public PhysicalResourcesProviderRequestingOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public boolean isLocal(Object o) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.connector ).isLocal(o);
	}
	
	@Override
	public void increaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderRequestingI) this.connector ).increaseCoreFrenquency(requesterUri, ac);
	}

	@Override
	public void decreaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderRequestingI) this.connector ).decreaseCoreFrenquency(requesterUri, ac);
	}

	@Override
	public void increaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderRequestingI) this.connector ).increaseProcessorFrenquency(requesterUri, ac);
	}

	@Override
	public void decreaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderRequestingI) this.connector ).decreaseProcessorFrenquency(requesterUri, ac);
	}

	@Override
	public void increaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderRequestingI) this.connector ).increaseComputerFrenquency(requesterUri, ac);
	}

	@Override
	public void decreaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		( (PhysicalResourcesProviderRequestingI) this.connector ).decreaseComputerFrenquency(requesterUri, ac);
	}

	@Override
	public AllocatedCore[] allocateCores(String requesterUri, AllocatedCore[] acs, Integer cores) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.connector ).allocateCores(requesterUri, acs, cores);
	}

	@Override
	public void releaseCores(String requesterUri, AllocatedCore[] allocatedCores) throws Exception {
		( (PhysicalResourcesProviderRequestingI) this.connector ).releaseCores(requesterUri, allocatedCores);
	}

}
