package fr.upmc.datacenter.software.admissioncontroller.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.admissioncontroller.AdmissionController;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;

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
	public void submitApplication(final Class<?> inter) throws Exception {
		final AdmissionController admissionController = (AdmissionController) this.owner;

		admissionController.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				admissionController.submitApplication(inter);
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


}
