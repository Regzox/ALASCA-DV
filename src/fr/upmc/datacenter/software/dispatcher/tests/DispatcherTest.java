package fr.upmc.datacenter.software.dispatcher.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;

import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenter.software.dispatcher.connectors.DispatcherManagementConnector;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.datacenter.software.dispatcher.ports.DispatcherManagementOutboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
/**
 * 
 * Pas d'appels de méthode direct passge obligatoire par utilisation des ports ...
 * Passage des uris par ces nouveaux ports ...
 * Il faut manipuler les différentes connexions par les ports
 * 
 * @author Daniel RADEAU
 *
 */

public class DispatcherTest 
	extends AbstractCVM {
	
	public static final String requestGeneratorURI = "requestGenerator";
	
	public static final String RequestGeneratorManagementOutboundPortURI = "rg-mop";
	
		public static final String requestGenerator_ManagementInboundPortURI = "rg-mip";
		public static final String requestGenarator_RequestSubmissionOutboundPortURI = "rg-rsop";
		public static final String requestGenarator_RequestNotificationInboundPortURI = "rg-rnip";
		protected RequestGeneratorManagementOutboundPort rgmop;
	
		public static final String dispatcher_RequestSubmissionInboundPortURI = "d-rsip";
		public static final String dispatcher_RequestNotificationOutboundPortURI = "d-rnop";
	
	public static final String ComputerServicesOutboundPortURI = "c-csop";
	
		public static final String computer_ComputerServicesInboundPortURI = "c-csip";
		public static final String computer_ComputerStaticStateDataInboundPortURI = "c-cssdip";
		public static final String computer_ComputerDynamicStateDataInboundPortURI = "c-cdsdip";
		protected ComputerServicesOutboundPort csop;
	
	public static final String applicationVirtualMachineURI = "applicationVirtualMachine";
	
		public static final String avm_ApplicationVMManagementInboundPortURI = "avm-amig";
		public static final String avm_RequestSubmissionInboundPortURI = "avm-rsip";
		public static final String avm_RequestNotificationOutboundPortURI = "avm-rnop";
	
	public Computer computer;
		
	static DispatcherManagementOutboundPort dmop; 
	public static final String dispatcher_ManagementInboundPort = "dmip";
	ApplicationVM avm, applicationVM;

	public DispatcherTest() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		
		/*
		 *  Création du générateur de requêtes avec 3 ports :
		 * requestGenerator_ManagementInboundPortURI : Pour la gestion externe du générateur (démarrage, stop)
		 * requestGenarator_RequestSubmissionOutboundPortURI : Pour l'émission de requête généré
		 * requestGenarator_RequestNotificationInboundPortURI : Pour la réception de notifications
		*/
		
		RequestGenerator requestGenerator = new RequestGenerator(
				requestGeneratorURI,
				500.0,
				6000000000L,
				requestGenerator_ManagementInboundPortURI,
				requestGenarator_RequestSubmissionOutboundPortURI,
				requestGenarator_RequestNotificationInboundPortURI);
		this.addDeployedComponent(requestGenerator);
		
		/*
		 * Création d'un port de gestion du générateur (appartenant à aucun composant particulier) 
		 * et connexion avec l'entrée de gestion du générateur de requêtes 
		 */
		
		rgmop = new RequestGeneratorManagementOutboundPort(RequestGeneratorManagementOutboundPortURI, new AbstractComponent() {});
		rgmop.publishPort();
		rgmop.doConnection(requestGenerator_ManagementInboundPortURI, RequestGeneratorManagementConnector.class.getCanonicalName());
		
		/*
		 * Création d'un répartiteur de requêtes ayant deux ports :
		 * dispatcher_RequestSubmissionInboundPortURI : Pour la réception d'une requête
		 * dispatcher_RequestNotificationOutboundPortURI : Pour l'émission d'une notification
		 */
		
		Dispatcher dispatcher = new Dispatcher("dispatcher",
				dispatcher_ManagementInboundPort, 
				"applicationVM-releasing-notification-outbound-port");
//		Dispatcher dispatcher = new Dispatcher();
//		dispatcher.plugRequestGenerator(
//				requestGenarator_RequestSubmissionOutboundPortURI,
//				requestGenarator_RequestNotificationInboundPortURI);
		this.addDeployedComponent(dispatcher);
		
		/*
		 * Connexion du port de sortie de requêtes du générateur aux port d'entrée du répartiteur
		 */
		
//		requestGenerator
//			.findPortFromURI(requestGenarator_RequestSubmissionOutboundPortURI)
//			.doConnection(dispatcher_RequestSubmissionInboundPortURI, RequestSubmissionConnector.class.getCanonicalName());
		
		/*
		 * Création d'un ordinateur
		 */
		
		String computerURI = "computer" ;
		int numberOfProcessors = 4 ;
		int numberOfCores = 8 ;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
		admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000) ;	// and at 3 GHz
		Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
		processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips
		computer = new Computer(
							computerURI,
							admissibleFrequencies,
							processingPower,  
							1500,		// Test scenario 1, frequency = 1,5 GHz
//							3000,	// Test scenario 2, frequency = 3 GHz
							1500,		// max frequency gap within a processor
							numberOfProcessors,
							numberOfCores,
							computer_ComputerServicesInboundPortURI,
							computer_ComputerStaticStateDataInboundPortURI,
							computer_ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(computer);
		
		/*
		 * Création d'un port de gestion externe à l'ordinateur et connexion à son port d'entrée 
		 */
		
		csop = new ComputerServicesOutboundPort(ComputerServicesOutboundPortURI, new AbstractComponent() {});
		csop.publishPort();
		csop.doConnection(computer_ComputerServicesInboundPortURI, ComputerServicesConnector.class.getCanonicalName());
		
		/*
		 * Création d'une AVM
		 */
		
		avm = new ApplicationVM(
				applicationVirtualMachineURI,
				avm_ApplicationVMManagementInboundPortURI,
				avm_RequestSubmissionInboundPortURI,
				avm_RequestNotificationOutboundPortURI) ;
		this.addDeployedComponent(avm);
		
		
		
//		avm.toggleLogging();
//		avm.toggleTracing();
		
//		ApplicationVM avm2 = new ApplicationVM(
//				"qkjskqjs",
//				"xxx",
//				"submission",
//				"notification") ;
//		this.addDeployedComponent(avm2);
//		
//		avm2.allocateCores(computer.allocateCores(5));
//		
		String rsip = dispatcher.connectToRequestGenerator(requestGenarator_RequestNotificationInboundPortURI);
		
		System.out.println(dispatcher.findPortFromURI(rsip).getPortURI());
		
		//dispatcher.plugIntoRequestGenerator(requestGenarator_RequestNotificationInboundPortURI);
		RequestSubmissionOutboundPort rsop = (RequestSubmissionOutboundPort) requestGenerator.findPortFromURI(requestGenarator_RequestSubmissionOutboundPortURI);
		rsop.doConnection(rsip, RequestSubmissionConnector.class.getCanonicalName());
		
		RequestNotificationOutboundPort rnop = (RequestNotificationOutboundPort) avm.findPortFromURI(avm_RequestNotificationOutboundPortURI);
		rnop.doConnection(dispatcher.connectToApplicationVM(avm_RequestSubmissionInboundPortURI), RequestNotificationConnector.class.getCanonicalName());
		
		dmop = new DispatcherManagementOutboundPort("OSEF", DispatcherManagementI.class, new AbstractComponent() {});
		dmop.publishPort();
		dmop.doConnection(dispatcher_ManagementInboundPort, DispatcherManagementConnector.class.getCanonicalName());
		
		dispatcher.toggleLogging();
		dispatcher.toggleTracing();
		
//		System.out.println(rsop.getServerPortURI());
//		System.out.println(rsop.getClientPortURI());
//		System.out.println("---------");
//		
//		for (String port : requestGenerator.findInboundPortURIsFromInterface(RequestSubmissionI.class))
//			System.out.println(port);
//		
//		for (String port : requestGenerator.findInboundPortURIsFromInterface(RequestNotificationI.class))
//			System.out.println(port);
//		
//		for (String port : dispatcher.findInboundPortURIsFromInterface(RequestSubmissionI.class))
//			System.out.println(port);
//		
//		for (String port : dispatcher.findInboundPortURIsFromInterface(RequestNotificationI.class))
//			System.out.println(port);
//		
		//dispatcher.connectApplicationVM(applicationVirtualMachineURI, avm_RequestSubmissionInboundPortURI);
			
		
		
//		avm.toggleLogging();
//		avm.toggleTracing();
		
//		dispatcher.plugApplicationVM(avm_RequestSubmissionInboundPortURI, avm_RequestNotificationOutboundPortURI);
//		dispatcher.plugApplicationVM(avm2, "submission", "notification");
		
		super.deploy();
	}
	
	@Override
	public void start() throws Exception {
		super.start();
		
		avm.allocateCores(csop.allocateCores(5));
	}
	
	static final String subIn = "subin";
	static final String notOut = "notout";
	
	public void			testScenario() throws Exception
	{
		// start the request generation in the request generator.
		this.rgmop.startGeneration() ;
		
		// Wait 5 seconds
		Thread.sleep(5000L) ;
		
		applicationVM = new ApplicationVM("FreshAVM", "No management", subIn, notOut);
		addDeployedComponent(applicationVM);
		applicationVM.allocateCores(computer.allocateCores(5));
		
		//Add an AVM in the dispatcher during it work
		String drnipURL = dmop.connectToApplicationVM(subIn);
		
		System.out.println(">>> DRNIP <<<" + drnipURL);
		
		RequestNotificationOutboundPort rnop = (RequestNotificationOutboundPort) applicationVM.findPortFromURI(notOut);
		rnop.doConnection(drnipURL, RequestNotificationConnector.class.getCanonicalName());
		
		applicationVM.start();
		
		Thread.sleep(5000L) ;
		System.out.println(">>> DISCONNECT RQ <<< " + drnipURL);
		
		dmop.disconnectFromApplicationVM();
		
		
		// wait 20 seconds
		Thread.sleep(20000L) ;
		// then stop the generation.
		this.rgmop.stopGeneration() ;
	}
	
	public static void	main(String[] args)
	{
		try {
			final DispatcherTest trg = new DispatcherTest() ;
			trg.deploy() ;
			System.out.println("starting...") ;
			trg.start() ;
			// Execute the chosen request generation test scenario in a
			// separate thread.
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						trg.testScenario() ;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}).start() ;
			// Sleep to let the test scenario execute to completion.
			Thread.sleep(90000L) ;
			// Shut down the application.
			System.out.println("shutting down...") ;
			trg.shutdown() ;
			System.out.println("ending...") ;
			// Exit from Java.
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
