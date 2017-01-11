package fr.upmc.datacenter.providers.resources.physical.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * Interface des services proposés par un fourniseur de ressources physiques.
 * Elle permet de manipuler ce qui touche aux resources physiques (hardware) tels 
 * les fréquences des coeurs sur les machines ou bien l'allocation des coeurs.
 * 
 * @author Daniel RADEAU
 *
 */

public interface PhysicalResourcesProviderServicesI extends OfferedI, RequiredI {

	/**
	 * Permet d'augmenter la fréquence d'un coeur de processeur.
	 * 
	 * @param processorURI
	 * @param coreNo
	 * @throws Exception
	 */
	
	public void increaseCoreFrenquency(AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de diminuer la fréquence d'un coeur de processeur.
	 * 
	 * @param processorURI
	 * @param coreNo
	 * @throws Exception
	 */
	
	public void decreaseCoreFrenquency(AllocatedCore ac) throws Exception;
	
	/**
	 * Permet d'augmenter la fréquence d'un processeur entier.
	 * 
	 * @param processorURI
	 * @throws Exception
	 */
	
	public void increaseProcessorFrenquency(AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de diminuer la fréquence d'un processeur entier.
	 * 
	 * @param processorURI
	 * @throws Exception
	 */
	
	public void decreaseProcessorFrenquency(AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de d'augmenter la fréquence d'un ordinateur entier.
	 * 
	 * @param computerURI
	 * @throws Exception
	 */
	
	public void increaseComputerFrenquency(AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de diminuer la fréquence d'un ordinateur entier.
	 * 
	 * @param computerURI
	 * @throws Exception
	 */
	
	public void decreaseComputerFrenquency(AllocatedCore ac) throws Exception;

	/**
	 * Permet l'allocation d'un certain nombre de coeurs sur les ordinateurs disponibles.
	 * 
	 * @param cores
	 * @return
	 * @throws Exception
	 */
	
	public AllocatedCore[] allocateCores(Integer cores) throws Exception;
	
	/**
	 * Permet l'allocation d'un certain nombre de coeurs sur l'ordinateur auquel appartient les coeurs alloués.
	 * 
	 * @param cores
	 * @return
	 * @throws Exception
	 */
	
	public AllocatedCore[] allocateCores(AllocatedCore[] acs, Integer cores) throws Exception;
	
	/**
	 * Permet de la libération d'une liste de coeurs alloués.
	 * 
	 * @param allocatedCores
	 * @throws Exception
	 */
	
	public void releaseCores(AllocatedCore[] allocatedCores) throws Exception;
	
}
