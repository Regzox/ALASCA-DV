package fr.upmc.external.software.applications;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Application abstraite qui implémentera d'éventuelles interfaces
 * proposant des methodes dont le nombre d'instructions sera aléatoirement
 * tiré à l'instanciation de l'application.
 * 
 * @author Daniel RADEAU
 *
 */

public abstract class AbstractApplication {

	public static long maxInstructions = (long) Math.pow(10, 11);
	public Map<Method, Long> methodsInstructions;

	public AbstractApplication() {
		methodsInstructions = new HashMap<>();
	}

}
