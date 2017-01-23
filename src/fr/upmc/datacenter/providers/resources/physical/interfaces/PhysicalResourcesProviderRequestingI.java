package fr.upmc.datacenter.providers.resources.physical.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.interfaces.RequestingI;
import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;

/**
 * Interface de requêtage entre {@link PhysicalResourcesProvider}
 * 
 * @author Daniel RADEAU
 *
 */

public interface PhysicalResourcesProviderRequestingI extends RequestingI, OfferedI, RequiredI {

	/**
	 * Permet d'augmenter la fréquence d'un coeur de processeur.
	 * 
	 * @param processorURI
	 * @param coreNo
	 * @return TODO
	 * @throws Exception
	 */
	
	public Integer increaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de diminuer la fréquence d'un coeur de processeur.
	 * 
	 * @param processorURI
	 * @param coreNo
	 * @return TODO
	 * @throws Exception
	 */
	
	public Integer decreaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception;
	
	/**
	 * Permet d'augmenter la fréquence d'un processeur entier.
	 * 
	 * @param processorURI
	 * @return TODO
	 * @throws Exception
	 */
	
	public Integer[] increaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de diminuer la fréquence d'un processeur entier.
	 * 
	 * @param processorURI
	 * @return TODO
	 * @throws Exception
	 */
	
	public Integer[] decreaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de d'augmenter la fréquence d'un ordinateur entier.
	 * 
	 * @param computerURI
	 * @return TODO
	 * @throws Exception
	 */
	
	public Integer[][] increaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de diminuer la fréquence d'un ordinateur entier.
	 * 
	 * @param computerURI
	 * @return TODO
	 * @throws Exception
	 */
	
	public Integer[][] decreaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception;

	/**
	 * Permet l'allocation d'un certain nombre de coeurs sur les ordinateurs disponibles.
	 * 
	 * @param cores
	 * @return
	 * @throws Exception
	 */
	
	public AllocatedCore[] allocateCores(String requesterUri, AllocatedCore[] acs, Integer cores) throws Exception;
	
	/**
	 * Permet de la libération d'une liste de coeurs alloués.
	 * 
	 * @param allocatedCores
	 * @return TODO
	 * @throws Exception
	 */
	
	public AllocatedCore[] releaseCores(String requesterUri, AllocatedCore[] allocatedCores) throws Exception;
	
}
