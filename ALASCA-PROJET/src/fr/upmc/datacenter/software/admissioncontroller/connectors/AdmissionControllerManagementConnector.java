package fr.upmc.datacenter.software.admissioncontroller.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;

/**
 * Effectue la connexion entre les interfaces requises et offertes de composants 
 * implémentant @link {@link AdmissionControllerManagementI}
 * 
 * @author Daniel RADEAU
 *
 */

public class AdmissionControllerManagementConnector
	extends 
		AbstractConnector
	implements
		AdmissionControllerManagementI
{

	@Override
	public void connectToComputer(String computerURI, String csipURI, String cssdipURI, String cdsdipURI)
			throws Exception {
		((AdmissionControllerManagementI) this.offering).connectToComputer(computerURI, csipURI, cssdipURI, cdsdipURI);
	}

	@Override
	public String submitApplication() throws Exception {
		return ((AdmissionControllerManagementI) this.offering).submitApplication();
	}

	@Override
	public void submitApplication(Class<?> inter) throws Exception {
		((AdmissionControllerManagementI) this.offering).submitApplication(inter);
	}

	@Override
	public void forceApplicationVMIncrementation() throws Exception {
		((AdmissionControllerManagementI) this.offering).forceApplicationVMIncrementation();
	}
}
