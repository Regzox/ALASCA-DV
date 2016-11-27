package fr.upmc.datacenter.software.admissioncontroller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.interfaces.DataRequiredI.PullI;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computer.stock.Stock;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.ComputerDynamicState;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processor.model.Model;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI.ControlledPullI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementInboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenter.software.dispatcher.connectors.DispatcherManagementConnector;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.datacenter.software.dispatcher.ports.DispatcherManagementOutboundPort;
import fr.upmc.datacenter.software.enumerations.Tag;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

/**
 * Le {@link AdmissionController} a pour r�le de r�cup�rer les demandes clientes
 * d'h�bergement d'applications. Pour cela il dispose d'un parc informatique {@link Stock}
 * mettant � sa disposition un certain nombre d'ordinateurs {@link Computer} compos�s d'un certain
 * nombre de processeurs {@link Processor} disposant d'une puissance de calcul bas�e sur des mod�les
 * existant ou fictifs de processeurs {@link Model}.
 * 
 * @author Daniel RADEAU
 *
 */

public class AdmissionController
	extends 
		AbstractComponent
	implements 
		AdmissionControllerI,
		AdmissionControllerManagementI,
		ComputerStateDataConsumerI
{		
	public static boolean LOGGING_ALL = false;
	public static boolean LOGGING_REQUEST_GENERATOR = false;
	public static boolean LOGGING_APPLICATION_VM = false;
	public static boolean LOGGING_DISPATCHER = false;
	
	protected String uri;

	protected Map<String, String> computerServicesOutboundPortMap;
	protected Map<String, String> computerStaticStateDataOutboundPortMap;
	protected Map<String, String> computerDynamicStateDataOutboundPortMap;

	/**
	 * Construction d'un {@link AdmissionController} ayant pour nom <em>uri</em> et
	 * disposant d'un port de contr�le offert {@link AdmissionControllerManagementInboundPort}
	 * 
	 * @param uri
	 * @param AdmissionControllerManagementInboundPortURI
	 * @throws Exception
	 */
	
	public AdmissionController(String uri, String AdmissionControllerManagementInboundPortURI) throws Exception {
		super(1, 1);

		this.uri = uri;

		/**
		 * Cr�ation du port d'entr�e de management du contr�leur d'admission
		 */

		if (!offeredInterfaces.contains(AdmissionControllerManagementI.class))
			addOfferedInterface(AdmissionControllerManagementI.class);
		
		AdmissionControllerManagementInboundPort acmip = new AdmissionControllerManagementInboundPort(AdmissionControllerManagementInboundPortURI, AdmissionControllerManagementI.class, this);
		addPort(acmip);
		acmip.publishPort();
		
		computerServicesOutboundPortMap = new HashMap<>();
		computerStaticStateDataOutboundPortMap = new HashMap<>();
		computerDynamicStateDataOutboundPortMap = new HashMap<>();
	}


	@Override
	public void connectToComputer(String computerURI, String csipURI, String cssdipURI, String cdsdipURI) throws Exception {

		/**
		 * Cr�ation d'un port de sortie ComputerServicesOutboundPort
		 * Connexion au port d'entr�e ComputerServicesInboundPort
		 */
		
		if (!requiredInterfaces.contains(ComputerServicesI.class))
			addRequiredInterface(ComputerServicesI.class);
		
		String csopURI = generateURI(Tag.COMPUTER_SERVICES_OUTBOUND_PORT);
		ComputerServicesOutboundPort csop = new ComputerServicesOutboundPort(csopURI, this);
		addPort(csop);
		csop.publishPort();
		csop.doConnection(csipURI, ComputerServicesConnector.class.getCanonicalName());

		logMessage(csopURI + " created and connected to " + csipURI);

		if (!requiredInterfaces.contains(PullI.class))
			addRequiredInterface(PullI.class);
		
		String cssdopURL = generateURI(Tag.COMPUTER_STATIC_STATE_DATA_OUTBOUND_PORT);
		ComputerStaticStateDataOutboundPort cssdop = new ComputerStaticStateDataOutboundPort(cssdopURL, this, computerURI);
		System.out.println(isInterface(cssdop.getImplementedInterface()));
		addPort(cssdop);
		cssdop.publishPort();
		cssdop.doConnection(cssdipURI, DataConnector.class.getCanonicalName());

		logMessage(cssdopURL + " created and connected to " + cssdipURI);
		
		if (!requiredInterfaces.contains(ControlledPullI.class))
			addRequiredInterface(ControlledPullI.class);
		
		String cdsdopURI = generateURI(Tag.COMPUTER_DYNAMIC_STATE_DATA_OUTBOUND_PORT);
		ComputerDynamicStateDataOutboundPort cdsdop = new ComputerDynamicStateDataOutboundPort(cdsdopURI, this, computerURI);
		addPort(cdsdop);
		cdsdop.publishPort();
		cdsdop.doConnection(cdsdipURI, ControlledDataConnector.class.getCanonicalName());

		logMessage(cdsdopURI + " created and connected to " + cdsdipURI);
		logMessage("");
		/**
		 * Etrangement n'impl�mente aucune interface permettant de 
		 * retrouver la liste des uri des ComputerDynamicStateDataOutboundPort ...
		 * Donc ajout � une map
		 */

		computerServicesOutboundPortMap.put(computerURI, csopURI);
		computerStaticStateDataOutboundPortMap.put(computerURI, cssdopURL);
		computerDynamicStateDataOutboundPortMap.put(computerURI, cdsdopURI);
	}


	@Override
	public String generateURI(Object tag) {
		return uri + '_' + tag + '_' + Math.abs(new Random().nextInt());
	}


	@Override
	public String submitApplication() throws Exception {

		/**
		 * Recherche parmis les ordinateurs disponibles du premier candidat poss�dant les ressources suffisantes
		 * � l'allocation d'une AVM de taille arbitraire.
		 * Dans un premier temps nous allons salement allouer un processeur entier par AVM.
		 * 
		 */

		/**
		 * Parcours de tous les ordinateurs mis � disposition � la recherche d'un processeur � allouer
		 */

		String computerURI = null;
		int numberOfCores = 0; 
		
		for (String cdsdopURI : computerDynamicStateDataOutboundPortMap.values()) {
			ComputerDynamicStateDataOutboundPort cdsdop = (ComputerDynamicStateDataOutboundPort) findPortFromURI(cdsdopURI);
			ComputerDynamicState data = (ComputerDynamicState) cdsdop.request();
			boolean[][] processorMap = data.getCurrentCoreReservations();

			/**
			 * Parcours de tous les processeurs de l'ordinateur en cours
			 */
			
			for (int proccessorIndex = 0; proccessorIndex < processorMap.length; proccessorIndex++) {
				
				/**
				 * Parcours de tous les coeurs du processeur en cours
				 */
				
				for (boolean coreAllocated : processorMap[proccessorIndex]) {
					
					/**
					 * A la d�tection du premier coeur non allou�, nous tenons notre machine pour l'allocation
					 */
					
					if (!coreAllocated) {
						computerURI = data.getComputerURI();
						numberOfCores =  processorMap[proccessorIndex].length;
					}
				}
			}
		}
		
		/**
		 * Dans la cas o� nous n'avons pas trouv� de d'ordinateurs de libres, alors computerURI est toujours nulle
		 * et nous ne pouvons pas donner suite � la demande d'hebergement d'application
		 */
		
		if (computerURI == null && numberOfCores == 0) {
			logMessage("Ressources leak, impossible to welcome the application");
			throw new Exception("Ressources leak, impossible to welcome the application");
		}
		
		logMessage("Ressources found, " + numberOfCores +  " cores on computer : " + computerURI);
		
		/**
		 * D�claration d'un nouveau g�n�rateur de requetes
		 */
		
		final String requestGeneratorURI = generateURI(Tag.REQUEST_GENERATOR);
		double meanInterArrivalTime = 500;
		long meanNumberOfInstructions = 10000000000L;
		final String requestGeneratorManagementInboundPortURI = generateURI(Tag.REQUEST_GENERATOR_MANAGEMENT_INBOUND_PORT);
		final String requestGeneratorRequestSubmissionOutboundPortURI = generateURI(Tag.REQUEST_SUBMISSION_OUTBOUND_PORT);
		final String requestGeneratorRequestNotificationInboundPortURI = generateURI(Tag.REQUEST_NOTIFICATION_INBOUND_PORT);
		
		RequestGenerator requestGenerator = new RequestGenerator(
				requestGeneratorURI, 
				meanInterArrivalTime, 
				meanNumberOfInstructions, 
				requestGeneratorManagementInboundPortURI, 
				requestGeneratorRequestSubmissionOutboundPortURI, 
				requestGeneratorRequestNotificationInboundPortURI
				);
		AbstractCVM.theCVM.addDeployedComponent(requestGenerator);
		
		if (LOGGING_ALL | LOGGING_REQUEST_GENERATOR) {
			requestGenerator.toggleLogging();
			requestGenerator.toggleTracing();
		}
		
		requestGenerator.start();
		
		/**
		 * D�claration d'une nouvelle applicationVM
		 */
		
		final String applicationVMURI = generateURI(Tag.APPLICATION_VM);
		final String applicationVMManagementInboundPortURI = generateURI(Tag.APPLICATION_VM_MANAGEMENT_INBOUND_PORT);
		final String applicationVMRequestSubmissionInboundPortURI = generateURI(Tag.REQUEST_SUBMISSION_INBOUND_PORT);
		final String applicationVMRequestNotificationOutboundPortURI = generateURI(Tag.REQUEST_NOTIFICATION_OUTBOUND_PORT);
		
		ApplicationVM applicationVM = new ApplicationVM(
				applicationVMURI,
				applicationVMManagementInboundPortURI,
				applicationVMRequestSubmissionInboundPortURI,
				applicationVMRequestNotificationOutboundPortURI
				);
		AbstractCVM.theCVM.addDeployedComponent(applicationVM);
		
		if (LOGGING_ALL | LOGGING_APPLICATION_VM) {
			applicationVM.toggleLogging();
			applicationVM.toggleTracing();
		}
		
		/**
		 * D�claration d'un nouveau dispatcher
		 */
		
		final String dispatcherURI = generateURI(Tag.DISPATCHER);
		final String dispatcherManagementInboundPortURI = generateURI(Tag.DISPATCHER_MANAGEMENT_INBOUND_PORT);
		
		Dispatcher dispatcher = new Dispatcher(
				dispatcherURI,
				dispatcherManagementInboundPortURI
				);
		AbstractCVM.theCVM.addDeployedComponent(dispatcher);
		
		if (LOGGING_ALL | LOGGING_DISPATCHER) {
			dispatcher.toggleLogging();
			dispatcher.toggleTracing();
		}
		
		dispatcher.start();
		
		/**
		 * Tentative d'allocation du nombre de coeurs voulu pour l'applicationVM
		 */
		
		ComputerServicesOutboundPort csop = (ComputerServicesOutboundPort) findPortFromURI(computerServicesOutboundPortMap.get(computerURI));
		AllocatedCore[] cores = csop.allocateCores(numberOfCores);
		
		if (cores.length == numberOfCores)
			logMessage("(" + cores.length + "/" + numberOfCores + ") Amount of wanted cores successfully found on " + computerURI);
		else
			logMessage("(" + cores.length + "/" + numberOfCores + ") Amount of wanted cores unfortunately not found on " + computerURI);
		
		/**
		 * Si � ce stade aucun coeurs n'est disponible alors nous nous trouvons dans un �tat incoh�rent 
		 * et la soumission ne peut donc donner suite
		 */
		
		if (cores.length == 0) {
			logMessage("GRAVE : no core busyless found on the selected computer " + computerURI);
			AbstractCVM.theCVM.removeDeployedComponent(requestGenerator);
			AbstractCVM.theCVM.removeDeployedComponent(applicationVM);
			AbstractCVM.theCVM.removeDeployedComponent(dispatcher);
			logMessage("Submission aborted due to incoherent an computer status on " + computerURI);
			throw new Exception("Submission aborted due to incoherent an computer status on " + computerURI);
		}
		
		applicationVM.allocateCores(cores);
		
		logMessage(cores.length + " cores are successfully allocated for the applicationVM " + applicationVMURI);
		
		/**
		 * Cr�ation d'un port de contr�le pour la gestion du dispatcher
		 */
		
		if (!requiredInterfaces.contains(DispatcherManagementI.class))
			addRequiredInterface(DispatcherManagementI.class);
		
		DispatcherManagementOutboundPort dmop = new DispatcherManagementOutboundPort(DispatcherManagementI.class, this);
		addPort(dmop);
		dmop.publishPort();
		dmop.doConnection(dispatcherManagementInboundPortURI, DispatcherManagementConnector.class.getCanonicalName());
		
		/**
		 * Connexion du RequestGenerator au dispatcher
		 */
				
		String rsipURI = dmop.connectToRequestGenerator(requestGeneratorRequestNotificationInboundPortURI);
		RequestSubmissionOutboundPort rsop = (RequestSubmissionOutboundPort) requestGenerator.findPortFromURI(requestGeneratorRequestSubmissionOutboundPortURI);
		rsop.doConnection(rsipURI, RequestSubmissionConnector.class.getCanonicalName());
		
		logMessage(requestGeneratorURI + " connected to " + dispatcherURI);
		
		/**
		 * Connexion de l'ApplicationVM au dispatcher
		 */
		
		String rnipURI = dmop.connectToApplicationVM(applicationVMRequestSubmissionInboundPortURI);
		RequestNotificationOutboundPort rnop = (RequestNotificationOutboundPort) applicationVM.findPortFromURI(applicationVMRequestNotificationOutboundPortURI);
		rnop.doConnection(rnipURI, RequestNotificationConnector.class.getCanonicalName());
		
		logMessage(applicationVMURI + " connected to " + dispatcherURI);
		
		/**
		 * Lancement de l'applicationVM
		 */
		
		applicationVM.start();
		
		logMessage(applicationVMURI + " launched and ready to receive requests");
		
		/**
		 * Cr�ation du port de management pour la gestion du RequestGenerator
		 */
		
		if (!requiredInterfaces.contains(RequestGeneratorManagementI.class))
			addRequiredInterface(RequestGeneratorManagementI.class);
		
		final String rgmopURI = generateURI(Tag.REQUEST_GENERATOR_MANAGEMENT_OUTBOUND_PORT);
		RequestGeneratorManagementOutboundPort rgmop = new RequestGeneratorManagementOutboundPort(rgmopURI, this);
		addPort(rgmop);
		rgmop.publishPort();
		
		/**
		 * Connexion du port de management au RequestGenerator
		 */
		
		rgmop.doConnection(requestGeneratorManagementInboundPortURI, RequestGeneratorManagementConnector.class.getCanonicalName());
		
		/**
		 * Retour de l'URI du port de management du RequestGenerator
		 */
		
		return rgmopURI;
	}

	@Override
	public void submitApplication(Class<?> inter) throws Exception {
		 
		/**
		 * Recherche parmis les ordinateurs disponibles du premier candidat poss�dant les ressources suffisantes
		 * � l'allocation d'une AVM de taille arbitraire.
		 * Dans un premier temps nous allons salement allouer un processeur entier par AVM.
		 * 
		 * De plus cette fois il va falloir changer le code dynamiquement pour qu'un appel
		 * � une methode de l'interface se solde par un calcul simul� en AVM et ce 
		 * quelque soit l'interface pass�e en param�tre.
		 * 
		 * Il faut savoir que nous n'avons aucune id�e du type d'interface qui nous est soumit. 
		 * Nous supposons donc que les m�thodes de l'interface �mettent des requ�tes quelconques
		 * sous la forme d'objets java.
		 * Chaque appel de ces m�thodes du c�t� client doit donner lieu � un passage de la requ�te
		 * par le r�partiteur de requ�te associ� vers une AVM avec possibilit� de retour de notification.
		 * 
		 * Pour r�aliser cette mission, � l'issue du la g�n�ration du r�partiteur de requ�tes, de l'AVM
		 * ainsi que de leur connexion, il va falloir g�n�rer dynamiquement un RequestSubmissionOutboundPort
		 * impl�mentant les m�thodes de soumission de l'application. De ce fait il faudra g�n�rer un connecteur
		 * modifi�, bidouiller sur le r�partiteur de requ�tes pour faire en sorte qu'il poss�de l'Inbound port associ�
		 * 
		 * 
		 *  
		 * 
		 */

		/**
		 * Parcours de tous les ordinateurs mis � disposition � la recherche d'un processeur � allouer
		 */

		String computerURI = null;
		int numberOfCores = 0; 
		
		for (String cdsdopURI : computerDynamicStateDataOutboundPortMap.values()) {
			ComputerDynamicStateDataOutboundPort cdsdop = (ComputerDynamicStateDataOutboundPort) findPortFromURI(cdsdopURI);
			ComputerDynamicState data = (ComputerDynamicState) cdsdop.request();
			boolean[][] processorMap = data.getCurrentCoreReservations();

			/**
			 * Parcours de tous les processeurs de l'ordinateur en cours
			 */
			
			for (int proccessorIndex = 0; proccessorIndex < processorMap.length; proccessorIndex++) {
				
				/**
				 * Parcours de tous les coeurs du processeur en cours
				 */
				
				for (boolean coreAllocated : processorMap[proccessorIndex]) {
					
					/**
					 * A la d�tection du premier coeur non allou�, nous tenons notre machine pour l'allocation
					 */
					
					if (!coreAllocated) {
						computerURI = data.getComputerURI();
						numberOfCores =  processorMap[proccessorIndex].length;
					}
				}
			}
		}
		
		/**
		 * Dans la cas o� nous n'avons pas trouv� de d'ordinateurs de libres, alors computerURI est toujours nulle
		 * et nous ne pouvons pas donner suite � la demande d'hebergement d'application
		 */
		
		if (computerURI == null && numberOfCores == 0) {
			logMessage("Ressources leak, impossible to welcome the application");
			throw new Exception("Ressources leak, impossible to welcome the application");
		}
		
		logMessage("Ressources found, " + numberOfCores +  " cores on computer : " + computerURI);
		
	}
	
	@Override
	public void forceApplicationVMIncrementation() throws Exception {
		String[] dmopsURI = this.findOutboundPortURIsFromInterface(DispatcherManagementI.class);

		for (String dmopURI : dmopsURI) {
			
			String computerURI = null;
			int numberOfCores = 0; 
			
			for (String cdsdopURI : computerDynamicStateDataOutboundPortMap.values()) {
				ComputerDynamicStateDataOutboundPort cdsdop = (ComputerDynamicStateDataOutboundPort) findPortFromURI(cdsdopURI);
				ComputerDynamicState data = (ComputerDynamicState) cdsdop.request();
				boolean[][] processorMap = data.getCurrentCoreReservations();

				/**
				 * Parcours de tous les processeurs de l'ordinateur en cours
				 */
				
				for (int proccessorIndex = 0; proccessorIndex < processorMap.length; proccessorIndex++) {
					
					/**
					 * Parcours de tous les coeurs du processeur en cours
					 */
					
					for (boolean coreAllocated : processorMap[proccessorIndex]) {
						
						/**
						 * A la d�tection du premier coeur non allou�, nous tenons notre machine pour l'allocation
						 */
						
						if (!coreAllocated) {
							computerURI = data.getComputerURI();
							numberOfCores =  processorMap[proccessorIndex].length;
						}
					}
				}
			}
			
			/**
			 * Dans la cas o� nous n'avons pas trouv� de d'ordinateurs de libres, alors computerURI est toujours nulle
			 * et nous ne pouvons pas donner suite � la demande d'hebergement d'application
			 */
			
			if (computerURI == null && numberOfCores == 0) {
				logMessage("Ressources leak, impossible to welcome the application");
				throw new Exception("Ressources leak, impossible to welcome the application");
			}
			
			logMessage("Ressources found, " + numberOfCores +  " cores on computer : " + computerURI);
			
			/**
			 * D�claration d'une nouvelle applicationVM
			 */
			
			final String applicationVMURI = generateURI(Tag.APPLICATION_VM);
			final String applicationVMManagementInboundPortURI = generateURI(Tag.APPLICATION_VM_MANAGEMENT_INBOUND_PORT);
			final String applicationVMRequestSubmissionInboundPortURI = generateURI(Tag.REQUEST_SUBMISSION_INBOUND_PORT);
			final String applicationVMRequestNotificationOutboundPortURI = generateURI(Tag.REQUEST_NOTIFICATION_OUTBOUND_PORT);
			
			ApplicationVM applicationVM = new ApplicationVM(
					applicationVMURI,
					applicationVMManagementInboundPortURI,
					applicationVMRequestSubmissionInboundPortURI,
					applicationVMRequestNotificationOutboundPortURI
					);
			AbstractCVM.theCVM.addDeployedComponent(applicationVM);
			
			if (LOGGING_ALL | LOGGING_APPLICATION_VM) {
				applicationVM.toggleLogging();
				applicationVM.toggleTracing();
			}
			
			/**
			 * Tentative d'allocation du nombre de coeurs voulu pour l'applicationVM
			 */
			
			ComputerServicesOutboundPort csop = (ComputerServicesOutboundPort) findPortFromURI(computerServicesOutboundPortMap.get(computerURI));
			AllocatedCore[] cores = csop.allocateCores(numberOfCores);
			
			if (cores.length == numberOfCores)
				logMessage("(" + cores.length + "/" + numberOfCores + ") Amount of wanted cores successfully found on " + computerURI);
			else
				logMessage("(" + cores.length + "/" + numberOfCores + ") Amount of wanted cores unfortunately not found on " + computerURI);
			
			/**
			 * Si � ce stade aucun coeurs n'est disponible alors nous nous trouvons dans un �tat incoh�rent 
			 * et la soumission ne peut donc donner suite
			 */
			
			if (cores.length == 0) {
				logMessage("GRAVE : no core busyless found on the selected computer " + computerURI);
				AbstractCVM.theCVM.removeDeployedComponent(applicationVM);
				logMessage("Submission aborted due to incoherent an computer status on " + computerURI);
				throw new Exception("Submission aborted due to incoherent an computer status on " + computerURI);
			}
			
			applicationVM.allocateCores(cores);
			
			logMessage(cores.length + " cores are successfully allocated for the applicationVM " + applicationVMURI);
			
			/**
			 * Connexion de l'ApplicationVM au dispatcher
			 */
			
			DispatcherManagementOutboundPort dmop = (DispatcherManagementOutboundPort) this.findPortFromURI(dmopURI);
			
			String rnipURI = dmop.connectToApplicationVM(applicationVMRequestSubmissionInboundPortURI);
			RequestNotificationOutboundPort rnop = (RequestNotificationOutboundPort) applicationVM.findPortFromURI(applicationVMRequestNotificationOutboundPortURI);
			rnop.doConnection(rnipURI, RequestNotificationConnector.class.getCanonicalName());
			
			logMessage(applicationVMURI + " connected to " + dmop.getClientPortURI());
			
			/**
			 * Lancement de l'applicationVM
			 */
			
			applicationVM.start();
			
			logMessage(applicationVMURI + " launched and ready to receive requests");
		}
		
	}

	@Override
	public void acceptComputerStaticData(String computerURI, ComputerStaticStateI staticState) throws Exception {
		logMessage(computerURI + "submit some static data");
	}


	@Override
	public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI currentDynamicState)
			throws Exception {
		logMessage(computerURI + "submit some dynamic data");
	}

}
