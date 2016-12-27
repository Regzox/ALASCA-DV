package fr.upmc.external.software.applications.webserver.interfaces;

/**
 * Interface de soumission des requêtes propre au serveur web
 * 
 * @author Daniel RADEAU
 *
 */

public interface WebServerI {

	/**
	 * Simule une demande page web
	 * 
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	String getWebPage(HttpRequestI request) throws Exception;
	
	
	/**
	 * Simule une verification de connexion
	 * 
	 * @param request
	 * @return
	 */
	Boolean checkConnection(HttpRequestI request) throws Exception;
	
}
