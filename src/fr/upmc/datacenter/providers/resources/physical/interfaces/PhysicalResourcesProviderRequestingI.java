package fr.upmc.datacenter.providers.resources.physical.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.interfaces.RequestingI;

public interface PhysicalResourcesProviderRequestingI extends RequestingI, OfferedI, RequiredI {

	/**
	 * Permet d'augmenter la fréquence d'un coeur de processeur.
	 * 
	 * @param processorURI
	 * @param coreNo
	 * @throws Exception
	 */
	
	public void increaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de diminuer la fréquence d'un coeur de processeur.
	 * 
	 * @param processorURI
	 * @param coreNo
	 * @throws Exception
	 */
	
	public void decreaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception;
	
	/**
	 * Permet d'augmenter la fréquence d'un processeur entier.
	 * 
	 * @param processorURI
	 * @throws Exception
	 */
	
	public void increaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de diminuer la fréquence d'un processeur entier.
	 * 
	 * @param processorURI
	 * @throws Exception
	 */
	
	public void decreaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de d'augmenter la fréquence d'un ordinateur entier.
	 * 
	 * @param computerURI
	 * @throws Exception
	 */
	
	public void increaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de diminuer la fréquence d'un ordinateur entier.
	 * 
	 * @param computerURI
	 * @throws Exception
	 */
	
	public void decreaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception;

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
	 * @throws Exception
	 */
	
	public void releaseCores(String requesterUri, AllocatedCore[] allocatedCores) throws Exception;
	
}
