package fr.upmc.datacenter.providers.resources.logical.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;

/**
 * Services du fournisseur de resources logiques.
 * 
 * @author Daniel RADEAU
 *
 */

public interface LogicalResourcesProviderServicesI 
extends 	RequiredI,
			OfferedI
{
	
	/**
	 * Augmente la fr�quence des coeurs allou� par l'{@link AllocatedApplicationVM} pass� en param�tre. 
	 * 
	 * @param avm
	 * @throws Exception TODO
	 */
	
	void increaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception;
	
	/**
	 * Diminue la fr�quence des coeurs allou� par l'{@link AllocatedApplicationVM} pass� en param�tre.
	 * 
	 * @param avm
	 * @throws Exception TODO
	 */
	
	void decreaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception;
	
	/**
	 * Augmente le nombre de coeurs allou�s � une m�me {@link AllocatedApplicationVM}.
	 * 
	 * @param avm
	 * @param coreCount
	 * @throws Exception TODO
	 */
	
	void increaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception;
	
	/**
	 * Diminue le nombre de coeurs allou�s � une m�me {@link AllocatedApplicationVM}.
	 * 
	 * @param avm
	 * @param coreCount
	 * @throws Exception TODO
	 */
	
	void decreaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception;
	
	/**
	 * Permet l'allocation d'un certain nombre de {@link AllocatedApplicationVM}.
	 * 
	 * @param avmCount
	 * @return TODO
	 * @throws Exception TODO
	 */
	
	AllocatedApplicationVM[] allocateApplicationVMs(Integer avmCount) throws Exception;
	
	/**
	 * Permet la d�sallocation d'un certain nombre de {@link AllocatedApplicationVM}.
	 * @param avms
	 * @throws Exception TODO
	 */
	
	void releaseApplicationVMs(AllocatedApplicationVM[] avms) throws Exception;
	
}
