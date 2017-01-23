package fr.upmc.datacenter.software.controllers.performance.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * Services du contr�leur de performances
 * 
 * @author Daniel RADEAU
 *
 */

public interface PerformanceControllerServicesI extends RequiredI, OfferedI {

	/**
	 * Accept une nouvelle application au sein du contr�leur de performance
	 * 
	 * @throws Exception
	 */
	
	void acceptApplication() throws Exception;
	
}
