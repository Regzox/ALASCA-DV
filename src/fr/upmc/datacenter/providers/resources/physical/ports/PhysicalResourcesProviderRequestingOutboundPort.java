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
	public Integer increaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.connector ).increaseCoreFrenquency(requesterUri, ac);
	}

	@Override
	public Integer decreaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.connector ).decreaseCoreFrenquency(requesterUri, ac);
	}

	@Override
	public Integer[] increaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.connector ).increaseProcessorFrenquency(requesterUri, ac);
	}

	@Override
	public Integer[] decreaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.connector ).decreaseProcessorFrenquency(requesterUri, ac);
	}

	@Override
	public Integer[][] increaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.connector ).increaseComputerFrenquency(requesterUri, ac);
	}

	@Override
	public Integer[][] decreaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.connector ).decreaseComputerFrenquency(requesterUri, ac);
	}

	@Override
	public AllocatedCore[] allocateCores(String requesterUri, AllocatedCore[] acs, Integer cores) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.connector ).allocateCores(requesterUri, acs, cores);
	}

	@Override
	public AllocatedCore[] releaseCores(String requesterUri, AllocatedCore[] allocatedCores) throws Exception {
		return ( (PhysicalResourcesProviderRequestingI) this.connector ).releaseCores(requesterUri, allocatedCores);
	}

}
