package fr.upmc.datacenter.providers.resources.exceptions;

import fr.upmc.datacenter.hardware.computer.extended.Computer;
import fr.upmc.datacenter.hardware.processors.Core;
import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;
import fr.upmc.datacenter.software.applicationvm.extended.ApplicationVM;

/**
 * Execption levée dans le cas d'une différence d'origine entre les {@link Core} 
 * des différentes {@link ApplicationVM}.
 * 
 * Prévu pour le cas de libération de {@link ApplicationVM}, si un vecteur de {@link Core}
 * doit être libéré, alors tous les {@link Core} doivent appartenir au même {@link PhysicalResourcesProvider}
 * et au même {@link Computer} sans quoi cette exception doit être levée.
 * 
 * 
 * @author Daniel RADEAU
 *
 */

public class OriginException extends Exception {

	private static final long serialVersionUID = 5331610054168264909L;

	public OriginException(String message) {
		super(message);
	}
	
}
