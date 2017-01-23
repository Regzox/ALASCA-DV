package fr.upmc.datacenter.providers.resources.interfaces;

/**
 * Interface générale de requêtage
 * 
 * @author Daniel RADEAU
 *
 */

public interface RequestingI {

	/**
	 * Permet la vérification de l'origine de l'élément passé en paramètre.
	 * 
	 * @param o élément à tester
	 * @return vrai, si l'élément est local à ce composant sinon faux
	 * @throws Exception
	 */
	
	boolean isLocal(Object o) throws Exception;	
	
}
