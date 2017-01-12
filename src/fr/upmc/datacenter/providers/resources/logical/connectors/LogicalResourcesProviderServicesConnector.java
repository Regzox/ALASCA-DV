package fr.upmc.datacenter.providers.resources.logical.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderRequestingI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderServicesI;
import fr.upmc.datacenter.software.controllers.performance.AllocatedDispatcher;

public class LogicalResourcesProviderServicesConnector 
extends AbstractConnector
implements LogicalResourcesProviderServicesI
{

	@Override
	public void increaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception {
		( (LogicalResourcesProviderServicesI) this.offering ).increaseApplicationVMFrequency(avm);		
	}

	@Override
	public void decreaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception {
		( (LogicalResourcesProviderServicesI) this.offering ).decreaseApplicationVMFrequency(avm);		
	}

	@Override
	public void increaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception {
		( (LogicalResourcesProviderServicesI) this.offering ).increaseApplicationVMCores(avm, coreCount);
	}

	@Override
	public void decreaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception {
		( (LogicalResourcesProviderServicesI) this.offering ).decreaseApplicationVMCores(avm, coreCount);		
	}

	@Override
	public AllocatedApplicationVM[] allocateApplicationVMs(Integer avmCount) throws Exception {
		return ( (LogicalResourcesProviderServicesI) this.offering ).allocateApplicationVMs(avmCount);
	}

	@Override
	public void releaseApplicationVMs(AllocatedApplicationVM[] avms) throws Exception {
		( (LogicalResourcesProviderServicesI) this.offering ).releaseApplicationVMs(avms);		
	}
	
	@Override
	public void connectApplicationVM(AllocatedApplicationVM aavm, AllocatedDispatcher adsp) throws Exception {
		( (LogicalResourcesProviderServicesI) this.offering ).connectApplicationVM(aavm, adsp);		
	}
	
	@Override
	public void disconnectApplicationVM(AllocatedApplicationVM aavm, AllocatedDispatcher adsp)
			throws Exception {
		( (LogicalResourcesProviderServicesI) this.offering ).disconnectApplicationVM(aavm, adsp);
	}

}
