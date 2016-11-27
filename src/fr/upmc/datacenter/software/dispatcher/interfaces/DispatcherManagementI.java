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
	 * Connecte le dispatcheur � un g�n�rateur de requ�tes via l'uri du port d'entr�e de notifications de requ�tes
	 * 
	 * @param rsip
	 */
	
	public String connectToRequestGenerator(final String rnipURI) throws Exception;
	
	/**
	 * 
	 * D�connecte le dispatcheur du g�n�rateur de requ�tes actuel
	 * 
	 */
	
	public void disconnectFromRequestGenerator() throws Exception;
	
	/**
	 * 
	 * Connecte le dispatcheur � une AVM via l'uri de son port d'entr�e de soumission de requ�tes 
	 * 
	 * @param rsip
	 */
	public String connectToApplicationVM(final String rsipURI) throws Exception;
	
	/**
	 * 
	 * D�connecte le dispatcheur de l'AVM la moins active
	 * 
	 */
	
	public void disconnectFromApplicationVM() throws Exception;
}
