package fr.upmc.datacenter.software.admissioncontroller.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.admissioncontroller.AdmissionController;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.upmc.external.software.applications.AbstractApplication;

/**
 * Port d'entrée de management du contrôleur d'admission. 
 * 
 * @author Daniel RADEAU
 *
 */

public class AdmissionControllerManagementInboundPort 
extends 
AbstractInboundPort
implements
AdmissionControllerManagementI
{

	private static final long serialVersionUID = -1708668634939556597L;

	public AdmissionControllerManagementInboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public AdmissionControllerManagementInboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void connectToComputer(final String computerURI, final String csipURI, final String cssdipURI, final String cdsdipURI)
			throws Exception {
		final AdmissionController admissionController = (AdmissionController) this.owner;

		admissionController.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				admissionController.connectToComputer(computerURI, csipURI, cssdipURI, cdsdipURI);
				return null;
			}


		});
	}

	@Override
	public String submitApplication() throws Exception {
		final AdmissionController admissionController = (AdmissionController) this.owner;

		return admissionController.handleRequestSync(new ComponentService<String>() {

			@Override
			public String call() throws Exception {
				return admissionController.submitApplication();
			}


		});
	}

	@Override
	public void submitApplication(
			AbstractApplication application,
			Class<?> submissionInterface) throws Exception {
		final AdmissionController admissionController = (AdmissionController) this.owner;

		admissionController.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				admissionController.submitApplication(application, submissionInterface);
				return null;
			}


		});
	}

	@Override
	public void forceApplicationVMIncrementation() throws Exception {
		final AdmissionController admissionController = (AdmissionController) this.owner;

		admissionController.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				admissionController.forceApplicationVMIncrementation();
				return null;
			}


		});
	}

	@Override
	public void stopDynamicStateDataPushing() throws Exception {
		final AdmissionController admissionController = (AdmissionController) this.owner;

		admissionController.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				admissionController.stopDynamicStateDataPushing();
				return null;
			}


		});
	}

	@Override
	public void startDynamicStateDataPushing(int milliseconds) throws Exception {
		final AdmissionController admissionController = (AdmissionController) this.owner;

		admissionController.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				admissionController.startDynamicStateDataPushing(milliseconds);
				return null;
			}


		});
	}

	@Override
	public void increaseCoreFrequency(String computerURI, String processorURI, Integer coreNo) throws Exception {
		final AdmissionController admissionController = (AdmissionController) this.owner;

		admissionController.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				admissionController.increaseCoreFrequency(computerURI, processorURI, coreNo);
				return null;
			}

		});
	}

	@Override
	public void decreaseCoreFrequency(String computerURI, String processorURI, Integer coreNo) throws Exception {
		final AdmissionController admissionController = (AdmissionController) this.owner;

		admissionController.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				admissionController.decreaseCoreFrequency(computerURI, processorURI, coreNo);
				return null;
			}

		});
	}

	@Override
	public void increaseProcessorFrenquency(String computerURI, String processorURI) throws Exception {
		final AdmissionController admissionController = (AdmissionController) this.owner;

		admissionController.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				admissionController.increaseProcessorFrenquency(computerURI, processorURI);
				return null;
			}

		});
	}

	@Override
	public void decreaseProcessorFrenquency(String computerURI, String processorURI) throws Exception {
		final AdmissionController admissionController = (AdmissionController) this.owner;

		admissionController.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				admissionController.decreaseProcessorFrenquency(computerURI, processorURI);
				return null;
			}

		});
	}

	@Override
	public void increaseProcessorsFrenquencies(String computerURI) throws Exception {
		final AdmissionController admissionController = (AdmissionController) this.owner;

		admissionController.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				admissionController.increaseProcessorsFrenquencies(computerURI);
				return null;
			}


		});
	}

	@Override
	public void decreaseProcessorsFrenquencies(String computerURI) throws Exception {
		final AdmissionController admissionController = (AdmissionController) this.owner;

		admissionController.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				admissionController.decreaseProcessorsFrenquencies(computerURI);
				return null;
			}

		});
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
