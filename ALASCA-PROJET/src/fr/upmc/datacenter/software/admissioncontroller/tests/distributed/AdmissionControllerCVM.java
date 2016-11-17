package fr.upmc.datacenter.software.admissioncontroller.tests.distributed;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.datacenter.software.admissioncontroller.AdmissionController;
import fr.upmc.datacenter.software.admissioncontroller.connectors.AdmissionControllerManagementConnector;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementOutboundPort;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenter.software.enumerations.Tag;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class AdmissionControllerCVM extends AbstractDistributedCVM {

	AdmissionController admissionController;
	AdmissionControllerManagementOutboundPort acmop;
	
	final String admissionControllerURI = "ac";
	final String admissionControllerManagementInboundPortURI = "acmip";
	
	final Integer stockCount = 10;
	final String stockURI = "stock";
	
	public AdmissionControllerCVM(String[] args) throws Exception {
		super(args);
	}
	
	@Override
	public void initialise() throws Exception {
		
		Dispatcher.DYNAMIC_STATE_DATA_DISPLAY = true;
		
		super.initialise();
	}
	
	@Override
	public void instantiateAndPublish() throws Exception {
		
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
		
		super.instantiateAndPublish();
	}
	
	@Override
	public void interconnect() throws Exception {
				
		super.interconnect();
	}
	
	@Override
	public void start() throws Exception {
		
		admissionController.start();
		
		super.start();
	}
	
	@Override
	public void shutdown() throws Exception {
		acmop.doDisconnection();
		super.shutdown();
	}
	
	public void scenario() throws Exception {

		String requestGeneratorManagementOutboundPortURI = acmop.submitApplication();
		RequestGeneratorManagementOutboundPort rgmop = (RequestGeneratorManagementOutboundPort) admissionController.findPortFromURI(requestGeneratorManagementOutboundPortURI);
		System.out.println(rgmop.getServerPortURI() + " BEGIN");
		rgmop.startGeneration();
		Thread.sleep(10000L);
		rgmop.stopGeneration();
		System.out.println(rgmop.getServerPortURI() + " ENDED");

	}

	public void incrementation() throws Exception {
		
		acmop.forceApplicationVMIncrementation();
		
	}
	
	public void dynamicConnect() throws Exception {
		
		for (int i = 1 ; i <= stockCount; i++) {
			String computerURI = stockURI + '_' + Tag.COMPUTER + '_' + i;
			String computerServicesInboundPortURI = stockURI + '_' + Tag.COMPUTER_SERVICES_INBOUND_PORT + '_' + i;
			String computerStaticStateDataInboundPortURI = stockURI + '_' + Tag.COMPUTER_STATIC_STATE_DATA_INBOUND_PORT + '_' + i;
			String computerDynamicStateDataInboundPortURI = stockURI + '_' + Tag.COMPUTER_DYNAMIC_STATE_DATA_INBOUND_PORT + '_' + i;
			
			System.out.println(computerURI);
			System.out.println(computerServicesInboundPortURI);
			System.out.println(computerStaticStateDataInboundPortURI);
			System.out.println(computerDynamicStateDataInboundPortURI);
			
			acmop.connectToComputer(
					computerURI, 
					computerServicesInboundPortURI, 
					computerStaticStateDataInboundPortURI, 
					computerDynamicStateDataInboundPortURI);
		}
	}
	
	public static void	main(String[] args)
	{
		try {
			final AdmissionControllerCVM target = new AdmissionControllerCVM(args);
			System.out.println("Deployement ... ");
			target.deploy();
			System.out.println("Starting ... ");
			target.start();

			target.dynamicConnect();
			
			/**
			 * Tentative de 2 lancements du scenario en parallèle.
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

			/**
			 * Lancement d'une incrémentation sur le contrôleur d'admission
			 */

			Thread.sleep(5000L);
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						target.incrementation();
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}).start() ;

			Thread.sleep(30000L);

			System.out.println("Stopping ... ");
			target.shutdown();
			System.out.println("End");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
