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
	 */
	String getWebPage(HttpRequestI request);
	
	
	/**
	 * Simule une verification de connexion
	 * 
	 * @param request
	 * @return
	 */
	Boolean checkConnection(HttpRequestI request);
	
}
