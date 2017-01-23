package fr.upmc.datacenter.software.controllers.performance.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;

/**
 * Interface de management du contrôleur de performance
 * 
 * @author Daniel RADEAU
 *
 */

public interface PerformanceControllerManagementI extends RequiredI, OfferedI {

	/**
	 * Connexion au {@link LogicalResourceProvider}
	 * 
	 * @param lrppdi {@link LogicalResourcesProviderPortsDataI} pour la connexion
	 * @throws Exception
	 */
	
	void connectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception;
	
	/**
	 * Déconnexion du {@link LogicalResourceProvider}
	 * 
	 * @param lrppdi {@link LogicalResourcesProviderPortsDataI} pour la déconnexion
	 * @throws Exception
	 */
	
	void disconnectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception;

}
