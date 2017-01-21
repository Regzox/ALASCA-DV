package fr.upmc.datacenter.providers.resources.physical.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * Interface des services propos�s par un fourniseur de ressources physiques.
 * Elle permet de manipuler ce qui touche aux resources physiques (hardware) tels 
 * les fr�quences des coeurs sur les machines ou bien l'allocation des coeurs.
 * 
 * @author Daniel RADEAU
 *
 */

public interface PhysicalResourcesProviderServicesI extends OfferedI, RequiredI {

	/**
	 * Permet d'augmenter la fr�quence d'un coeur de processeur.
	 * 
	 * @param processorURI
	 * @param coreNo
	 * @return TODO
	 * @throws Exception
	 */
	
	public Integer increaseCoreFrenquency(AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de diminuer la fr�quence d'un coeur de processeur.
	 * 
	 * @param processorURI
	 * @param coreNo
	 * @return TODO
	 * @throws Exception
	 */
	
	public Integer decreaseCoreFrenquency(AllocatedCore ac) throws Exception;
	
	/**
	 * Permet d'augmenter la fr�quence d'un processeur entier.
	 * 
	 * @param processorURI
	 * @return TODO
	 * @throws Exception
	 */
	
	public Integer[] increaseProcessorFrenquency(AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de diminuer la fr�quence d'un processeur entier.
	 * 
	 * @param processorURI
	 * @return TODO
	 * @throws Exception
	 */
	
	public Integer[] decreaseProcessorFrenquency(AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de d'augmenter la fr�quence d'un ordinateur entier.
	 * 
	 * @param computerURI
	 * @return TODO
	 * @throws Exception
	 */
	
	public Integer[][] increaseComputerFrenquency(AllocatedCore ac) throws Exception;
	
	/**
	 * Permet de diminuer la fr�quence d'un ordinateur entier.
	 * 
	 * @param computerURI
	 * @return TODO
	 * @throws Exception
	 */
	
	public Integer[][] decreaseComputerFrenquency(AllocatedCore ac) throws Exception;

	/**
	 * Permet l'allocation d'un certain nombre de coeurs sur les ordinateurs disponibles.
	 * 
	 * @param cores
	 * @return
	 * @throws Exception
	 */
	
	public AllocatedCore[] allocateCores(Integer cores) throws Exception;
	
	/**
	 * Permet l'allocation d'un certain nombre de coeurs sur l'ordinateur auquel appartient les coeurs allou�s.
	 * 
	 * @param cores
	 * @return
	 * @throws Exception
	 */
	
	public AllocatedCore[] allocateCores(AllocatedCore[] acs, Integer cores) throws Exception;
	
	/**
	 * Permet de la lib�ration d'une liste de coeurs allou�s.
	 * 
	 * @param allocatedCores
	 * @return TODO
	 * @throws Exception
	 */
	
	public AllocatedCore[] releaseCores(AllocatedCore[] allocatedCores) throws Exception;
	
}
