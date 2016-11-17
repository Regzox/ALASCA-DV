package fr.upmc.datacenter.software.admissioncontroller.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;

/**
 * Port de sortie du contrôleur d'admission. Permet l'appel des méthodes offertes par les composants disposant 
 * d'un port d'entrée. 
 * 
 * @author Daniel RADEAU
 *
 */

public class AdmissionControllerManagementOutboundPort 
	extends 
		AbstractOutboundPort
	implements
		AdmissionControllerManagementI
{

	public AdmissionControllerManagementOutboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public AdmissionControllerManagementOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void connectToComputer(String computerURI, String csipURI, String cssdipURI, String cdsdipURI)
			throws Exception {
		((AdmissionControllerManagementI) this.connector).connectToComputer(computerURI, csipURI, cssdipURI, cdsdipURI);
	}

	@Override
	public String submitApplication() throws Exception {
		return ((AdmissionControllerManagementI) this.connector).submitApplication();
	}
	
	@Override
	public void submitApplication(Class<?> inter) throws Exception {
		((AdmissionControllerManagementI) this.connector).submitApplication(inter);
	}
	
	@Override
	public void forceApplicationVMIncrementation() throws Exception {
		((AdmissionControllerManagementI) this.connector).forceApplicationVMIncrementation();
	}
}
