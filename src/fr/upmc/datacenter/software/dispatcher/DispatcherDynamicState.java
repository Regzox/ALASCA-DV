package fr.upmc.datacenter.software.dispatcher;

import java.util.HashMap;
import java.util.Map;

import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherDynamicStateI;
import fr.upmc.datacenter.software.dispatcher.statistics.interfaces.ExponentialAverageI;

public class DispatcherDynamicState 
	implements 
		DispatcherDynamicStateI
{
	
	private static final long serialVersionUID = 4949083977082476764L;
	
	protected String dispatcherURI;
	protected long timestamp;
	protected String timestamperIP;
	protected Map<String, ExponentialAverageI> exponentialAverages;
	protected Map<String, Integer> pendingRequests;
	protected Map<String, Integer> performedRequests;
	
	public DispatcherDynamicState(
			String dispatcherURI,
			Map<String, ExponentialAverageI> exponentialAverages,
			Map<String, Integer> pendingsRequests,
			Map<String, Integer> performedRequests) 
	{
		super();
		this.exponentialAverages = new HashMap<String, ExponentialAverageI>(exponentialAverages);
		this.pendingRequests = pendingsRequests;
		this.performedRequests = performedRequests;
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
	public Map<String, ExponentialAverageI> getExponentialAverages() {
		return exponentialAverages;
	}

	@Override
	public Map<String, Integer> getPendingRequests() {
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
