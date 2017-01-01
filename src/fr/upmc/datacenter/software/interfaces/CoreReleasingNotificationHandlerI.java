package fr.upmc.datacenter.software.interfaces;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * Interface de gestion des libérations de coeurs 
 * 
 * @author Daniel RADEAU
 *
 */

public interface CoreReleasingNotificationHandlerI {

	/**
	 * Accepte la libération d'un coeur détenu par l'AVM.
	 * Cette méthode n'est appelée qu'en cas de libération effective du coeur par l'AVM.
	 * Dans le cadre d'une demande de libération de coeur, celle-ci n'est qu'effectivement réalisée
	 * qu'au moment de la terminaison de la tâche ce qui peut mettre un certain délai en fonction 
	 * de la durée de cette même tâche.
	 * 
	 * @param avmURI
	 * @param allocatedCore
	 * @throws Exception 
	 */
	
	void acceptCoreReleasing(String avmURI, AllocatedCore allocatedCore) throws Exception;
}
