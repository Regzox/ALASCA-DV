package fr.upmc.datacenter.software.admissioncontroller.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.upmc.external.software.applications.AbstractApplication;

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
	public void connectToComputer(String computerURI, String csipURI, String cssdipURI, String cdsdipURI, String ccripURI)
			throws Exception {
		((AdmissionControllerManagementI) this.connector).connectToComputer(computerURI, csipURI, cssdipURI, cdsdipURI, ccripURI);
	}

	@Override
	public String submitApplication() throws Exception {
		return ((AdmissionControllerManagementI) this.connector).submitApplication();
	}
	
	@Override
	public void submitApplication(
			AbstractApplication application,
			Class<?> submissionInterface) throws Exception {
		((AdmissionControllerManagementI) this.connector).submitApplication(application, submissionInterface);
	}
	
	@Override
	public void forceApplicationVMIncrementation() throws Exception {
		((AdmissionControllerManagementI) this.connector).forceApplicationVMIncrementation();
	}

	@Override
	public void stopDynamicStateDataPushing() throws Exception {
		((AdmissionControllerManagementI) this.connector).stopDynamicStateDataPushing();
	}

	@Override
	public void startDynamicStateDataPushing(int milliseconds) throws Exception {
		((AdmissionControllerManagementI) this.connector).startDynamicStateDataPushing(milliseconds);
	}

	@Override
	public void increaseCoreFrequency(String computerURI, String processorURI, Integer coreNo) throws Exception {
		((AdmissionControllerManagementI) this.connector).increaseCoreFrequency(computerURI, processorURI, coreNo);
	}

	@Override
	public void decreaseCoreFrequency(String computerURI, String processorURI, Integer coreNo) throws Exception {
		((AdmissionControllerManagementI) this.connector).decreaseCoreFrequency(computerURI, processorURI, coreNo);
	}

	@Override
	public void increaseProcessorFrenquency(String computerURI, String processorURI) throws Exception {
		((AdmissionControllerManagementI) this.connector).increaseProcessorFrenquency(computerURI, processorURI);
	}

	@Override
	public void decreaseProcessorFrenquency(String computerURI, String processorURI) throws Exception {
		((AdmissionControllerManagementI) this.connector).decreaseProcessorFrenquency(computerURI, processorURI);
	}

	@Override
	public void increaseProcessorsFrenquencies(String computerURI) throws Exception {
		((AdmissionControllerManagementI) this.connector).increaseProcessorsFrenquencies(computerURI);
	}

	@Override
	public void decreaseProcessorsFrenquencies(String computerURI) throws Exception {
		((AdmissionControllerManagementI) this.connector).decreaseProcessorsFrenquencies(computerURI);
	}

	@Override
	public void allocateCores(String computerURI, String avmURI, int cores) throws Exception {
		((AdmissionControllerManagementI) this.connector).allocateCores(computerURI, avmURI, cores);
	}

	@Override
	public void releaseCores(String computerURI, String avmURI, int cores) throws Exception {
		((AdmissionControllerManagementI) this.connector).releaseCores(computerURI, avmURI, cores);
	}

	@Override
	public void increaseAVMs(String dispatcherURI) throws Exception {
		((AdmissionControllerManagementI) this.connector).increaseAVMs(dispatcherURI);
	}

	@Override
	public void decreaseAVMs(String dispatcherURI) throws Exception {
		((AdmissionControllerManagementI) this.connector).decreaseAVMs(dispatcherURI);
	}
}
