package fr.upmc.datacenter.providers.resources.logical.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.processors.Core;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;

/**
 * Infeface de notification de lib�ration de coeurs
 * 
 * @author Daniel RADEAU
 *
 */

public interface LogicalResourcesProviderCoreReleasingNotificationI extends RequiredI, OfferedI {
	
	/**
	 * Notifie que l' {@link AllocatedApplicationVM} vient de lib�rer un {@link Core}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} qui vient de lib�rer un coeur
	 * @throws Exception
	 */
	
	void notifyCoreReleasing(AllocatedApplicationVM aavm) throws Exception;

}
