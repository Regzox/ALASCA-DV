package fr.upmc.datacenter.data.interfaces;

import fr.upmc.datacenter.data.ComputerPortsData;
import fr.upmc.datacenter.hardware.computer.extended.Computer;

/**
 * Interface du {@link ComputerPortsData}
 * 
 * @author Daniel RADEAU
 *
 */

public interface ComputerPortsDataI extends PortsDataI {
	
	/**
	 * Retourne nom du port d'entrée de services du composant {@link Computer}
	 * @return
	 */
	
	String getComputerServicesInboundPort();
	
	/**
	 * Retourne le nom du port d'entrée de données statiques du composant {@link Computer}
	 * 
	 * @return
	 */
	
	String getComputerStaticStateDataInboundPort();
	
	/**
	 * Retourne le nom du port d'entrée de données dynamiques du composant {@link Computer}
	 * 
	 * @return
	 */
	
	String getComputerDynamicStateDataInboundPort();
	
	/**
	 * Retourne le nom du port d'entrée de libération de coeur du composant {@link Computer}
	 * @return
	 */
	
	String getComputerCoreReleasingInboundPort();
	
}
