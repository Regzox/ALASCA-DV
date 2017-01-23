package fr.upmc.datacenter.providers.resources.interfaces;

/**
 * Interface g�n�rale de requ�tage
 * 
 * @author Daniel RADEAU
 *
 */

public interface RequestingI {

	/**
	 * Permet la v�rification de l'origine de l'�l�ment pass� en param�tre.
	 * 
	 * @param o �l�ment � tester
	 * @return vrai, si l'�l�ment est local � ce composant sinon faux
	 * @throws Exception
	 */
	
	boolean isLocal(Object o) throws Exception;	
	
}
