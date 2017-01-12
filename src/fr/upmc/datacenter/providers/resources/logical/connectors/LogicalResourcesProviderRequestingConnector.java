package fr.upmc.datacenter.providers.resources.logical.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderRequestingI;
import fr.upmc.datacenter.software.controllers.performance.AllocatedDispatcher;

public class LogicalResourcesProviderRequestingConnector 
extends AbstractConnector
implements LogicalResourcesProviderRequestingI
{

	@Override
	public void increaseApplicationVMFrequency(String requesterUri, AllocatedApplicationVM avm) throws Exception {
		( (LogicalResourcesProviderRequestingI) this.offering ).increaseApplicationVMFrequency(requesterUri, avm);	
	}

	@Override
	public void decreaseApplicationVMFrequency(String requesterUri, AllocatedApplicationVM avm) throws Exception {
		( (LogicalResourcesProviderRequestingI) this.offering ).decreaseApplicationVMFrequency(requesterUri, avm);		
	}

	@Override
	public void increaseApplicationVMCores(String requesterUri, AllocatedApplicationVM avm, Integer coreCount) throws Exception {
		( (LogicalResourcesProviderRequestingI) this.offering ).increaseApplicationVMCores(requesterUri, avm, coreCount);
	}

	@Override
	public void decreaseApplicationVMCores(String requesterUri, AllocatedApplicationVM avm, Integer coreCount) throws Exception {
		( (LogicalResourcesProviderRequestingI) this.offering ).decreaseApplicationVMCores(requesterUri, avm, coreCount);
	}

	@Override
	public AllocatedApplicationVM[] allocateApplicationVMs(String requesterUri, Integer avmCount) throws Exception {
		return ( (LogicalResourcesProviderRequestingI) this.offering ).allocateApplicationVMs(requesterUri, avmCount);
	}

	@Override
	public void releaseApplicationVMs(String requesterUri, AllocatedApplicationVM[] avms) throws Exception {
		( (LogicalResourcesProviderRequestingI) this.offering ).releaseApplicationVMs(requesterUri, avms);
	}
	
	@Override
	public void connectApplicationVM(String requesterUri, AllocatedApplicationVM aavm, AllocatedDispatcher adsp)
			throws Exception {
		( (LogicalResourcesProviderRequestingI) this.offering ).connectApplicationVM(requesterUri, aavm, adsp);
	}

	@Override
	public void disconnectApplicationVM(String requesterUri, AllocatedApplicationVM aavm, AllocatedDispatcher adsp)
			throws Exception {
		( (LogicalResourcesProviderRequestingI) this.offering ).disconnectApplicationVM(requesterUri, aavm, adsp);
	}
	
	@Override
	public boolean isLocal(Object o) throws Exception {
		return ( (LogicalResourcesProviderRequestingI) this.offering ).isLocal(o);
	}

}
