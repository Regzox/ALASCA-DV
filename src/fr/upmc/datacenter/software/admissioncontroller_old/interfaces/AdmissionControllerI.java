package fr.upmc.datacenter.software.admissioncontroller_old.interfaces;

import fr.upmc.datacenter.software.enumerations.Tag;

/**
 * Methodes du {@link AdmissionController} ne d�pendant pas du BCM.
 * 
 * @author Daniel RADEAU
 *
 */

public interface AdmissionControllerI {

	/**
	 * G�n�re une URI al�atoire � partir d'un {@link Tag} 
	 * 
	 * @param tag
	 * @return
	 */
	
	String generateURI(Object tag);
	
}
