package fr.upmc.datacenter.software.dispatcher.interfaces;

import java.util.List;
import java.util.Map;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.interfaces.TimeStampingI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenter.software.dispatcher.statistics.ExponentialAverage;

public interface DispatcherDynamicStateI 
	extends 
		DataOfferedI.DataI,
		DataRequiredI.DataI,
		TimeStampingI
{
	/**
	 * Retourne l'URI du {@link Dispatcher} émetteur
	 * 
	 * @return
	 */
	
	String getDispatcherURI();
	
	/**
	 * Retourne la map des moyennes exponentielles des temps d'executions par AVM
	 * 
	 * @return
	 */
	
	Map<String, ExponentialAverage> getExponentialAverages();
	
	/**
	 * Retourne les requêtes par AVM
	 * @return
	 */
	
	Map<String, List<String>> getPendingRequests();
	
	/**
	 * Retourne le nombre de requêtes terminées par AVM (ne concerne que les requêtes notifiées)
	 * @return
	 */
	
	Map<String, Integer> getPerformedRequests();
	
	/**
	 * Retourne le nombre d' {@link ApplicationVM} gérées par le {@link Dispatcher}
	 * @return
	 */
	
	Integer avmCount();
	
}
