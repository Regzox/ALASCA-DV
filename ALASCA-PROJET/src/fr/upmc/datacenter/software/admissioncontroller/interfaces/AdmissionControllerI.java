package fr.upmc.datacenter.software.admissioncontroller.interfaces;

import fr.upmc.datacenter.software.enumerations.Tag;

public interface AdmissionControllerI {

	/**
	 * Génère une URI aléatoire à partir d'un {@link Tag} 
	 * 
	 * @param tag
	 * @return
	 */
	
	String generateURI(Object tag);
	
}
