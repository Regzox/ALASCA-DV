package fr.upmc.datacenter.software.admissioncontroller.interfaces;

import fr.upmc.datacenter.software.enumerations.Tag;

public interface AdmissionControllerI {

	/**
	 * G�n�re une URI al�atoire � partir d'un {@link Tag} 
	 * 
	 * @param tag
	 * @return
	 */
	
	String generateURI(Object tag);
	
}
