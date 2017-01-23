package fr.upmc.datacenter.providers.resources.logical.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;

/**
 * Interface de retour de notification.
 * 
 * Cette interface permet la mise en place du m�canisme le plus lourd en terme de communication au sein des {@link LogicalResourceProvider}.
 * Au moment d'une demande de lib�ration de coeurs de la part d'un composant utilisant le port de services du {@link LogicalResourceProvider}
 * auquel il est connecter, cette lib�ration va n�cessiter un certain d�lai marqu� par le fait qu'un coeur doit n�cessairement avoir terminer
 * son traitement en cours avant de pouvoir �tre lib�r�. 
 * Cependant pour des raisons des coh�rences du syst�me il est n�cessaire d'informer
 * le composant initiateur que cette lib�ration a bien eu lieu. 
 * Comme les fournisseurs sont connect�s en anneau, la resource d�tenue par le composant 
 * initiateur peut ne par appartenir � sont fournisseur direct (basculement des resources non utilis�es pour les
 * applications qui en requi�re). On se retrouve donc � demander une action sur une machine virtuelle non d�tenue par notre fournisseur
 * direct. Une telle demande va engendrer une recherche parmis les participant de l'anneau afin de trouver le d�tenteur de la machine
 * virtuelle qui lui seul pourra r�aliser une demande lib�ration de coeur aupr�s de celle-ci. Une lib�ration de coeur par machine
 * virtuelle va permettre la g�n�ration d'une notification sur l'effectivit� de la lib�ration et devra r�aliser le chemin inverse pour �tre
 * transmise au composant initiateur. Un retour de notification non directe est invisible de la part du composant initiateur mais pourra �ventuellement
 * prendre un temps non n�gligeable dans le cas d'une r�partition multi-CVM � travers un r�seau.
 * 
 * @author Daniel RADEAU
 *
 */

public interface LogicalResourcesProviderCoreReleasingNotifyBackI extends RequiredI, OfferedI {

	/**
	 * Retour de notification de la part d'un {@link LogicalResourceProvider} distant
	 * 
	 * @param requesterUri du {@link LogicalResourceProvider} demandeur de l'action
	 * @param answererUri du {@link LogicalResourceProvider} d�tenteur de la machine virtuelle
	 * @param aavm jeton d'allocation de la machine virtuelle en question
	 * @param ac jeton de coeur d�sallou�
	 * @throws Exception
	 */
	
	void notifyBackCoreReleasing(String requesterUri, String answererUri, AllocatedApplicationVM aavm, AllocatedCore ac) throws Exception;
	
}
