package fr.upmc.datacenter.providers.resources.exceptions;

import fr.upmc.datacenter.hardware.computer.extended.Computer;
import fr.upmc.datacenter.hardware.processors.Core;
import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;
import fr.upmc.datacenter.software.applicationvm.extended.ApplicationVM;

/**
 * Execption lev�e dans le cas d'une diff�rence d'origine entre les {@link Core} 
 * des diff�rentes {@link ApplicationVM}.
 * 
 * Pr�vu pour le cas de lib�ration de {@link ApplicationVM}, si un vecteur de {@link Core}
 * doit �tre lib�r�, alors tous les {@link Core} doivent appartenir au m�me {@link PhysicalResourcesProvider}
 * et au m�me {@link Computer} sans quoi cette exception doit �tre lev�e.
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
