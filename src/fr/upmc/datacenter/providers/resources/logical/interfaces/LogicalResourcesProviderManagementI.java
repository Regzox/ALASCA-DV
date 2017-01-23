package fr.upmc.datacenter.providers.resources.logical.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.data.interfaces.PerformanceControllerPortsDataI;
import fr.upmc.datacenter.data.interfaces.PhysicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;

/**
 * Interface de gestion du {@link LogicalResourceProvider}.
 * Utilisé pour réaliser les interconnexions externes au composant
 * 
 * @author Daniel RADEAU
 *
 */

public interface LogicalResourcesProviderManagementI 
extends RequiredI, OfferedI
{
	void connectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppdi) throws Exception;
	
	void disconnectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppdi) throws Exception;
	
	void connectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception;
	
	void disconnectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception;
	
	void connectPerformanceController(PerformanceControllerPortsDataI pcpdi) throws Exception;
	
	void disconnectPerformanceController(PerformanceControllerPortsDataI pcpdi) throws Exception;
	
	void connectLogicalResourcesProviderNotifyBack(LogicalResourcesProviderPortsDataI lrppdi) throws Exception;
	
	void disconnectLogicalResourcesProviderNotifyBack(LogicalResourcesProviderPortsDataI lrppdi) throws Exception;
}
