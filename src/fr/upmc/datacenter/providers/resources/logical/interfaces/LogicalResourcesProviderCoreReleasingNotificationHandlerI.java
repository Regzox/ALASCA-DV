package fr.upmc.datacenter.providers.resources.logical.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.processors.Core;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;

/**
 * Interface de gestion des libération de {@link Core} d' {@link AllocatedApplicationVM}
 * 
 * @author Daniel RADEAU
 *
 */

public interface LogicalResourcesProviderCoreReleasingNotificationHandlerI extends RequiredI, OfferedI {
	
	/**
	 * Accepte la notification de libération de {@link Core} pour l' {@link AllocatedApplicationVM}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @throws Exception
	 */
	
	void acceptCoreReleasingNotification(AllocatedApplicationVM aavm) throws Exception;

}
