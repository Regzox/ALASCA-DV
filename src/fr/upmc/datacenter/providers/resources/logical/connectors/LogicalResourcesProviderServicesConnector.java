package fr.upmc.datacenter.providers.resources.logical.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderServicesI;
import fr.upmc.datacenter.software.controllers.performance.AllocatedDispatcher;

public class LogicalResourcesProviderServicesConnector 
extends AbstractConnector
implements LogicalResourcesProviderServicesI
{

	@Override
	public Integer[] increaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception {
		return ( (LogicalResourcesProviderServicesI) this.offering ).increaseApplicationVMFrequency(avm);		
	}

	@Override
	public Integer[] decreaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception {
		return ( (LogicalResourcesProviderServicesI) this.offering ).decreaseApplicationVMFrequency(avm);		
	}

	@Override
	public Integer increaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception {
		return ( (LogicalResourcesProviderServicesI) this.offering ).increaseApplicationVMCores(avm, coreCount);
	}

	@Override
	public Integer decreaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception {
		return ( (LogicalResourcesProviderServicesI) this.offering ).decreaseApplicationVMCores(avm, coreCount);		
	}

	@Override
	public AllocatedApplicationVM[] allocateApplicationVMs(Integer avmCount) throws Exception {
		return ( (LogicalResourcesProviderServicesI) this.offering ).allocateApplicationVMs(avmCount);
	}

	@Override
	public AllocatedApplicationVM[] releaseApplicationVMs(AllocatedApplicationVM[] avms) throws Exception {
		return ( (LogicalResourcesProviderServicesI) this.offering ).releaseApplicationVMs(avms);		
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
