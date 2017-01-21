package fr.upmc.datacenter.providers.resources.logical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderServicesI;
import fr.upmc.datacenter.software.controllers.performance.AllocatedDispatcher;

public class LogicalResourcesProviderServicesOutboundPort 
extends AbstractOutboundPort
implements LogicalResourcesProviderServicesI
{

	public LogicalResourcesProviderServicesOutboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public LogicalResourcesProviderServicesOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public Integer[] increaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception {
		return ( (LogicalResourcesProviderServicesI) this.connector ).increaseApplicationVMFrequency(avm);		
	}

	@Override
	public Integer[] decreaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception {
		return ( (LogicalResourcesProviderServicesI) this.connector ).decreaseApplicationVMFrequency(avm);		
	}

	@Override
	public Integer increaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception {
		return ( (LogicalResourcesProviderServicesI) this.connector ).increaseApplicationVMCores(avm, coreCount);
	}

	@Override
	public Integer decreaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception {
		return ( (LogicalResourcesProviderServicesI) this.connector ).decreaseApplicationVMCores(avm, coreCount);		
	}

	@Override
	public AllocatedApplicationVM[] allocateApplicationVMs(Integer avmCount) throws Exception {
		return ( (LogicalResourcesProviderServicesI) this.connector ).allocateApplicationVMs(avmCount);
	}

	@Override
	public AllocatedApplicationVM[] releaseApplicationVMs(AllocatedApplicationVM[] avms) throws Exception {
		return ( (LogicalResourcesProviderServicesI) this.connector ).releaseApplicationVMs(avms);		
	}
	
	@Override
	public void connectApplicationVM(AllocatedApplicationVM aavm, AllocatedDispatcher adsp) throws Exception {
		( (LogicalResourcesProviderServicesI) this.connector ).connectApplicationVM(aavm, adsp);		
	}
	
	@Override
	public void disconnectApplicationVM(AllocatedApplicationVM aavm, AllocatedDispatcher adsp)
			throws Exception {
		( (LogicalResourcesProviderServicesI) this.connector ).disconnectApplicationVM(aavm, adsp);
	}
}
