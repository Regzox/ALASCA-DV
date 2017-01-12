package fr.upmc.datacenter.software.admissioncontroller_old.tests;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.computer.stock.Stock;
import fr.upmc.datacenter.hardware.processor.model.Model;
import fr.upmc.datacenter.software.admissioncontroller_old.AdmissionController;
import fr.upmc.datacenter.software.admissioncontroller_old.connectors.AdmissionControllerManagementConnector;
import fr.upmc.datacenter.software.admissioncontroller_old.interfaces.AdmissionControllerManagementI;
import fr.upmc.datacenter.software.admissioncontroller_old.ports.AdmissionControllerManagementOutboundPort;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class AdmissionControllerTest extends AbstractCVM {

	public static long emissionDelay = 40 * 1000L;
	public static long simulationDelay = 60 * 1000L;
	
	protected final String admissionControllerURI = "admissionController";
	protected final String admissionControllerManagementInboundPortURI = "admissionController-mip";
	protected final String admissionControllerManagementOutboundPortURI = "admissionController-mop";

	protected AdmissionControllerManagementOutboundPort acmop;

	protected Stock computerPark;

	AdmissionController admissionController;

	public AdmissionControllerTest() throws Exception {
		super();

		/**
		 * Logging mode actif
		 */

		//AdmissionController.LOGGING_DISPATCHER = true;
		Dispatcher.DYNAMIC_STATE_DATA_DISPLAY = true;

		/**
		 * Instanciation de 2 ordinateurs muni de 2 processeurs I7-6700K
		 */

		computerPark = new Stock("i7-6700k-park", 2, 2, Model.I7_6700K);
	}

	@Override
	public void deploy() throws Exception {

		admissionController = new AdmissionController(
				admissionControllerURI, 
				admissionControllerManagementInboundPortURI
				);
		addDeployedComponent(admissionController);

		admissionController.toggleLogging();
		admissionController.toggleTracing();

		acmop = new AdmissionControllerManagementOutboundPort(
				admissionControllerURI, 
				AdmissionControllerManagementI.class, 
				new AbstractComponent() {}
				);
		acmop.publishPort();
		acmop.doConnection(admissionControllerManagementInboundPortURI, AdmissionControllerManagementConnector.class.getCanonicalName());	

		super.deploy();
	}
	
	@Override
	public void start() throws Exception {
		super.start();
			
		for (String computerURI : computerPark.getComputersURI()) {
			
			acmop.connectToComputer(
					computerURI, 
					computerPark.getComputerServicesInboundPortURIMap().get(computerURI), 
					computerPark.getComputerStaticStateDataInboundPortURIMap().get(computerURI),
					computerPark.getComputerDynamicStateDataInboundPortURIMap().get(computerURI),
					computerPark.getComputerCoreReleasingInboundPortURIMap().get(computerURI)
					);
			
		}
		
		
	}

	@Override
	public void shutdown() throws Exception {
		acmop.doDisconnection();
		super.shutdown();
	}

	public void scenario() throws Exception {

		String requestGeneratorManagementOutboundPortURI = acmop.submitApplication();
		RequestGeneratorManagementOutboundPort rgmop = (RequestGeneratorManagementOutboundPort) admissionController.findPortFromURI(requestGeneratorManagementOutboundPortURI);
		rgmop.startGeneration();
		Thread.sleep(emissionDelay);
		rgmop.stopGeneration();

		System.out.println(rgmop.getServerPortURI() + " ENDED");

	}

	public void incrementation() throws Exception {
		acmop.forceApplicationVMIncrementation();
	}

	public static void main(String[] args) {
		try {
			final AdmissionControllerTest target = new AdmissionControllerTest();
			System.out.println("Deployement ... ");
			target.deploy();
			System.out.println("Starting ... ");
			target.start();


			/**
			 * Tentative de 2 lancements du scenario en parall�le.
			 */

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						target.scenario() ;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}).start() ;

//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						target.scenario() ;
//					} catch (Exception e) {
//						throw new RuntimeException(e) ;
//					}
//				}
//			}).start() ;

			/**
			 * Lancement d'une incr�mentation sur le contr�leur d'admission
			 */

//			Thread.sleep(5000L);
//			
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						target.incrementation();
//					} catch (Exception e) {
//						throw new RuntimeException(e) ;
//					}
//				}
//			}).start() ;

			Thread.sleep(simulationDelay);

			System.out.println("Stopping ... ");
			target.shutdown();
			System.out.println("End");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
