package fr.upmc.datacenter.providers.resources.logical.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;

/**
 * Interface de retour de notification.
 * 
 * Cette interface permet la mise en place du mécanisme le plus lourd en terme de communication au sein des {@link LogicalResourceProvider}.
 * Au moment d'une demande de libération de coeurs de la part d'un composant utilisant le port de services du {@link LogicalResourceProvider}
 * auquel il est connecté, cette libération va nécessiter un certain délai marqué par le fait qu'un coeur doit nécessairement avoir terminé
 * son traitement en cours avant de pouvoir être libéré. 
 * Cependant pour des raisons de cohérence du système, il est nécessaire d'informer
 * le composant initiateur que cette libération a bien eu lieu. 
 * Comme les fournisseurs sont connectés en anneau, la resource détenue par le composant 
 * initiateur peut ne par appartenir à sont fournisseur direct (basculement des resources non utilisées pour les
 * applications qui en requière). On se retrouve donc à demander une action sur une machine virtuelle non détenue par notre fournisseur
 * direct. Une telle demande va engendrer une recherche parmis les participants de l'anneau afin de trouver le détenteur de la machine
 * virtuelle qui lui seul pourra réaliser une demande libération de coeur auprès de celle-ci. Une libération de coeur de la machine
 * virtuelle va permettre la génération d'une notification sur l'effectivité de la libération et devra réaliser le chemin inverse pour être
 * transmise au composant initiateur. Un retour de notification non direct est invisible de la part du composant initiateur mais pourra éventuellement
 * prendre un temps non négligeable dans le cas d'une répartition multi-CVM à travers un réseau.
 * 
 * @author Daniel RADEAU
 *
 */

public interface LogicalResourcesProviderCoreReleasingNotifyBackI extends RequiredI, OfferedI {

	/**
	 * Retour de notification de la part d'un {@link LogicalResourceProvider} distant
	 * 
	 * @param requesterUri du {@link LogicalResourceProvider} demandeur de l'action
	 * @param answererUri du {@link LogicalResourceProvider} détenteur de la machine virtuelle
	 * @param aavm jeton d'allocation de la machine virtuelle en question
	 * @param ac jeton de coeur désalloué
	 * @throws Exception
	 */
	
	void notifyBackCoreReleasing(String requesterUri, String answererUri, AllocatedApplicationVM aavm, AllocatedCore ac) throws Exception;
	
}
