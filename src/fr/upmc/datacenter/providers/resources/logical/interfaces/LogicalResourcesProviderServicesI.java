package fr.upmc.datacenter.providers.resources.logical.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.software.controllers.performance.AllocatedDispatcher;

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
	 * @return TODO
	 * @throws Exception TODO
	 */
	
	Integer[] increaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception;
	
	/**
	 * Diminue la fréquence des coeurs alloué par l'{@link AllocatedApplicationVM} passé en paramètre.
	 * 
	 * @param avm
	 * @return TODO
	 * @throws Exception TODO
	 */
	
	Integer[] decreaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception;
	
	/**
	 * Augmente le nombre de coeurs alloués à une même {@link AllocatedApplicationVM}.
	 * 
	 * @param avm
	 * @param coreCount
	 * @return TODO
	 * @throws Exception TODO
	 */
	
	Integer increaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception;
	
	/**
	 * Diminue le nombre de coeurs alloués à une même {@link AllocatedApplicationVM}.
	 * 
	 * @param avm
	 * @param coreCount
	 * @return TODO
	 * @throws Exception TODO
	 */
	
	Integer decreaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception;
	
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
	 * @return TODO
	 * @throws Exception TODO
	 */
	
	AllocatedApplicationVM[] releaseApplicationVMs(AllocatedApplicationVM[] avms) throws Exception;
	
	/**
	 * Permet de réalisé la connection du port de sortie de l'avm concernée au port d'entrée du répartiteur concerné.
	 * 
	 * @param aavm
	 * @param adsp
	 * @throws Exception
	 */
	
	void connectApplicationVM(AllocatedApplicationVM aavm, AllocatedDispatcher adsp) throws Exception;
	
	/**
	 * Permet de réalisé la déconnection de l'avm au répartiteur de requêtes.
	 * 
	 * @param aavm
	 * @param adsp
	 * @throws Exception
	 */
	
	void disconnectApplicationVM(AllocatedApplicationVM aavm, AllocatedDispatcher adsp) throws Exception;
	
}
