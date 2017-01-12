package fr.upmc.datacenter.software.controllers.performance.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface PerformanceControllerServicesI extends RequiredI, OfferedI {

	void acceptApplication() throws Exception;
	
}
