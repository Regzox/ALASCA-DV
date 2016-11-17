package fr.upmc.datacenter.software.dispatcher.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * 
 * Allows the management of a dispatcher by an other connected component
 * 
 * @author Daniel RADEAU
 *
 */

public interface DispatcherManagementI
	extends		
		OfferedI,
		RequiredI
{
	
	/**
	 * Connecte le dispatcheur à un générateur de requêtes via l'uri du port d'entrée de notifications de requêtes
	 * 
	 * @param rsip
	 */
	
	public String connectToRequestGenerator(final String rnipURI) throws Exception;
	
	/**
	 * 
	 * Déconnecte le dispatcheur du générateur de requêtes actuel
	 * 
	 */
	
	public void disconnectFromRequestGenerator() throws Exception;
	
	/**
	 * 
	 * Connecte le dispatcheur à une AVM via l'uri de son port d'entrée de soumission de requêtes 
	 * 
	 * @param rsip
	 */
	public String connectToApplicationVM(final String rsipURI) throws Exception;
	
	/**
	 * 
	 * Déconnecte le dispatcheur de l'AVM la moins active
	 * 
	 */
	
	public void disconnectFromApplicationVM() throws Exception;
}
