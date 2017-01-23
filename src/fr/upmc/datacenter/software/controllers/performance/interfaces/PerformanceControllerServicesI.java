package fr.upmc.datacenter.software.controllers.performance.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * Services du contrôleur de performances
 * 
 * @author Daniel RADEAU
 *
 */

public interface PerformanceControllerServicesI extends RequiredI, OfferedI {

	/**
	 * Accept une nouvelle application au sein du contrôleur de performance
	 * 
	 * @throws Exception
	 */
	
	void acceptApplication() throws Exception;
	
}
