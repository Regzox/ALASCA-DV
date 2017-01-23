package fr.upmc.datacenter.software.dispatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherDynamicStateI;
import fr.upmc.datacenter.software.dispatcher.statistics.ExponentialAverage;

/**
 * Bloc de donnée représentatif de l'état dynamique du répartiteur de requêtes
 * 
 * @author Daniel RADEAU
 *
 */

public class DispatcherDynamicState 
	implements 
		DispatcherDynamicStateI
{
	
	private static final long serialVersionUID = 4949083977082476764L;
	
	protected String dispatcherURI;
	protected long timestamp;
	protected String timestamperIP;
	protected Map<String, ExponentialAverage> exponentialAverages;
	protected Map<String, List<String>> pendingRequests;
	protected Map<String, Integer> performedRequests;
	
	public DispatcherDynamicState(
			String dispatcherURI,
			Map<String, ExponentialAverage> exponentialAverages,
			Map<String, List<String>> pendingsRequests,
			Map<String, Integer> performedRequests) 
	{
		this.dispatcherURI = dispatcherURI;
		this.exponentialAverages = new HashMap<>(exponentialAverages);
		this.pendingRequests = new HashMap<>(pendingsRequests);
		this.performedRequests = new HashMap<>(performedRequests);
	}
	
	@Override
	public long getTimeStamp() {
		return timestamp;
	}

	@Override
	public String getTimeStamperId() {
		return timestamperIP;
	}

	@Override
	public String getDispatcherURI() {
		return dispatcherURI;
	}
	
	@Override
	public Map<String, ExponentialAverage> getExponentialAverages() {
		return exponentialAverages;
	}

	@Override
	public Map<String, List<String>> getPendingRequests() {
		return pendingRequests;
	}

	@Override
	public Map<String, Integer> getPerformedRequests() {
		return performedRequests;
	}

	@Override
	public Integer avmCount() {
		return exponentialAverages.size();
	}

}
