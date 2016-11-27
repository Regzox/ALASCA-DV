package fr.upmc.datacenter.software.dispatcher.interfaces;

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
	 * Retourne le score du port de soumission le moins utilis�
	 * @return
	 */
	
	Integer lessBusyRequestSubmissionOutboundPortScore();
	
	/**
	 * Retourne le score du port de soumission le plus utilis�
	 * @return
	 */
	
	Integer mostBusyRequestSubmissionOutboundPortScore();
	
	/**
	 * Tente de d�connecter une AMV, pour cela elle doit avoir �puiser toutes 
	 * ses requ�tes en cours de traitement. 
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
}
