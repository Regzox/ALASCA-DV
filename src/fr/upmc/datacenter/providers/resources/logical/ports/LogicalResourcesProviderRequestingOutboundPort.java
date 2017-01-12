package fr.upmc.datacenter.providers.resources.logical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderRequestingI;
import fr.upmc.datacenter.software.controllers.performance.AllocatedDispatcher;

public class LogicalResourcesProviderRequestingOutboundPort 
extends AbstractOutboundPort
implements LogicalResourcesProviderRequestingI
{

	public LogicalResourcesProviderRequestingOutboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public LogicalResourcesProviderRequestingOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void increaseApplicationVMFrequency(String requesterUri, AllocatedApplicationVM avm) throws Exception {
		( (LogicalResourcesProviderRequestingI) this.connector ).increaseApplicationVMFrequency(requesterUri, avm);	
	}

	@Override
	public void decreaseApplicationVMFrequency(String requesterUri, AllocatedApplicationVM avm) throws Exception {
		( (LogicalResourcesProviderRequestingI) this.connector ).decreaseApplicationVMFrequency(requesterUri, avm);		
	}

	@Override
	public void increaseApplicationVMCores(String requesterUri, AllocatedApplicationVM avm, Integer coreCount) throws Exception {
		( (LogicalResourcesProviderRequestingI) this.connector ).increaseApplicationVMCores(requesterUri, avm, coreCount);
	}

	@Override
	public void decreaseApplicationVMCores(String requesterUri, AllocatedApplicationVM avm, Integer coreCount) throws Exception {
		( (LogicalResourcesProviderRequestingI) this.connector ).decreaseApplicationVMCores(requesterUri, avm, coreCount);
	}

	@Override
	public AllocatedApplicationVM[] allocateApplicationVMs(String requesterUri, Integer avmCount) throws Exception {
		return ( (LogicalResourcesProviderRequestingI) this.connector ).allocateApplicationVMs(requesterUri, avmCount);
	}

	@Override
	public void releaseApplicationVMs(String requesterUri, AllocatedApplicationVM[] avms) throws Exception {
		( (LogicalResourcesProviderRequestingI) this.connector ).releaseApplicationVMs(requesterUri, avms);
	}

	@Override
	public void connectApplicationVM(String requesterUri, AllocatedApplicationVM aavm, AllocatedDispatcher adsp)
			throws Exception {
		( (LogicalResourcesProviderRequestingI) this.connector ).connectApplicationVM(requesterUri, aavm, adsp);
	}
	
	@Override
	public void disconnectApplicationVM(String requesterUri, AllocatedApplicationVM aavm, AllocatedDispatcher adsp)
			throws Exception {
		( (LogicalResourcesProviderRequestingI) this.connector ).disconnectApplicationVM(requesterUri, aavm, adsp);
	}
	
	@Override
	public boolean isLocal(Object o) throws Exception {
		return ( (LogicalResourcesProviderRequestingI) this.connector ).isLocal(o);
	}
	
}
