package fr.upmc.datacenter.software.controllers.performance.interfaces;

import fr.upmc.datacenter.data.interfaces.PerformanceControllerPortsDataI;
import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;

public interface PerformanceControllerManagementI extends RequiredI, OfferedI {

	void connectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception;
	
	void disconnectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception;
	
	void connectPerformanceController(PerformanceControllerPortsDataI cpdi) throws Exception;
	
	void disconnectPerformanceController(PerformanceControllerPortsDataI cpdi) throws Exception;

}
