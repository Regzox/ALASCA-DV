package fr.upmc.datacenter.software.interfaces;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * Interface permettant de notifier d'une libération de coeur.
 * 
 * @author Daniel RADEAU
 *
 */

public interface CoreReleasingNotificationI {

	/**
	 * Notifie d'une libération de coeur
	 * 
	 * @param avmURI l'URI de l'AVM qui libère un coeur pour pouvoir authentifier la source de la libération
	 * @param allocatedCore
	 */
	void notifyCoreReleasing(String avmURI, AllocatedCore allocatedCore) throws Exception;
	
}
