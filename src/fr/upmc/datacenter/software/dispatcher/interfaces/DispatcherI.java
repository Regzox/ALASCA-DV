package fr.upmc.datacenter.software.dispatcher.interfaces;

import java.util.List;

import fr.upmc.datacenter.software.interfaces.RequestI;

public interface DispatcherI {
	
	/**
	 * Method where the dispatching strategy is applied to the request capture
	 * @param request
	 * @throws Exception
	 */
	
	void dispatch(RequestI request, Boolean forward) throws Exception;
	
	/**
	 * Method to perform some action on notification capture
	 * @param request
	 * @throws Exception
	 */
	void forward(RequestI request) throws Exception;
	
	/**
	 * To get uri of the less current used RequestSubmissionOutboundPort 
	 * @return
	 */
	
	String lessBusyRequestSubmissionOutboundPortURI();
	
	/**
	 * To get uri of the most current used RequestSubmissionOutboundPort 
	 * @return
	 */
	
	String mostBusyRequestSubmissionOutboundPortURI();
	
	/**
	 * Retourne le score du port de soumission le moins utilisé
	 * @return
	 */
	
	Integer lessBusyRequestSubmissionOutboundPortScore();
	
	/**
	 * Retourne le score du port de soumission le plus utilisé
	 * @return
	 */
	
	Integer mostBusyRequestSubmissionOutboundPortScore();
	
	/**
	 * Tente de déconnecter une AMV, pour cela elle doit avoir épuiser toutes 
	 * ses requêtes en cours de traitement. 
	 * @throws Exception
	 */
	
	void tryToPerformApplicationVMDisconnection() throws Exception;
	
	/** 
	 * Method to generate uri from tag
	 * 
	 * @param tag
	 * @return
	 */
	
	String generateURI(Object tag);
	
	/**
	 * Returns to String the dynamic state of the dispatcher 
	 */

	String dynamicStateToString();
	
	String getURI();
	String getRequestSubmissionInboundPortURI();
	List<String> getRequestSubmissionOutboundPortURIs();
	List<String> getRequestNotificationInboundPortURIs();
	String getRequestNotificationOutboundPortURI();
	String getApplicationVMReleasingNotificationOutboundPortURI();
}
