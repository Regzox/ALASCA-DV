package fr.upmc.datacenter.software.dispatcher.interfaces;

import java.util.Map;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.interfaces.TimeStampingI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenter.software.dispatcher.statistics.interfaces.ExponentialAverageI;

public interface DispatcherDynamicStateI 
	extends 
		DataOfferedI.DataI,
		DataRequiredI.DataI,
		TimeStampingI
{
	
	/**
	 * Retourne la map des moyennes exponentielles des temps d'executions par AVM
	 * 
	 * @return
	 */
	
	Map<String, ExponentialAverageI> getExponentialAverages();
	
	/**
	 * Retourne le nombre de requ�tes "du c�t� AVM" par AVM
	 * @return
	 */
	
	Map<String, Integer> getPendingRequests();
	
	/**
	 * Retourne le nombre de requ�tes termin�es par AVM (ne concerne que les requ�tes notifi�es)
	 * @return
	 */
	
	Map<String, Integer> getPerformedRequests();
	
	/**
	 * Retourne le nombre d' {@link ApplicationVM} g�r�es par le {@link Dispatcher}
	 * @return
	 */
	
	Integer avmCount();
	
}
