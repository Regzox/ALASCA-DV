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
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.external.software.applications.webserver.HttpRequest;
import fr.upmc.external.software.applications.webserver.WebServer;
import fr.upmc.external.software.applications.webserver.interfaces.WebServerI;
import fr.upmc.javassist.DynamicConnectorFactory;

/**
 * Reprend le même schéma que {@link AdmissionControllerTest} mais cette fois c'est un objet externe qui est créé
 * sous la forme d'une application implémentant un {@link AbstractComponent} possèdant un {@link RequestNotificationInboundPort}
 * et un {@link RequestSubmissionOutboundPort} comme le {@link RequestGenerator}.
 * 
 * L'objet utilisé dans cette simulation est un {@link WebServer} qui peut soumettre des requêtes via son l'interface de 
 * soumission proposé à ses client {@link WebServerI}.
 * 
 * Cette simulation à simplement pour but de tester la génération automatique des connecteurs en fonction des interfaces présentés 
 * à la {@link DynamicConnectorFactory}
 * 
 * @author Daniel RADEAU
 *
 */

@Deprecated /** N'est plus géré avec la refonte du modèle au moment de l'étape 2 **/
public class AdmissionControllerTestJavassist extends AbstractCVM {

	protected final String admissionControllerURI = "admissionController";
	protected final String admissionControllerManagementInboundPortURI = "admissionController-mip";
	protected final String admissionControllerManagementOutboundPortURI = "admissionController-mop";

	protected AdmissionControllerManagementOutboundPort acmop;

	protected Stock computerPark;

	AdmissionController admissionController;
	WebServer webServer;

	public AdmissionControllerTestJavassist() throws Exception {
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

		webServer = new WebServer("web-server", "web-server-rsop", "web-server-rnip");
		addDeployedComponent(webServer);
		
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

	/**
	 * Dans ce scénario très simple, on simule 3 appels client à la page www.nothing.com supossant que chaque appel
	 * requière un nombre fixe d'instruction pour s'executer et recupérer une réponse
	 * 
	 * @throws Exception
	 */
	
	public void scenario() throws Exception {

		acmop.submitApplication(webServer, WebServerI.class);
		webServer.getWebPage(new HttpRequest("simple-page-request-1", 100000000L, "www.nothing.com"));
		webServer.getWebPage(new HttpRequest("simple-page-request-2", 100000000L, "www.nothing.com"));
		webServer.getWebPage(new HttpRequest("simple-page-request-3", 100000000L, "www.nothing.com"));
	
	}

	public void incrementation() throws Exception {
		acmop.forceApplicationVMIncrementation();
	}

	public static void main(String[] args) {
		try {
			final AdmissionControllerTestJavassist target = new AdmissionControllerTestJavassist();
			System.out.println("Deployement ... ");
			target.deploy();
			System.out.println("Starting ... ");
			target.start();


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
