package fr.upmc.datacenter.software.interfaces;

/**
 * Interface de gestion des lib�rations de coeurs 
 * 
 * @author Daniel RADEAU
 *
 */

public interface CoreReleasingNotificationHandlerI {

	/**
	 * Accepte la lib�ration d'un coeur d�tenu par l'AVM.
	 * Cette m�thode n'est appel�e qu'en cas de lib�ration effective du coeur par l'AVM.
	 * Dans le cadre d'une demande de lib�ration de coeur, celle-ci n'est qu'effectivement r�alis�e
	 * qu'au moment de la terminaison de la t�che ce qui peut mettre un certain d�lai en fonction 
	 * de la dur�e de cette m�me t�che.
	 * 
	 * @param avmURI
	 */
	
	void acceptCoreReleasing(String avmURI);
}
