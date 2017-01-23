package fr.upmc.datacenter.providers.resources.logical.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;

/**
 * Interface de manipulation du retour de notification. Là où la notification sera retransmise aux autres {@link LogicalResourceProvider}
 * ou bien ditribuée au composant client initiateur de la demande.
 * 
 * @author Daniel RADEAU
 *
 */

public interface LogicalResourcesProviderCoreReleasingNotifyBackHandlerI extends RequiredI, OfferedI {

	/**
	 * Accepte le retour de notification de la part d'un autre {@link LogicalResourceProvider}
	 * 
	 * @param requesterUri uri du demendeur de l'action notifiée
	 * @param answererUri uri de l'émetteur de la notification
	 * @param aavm jeton d'allocation de machine virtuelle en question
	 * @param ac jeton d'allocation de coeur libéré
	 * @throws Exception
	 */
	
	void acceptBackCoreReleasing(String requesterUri, String answererUri, AllocatedApplicationVM aavm, AllocatedCore ac) throws Exception;
	
}
