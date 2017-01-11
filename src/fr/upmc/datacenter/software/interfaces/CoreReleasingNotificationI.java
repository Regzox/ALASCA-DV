package fr.upmc.datacenter.software.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * Interface permettant de notifier d'une lib�ration de coeur.
 * 
 * @author Daniel RADEAU
 *
 */

public interface CoreReleasingNotificationI extends RequiredI, OfferedI {

	/**
	 * Notifie d'une lib�ration de coeur
	 * 
	 * @param avmURI l'URI de l'AVM qui lib�re un coeur pour pouvoir authentifier la source de la lib�ration
	 * @param allocatedCore
	 */
	void notifyCoreReleasing(String avmURI, AllocatedCore allocatedCore) throws Exception;
	
}
