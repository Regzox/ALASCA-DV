package fr.upmc.datacenter.providers.resources.logical.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.providers.resources.interfaces.RequestingI;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.software.controllers.performance.AllocatedDispatcher;

public interface LogicalResourcesProviderRequestingI 
extends 	RequestingI,
			RequiredI,
			OfferedI
{
	
	Integer[] increaseApplicationVMFrequency(String requesterUri, AllocatedApplicationVM avm) throws Exception;
	
	
	Integer[] decreaseApplicationVMFrequency(String requesterUri, AllocatedApplicationVM avm) throws Exception;
	
	
	Integer increaseApplicationVMCores(String requesterUri, AllocatedApplicationVM avm, Integer coreCount) throws Exception;
	
	
	Integer decreaseApplicationVMCores(String requesterUri, AllocatedApplicationVM avm, Integer coreCount) throws Exception;
	
	
	AllocatedApplicationVM[] allocateApplicationVMs(String requesterUri, Integer avmCount) throws Exception;
	
	
	AllocatedApplicationVM[] releaseApplicationVMs(String requesterUri, AllocatedApplicationVM[] avms) throws Exception;
	
	
	void connectApplicationVM(String requesterUri, AllocatedApplicationVM aavm, AllocatedDispatcher adsp) throws Exception;
	
	void disconnectApplicationVM(String requesterUri, AllocatedApplicationVM aavm, AllocatedDispatcher adsp) throws Exception;
	
}
