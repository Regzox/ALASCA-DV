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
	 * Augmente la fréquence des coeurs alloué par l'{@link AllocatedApplicationVM} passé en paramètre. 
	 * 
	 * @param avm
	 * @throws Exception TODO
	 */
	
	void increaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception;
	
	/**
	 * Diminue la fréquence des coeurs alloué par l'{@link AllocatedApplicationVM} passé en paramètre.
	 * 
	 * @param avm
	 * @throws Exception TODO
	 */
	
	void decreaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception;
	
	/**
	 * Augmente le nombre de coeurs alloués à une même {@link AllocatedApplicationVM}.
	 * 
	 * @param avm
	 * @param coreCount
	 * @throws Exception TODO
	 */
	
	void increaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception;
	
	/**
	 * Diminue le nombre de coeurs alloués à une même {@link AllocatedApplicationVM}.
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
	 * Permet la désallocation d'un certain nombre de {@link AllocatedApplicationVM}.
	 * @param avms
	 * @throws Exception TODO
	 */
	
	void releaseApplicationVMs(AllocatedApplicationVM[] avms) throws Exception;
	
}
