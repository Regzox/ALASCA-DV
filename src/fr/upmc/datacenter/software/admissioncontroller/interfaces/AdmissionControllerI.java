package fr.upmc.datacenter.software.admissioncontroller.interfaces;

import fr.upmc.datacenter.software.enumerations.Tag;

/**
 * Methodes du {@link AdmissionController} ne dépendant pas du BCM.
 * 
 * @author Daniel RADEAU
 *
 */

public interface AdmissionControllerI {

	/**
	 * Génère une URI aléatoire à partir d'un {@link Tag} 
	 * 
	 * @param tag
	 * @return
	 */
	
	String generateURI(Object tag);
	
}
