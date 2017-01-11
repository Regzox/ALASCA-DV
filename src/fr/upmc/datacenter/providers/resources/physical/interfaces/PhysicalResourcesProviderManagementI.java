package fr.upmc.datacenter.providers.resources.physical.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.data.interfaces.ComputerPortsDataI;
import fr.upmc.datacenter.data.interfaces.PhysicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.hardware.computer.extended.Computer;
import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;

/**
 * Interface de gestion du fournisseur de ressources physiques.
 * Elle permet la manipulation du composant {@link PhysicalResourceProvider}.
 * 
 * @author Daniel RADEAU
 *
 */

public interface PhysicalResourcesProviderManagementI extends OfferedI, RequiredI {

	/**
	 * Permet de connecter un composant {@link Computer} grâce à ses informations de ports {@link ComputerPortsDataI}.
	 * 
	 * @param cpd
	 * @throws Exception
	 */
	
	void connectComputer(ComputerPortsDataI cpd) throws Exception;
	
	/**
	 * Permet de déconnecter un composant {@link Computer} grâce à ses informations de ports {@link ComputerPortsDataI}.
	 * 
	 * @param cpd
	 * @throws Exception
	 */
	
	void disconnectComputer(ComputerPortsDataI cpd) throws Exception;
	
	/**
	 * Permet de connecter un composant {@link PhysicalResourcesProvider} grâce à ses informations de ports {@link PhysicalResourcesProviderPortsDataI}.
	 * 
	 * @param prppd
	 * @throws Exception
	 */
	
	void connectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppd) throws Exception;
	
	/**
	 * Permet de déconnecter un composant {@link PhysicalResourcesProvider} grâce à ses informations de ports {@link PhysicalResourcesProviderPortsDataI}.
	 * 
	 * @param prppd
	 * @throws Exception
	 */
	
	void disconnectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppd) throws Exception;
	
}
