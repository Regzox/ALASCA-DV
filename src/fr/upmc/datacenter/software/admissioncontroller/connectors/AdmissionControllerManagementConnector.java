package fr.upmc.datacenter.software.admissioncontroller.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.upmc.external.software.applications.AbstractApplication;

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
	public void submitApplication(
			AbstractApplication application,
			Class<?> submissionInterface) throws Exception {
		((AdmissionControllerManagementI) this.offering).submitApplication(application, submissionInterface);
	}

	@Override
	public void forceApplicationVMIncrementation() throws Exception {
		((AdmissionControllerManagementI) this.offering).forceApplicationVMIncrementation();
	}

	@Override
	public void stopDynamicStateDataPushing() throws Exception {
		((AdmissionControllerManagementI) this.offering).stopDynamicStateDataPushing();
	}

	@Override
	public void startDynamicStateDataPushing(int milliseconds) throws Exception {
		((AdmissionControllerManagementI) this.offering).startDynamicStateDataPushing(milliseconds);
	}

	@Override
	public void increaseCoreFrequency(String computerURI, String processorURI, Integer coreNo) throws Exception {
		((AdmissionControllerManagementI) this.offering).increaseCoreFrequency(computerURI, processorURI, coreNo);
	}

	@Override
	public void decreaseCoreFrequency(String computerURI, String processorURI, Integer coreNo) throws Exception {
		((AdmissionControllerManagementI) this.offering).decreaseCoreFrequency(computerURI, processorURI, coreNo);
	}

	@Override
	public void increaseProcessorFrenquency(String computerURI, String processorURI) throws Exception {
		((AdmissionControllerManagementI) this.offering).increaseProcessorFrenquency(computerURI, processorURI);
	}

	@Override
	public void decreaseProcessorFrenquency(String computerURI, String processorURI) throws Exception {
		((AdmissionControllerManagementI) this.offering).decreaseProcessorFrenquency(computerURI, processorURI);
	}

	@Override
	public void increaseProcessorsFrenquencies(String computerURI) throws Exception {
		((AdmissionControllerManagementI) this.offering).increaseProcessorsFrenquencies(computerURI);
	}

	@Override
	public void decreaseProcessorsFrenquencies(String computerURI) throws Exception {
		((AdmissionControllerManagementI) this.offering).decreaseProcessorsFrenquencies(computerURI);
	}

	@Override
	public void allocateCores(String computerURI, String avmURI, int cores) throws Exception {
		
	}

	@Override
	public void releaseCores(String compterURI, String avmURI, int cores) throws Exception {

	}

//	@Override
//	public void increaseAVMs(String dispatcherURI) {
//		
//	}
//
//	@Override
//	public void decreaseAVMs(String dispatcherURI) {
//
//	}
}
