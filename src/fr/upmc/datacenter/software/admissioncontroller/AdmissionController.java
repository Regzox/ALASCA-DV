package fr.upmc.datacenter.software.admissioncontroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.interfaces.DataRequiredI.PullI;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computer.extended.connectors.ComputerCoreReleasingConnector;
import fr.upmc.datacenter.hardware.computer.extended.interfaces.ComputerCoreReleasingI;
import fr.upmc.datacenter.hardware.computer.extended.ports.ComputerCoreReleasingOutboundPort;
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
import fr.upmc.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.upmc.datacenter.hardware.processors.ProcessorDynamicState;
import fr.upmc.datacenter.hardware.processors.ProcessorStaticState;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorIntrospectionConnector;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorIntrospectionOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI.ControlledPullI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementInboundPort;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.extended.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.extended.connectors.ApplicationVMCoreReleasingConnector;
import fr.upmc.datacenter.software.applicationvm.extended.interfaces.ApplicationVMCoreReleasingI;
import fr.upmc.datacenter.software.applicationvm.extended.ports.ApplicationVMCoreReleasingOutboundPort;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.CoreReleasingNotifiactionConnector;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenter.software.dispatcher.connectors.DispatcherManagementConnector;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.datacenter.software.dispatcher.ports.DispatcherManagementOutboundPort;
import fr.upmc.datacenter.software.enumerations.Tag;
import fr.upmc.datacenter.software.interfaces.CoreReleasingNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.CoreReleasingNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.CoreReleasingNotificationInboundPort;
import fr.upmc.datacenter.software.ports.CoreReleasingNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
import fr.upmc.external.software.applications.AbstractApplication;
import fr.upmc.javassist.DynamicConnectorFactory;
import fr.upmc.nodes.ComponentDataNode;

/**
 * Le {@link AdmissionController} a pour rôle de récupérer les demandes clientes
 * d'hébergement d'applications. Pour cela il dispose d'un parc informatique {@link Stock}
 * mettant à sa disposition un certain nombre d'ordinateurs
 * {@link Computer} composés d'un certain nombre de processeurs
 * {@link Processor} disposant d'une puissance de calcul basée sur des modèles
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
		ComputerStateDataConsumerI, 
		CoreReleasingNotificationHandlerI 
{
	public static boolean LOGGING_ALL = false;
	public static boolean LOGGING_REQUEST_GENERATOR = false;
	public static boolean LOGGING_APPLICATION_VM = false;
	public static boolean LOGGING_DISPATCHER = false;

	protected String uri;
	protected Map<String, ComputerStaticStateI> computersStaticStates;
	protected Map<String, ComputerDynamicStateI> computersDynamicStates;
	protected ComponentDataNode admissionControllerDataNode;

	/**
	 * Construction d'un {@link AdmissionController} ayant pour nom <em>uri</em>
	 * et disposant d'un port de contrôle offert
	 * {@link AdmissionControllerManagementInboundPort}
	 * 
	 * @param uri
	 * @param AdmissionControllerManagementInboundPortURI
	 * @throws Exception
	 */

	public AdmissionController(String uri, String AdmissionControllerManagementInboundPortURI) throws Exception {
		super(1, 1);
		
		this.uri = uri;
		computersStaticStates = new HashMap<>();
		computersDynamicStates = new HashMap<>();

		admissionControllerDataNode = new ComponentDataNode(uri);
		if (!offeredInterfaces.contains(AdmissionControllerManagementI.class))
			addOfferedInterface(AdmissionControllerManagementI.class);

		AdmissionControllerManagementInboundPort acmip = new AdmissionControllerManagementInboundPort(
				AdmissionControllerManagementInboundPortURI, 
				AdmissionControllerManagementI.class, 
				this);
		addPort(acmip);
		acmip.publishPort();
	}

	/**
	 * Traitements en interne
	 */

	@Override
	public void connectToComputer(String computerURI, String csipURI, String cssdipURI, String cdsdipURI, String ccripURI)
			throws Exception {

		/**
		 * Création d'un port de sortie ComputerServicesOutboundPort Connexion
		 * au port d'entrée ComputerServicesInboundPort
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

		String cssdopURI = generateURI(Tag.COMPUTER_STATIC_STATE_DATA_OUTBOUND_PORT);
		ComputerStaticStateDataOutboundPort cssdop = new ComputerStaticStateDataOutboundPort(cssdopURI, this,
				computerURI);
		System.out.println(isInterface(cssdop.getImplementedInterface()));
		addPort(cssdop);
		cssdop.publishPort();
		cssdop.doConnection(cssdipURI, DataConnector.class.getCanonicalName());

		logMessage(cssdopURI + " created and connected to " + cssdipURI);

		if (!requiredInterfaces.contains(ControlledPullI.class))
			addRequiredInterface(ControlledPullI.class);

		String cdsdopURI = generateURI(Tag.COMPUTER_DYNAMIC_STATE_DATA_OUTBOUND_PORT);
		ComputerDynamicStateDataOutboundPort cdsdop = new ComputerDynamicStateDataOutboundPort(cdsdopURI, this,
				computerURI);
		addPort(cdsdop);
		cdsdop.publishPort();
		cdsdop.doConnection(cdsdipURI, ControlledDataConnector.class.getCanonicalName());

		logMessage(cdsdopURI + " created and connected to " + cdsdipURI);
		logMessage("");

		if (!requiredInterfaces.contains(ComputerCoreReleasingI.class))
			addRequiredInterface(ComputerCoreReleasingI.class);

		String ccropURI = generateURI(Tag.COMPUTER_CORE_RELEASING_OUTBOUND_PORT);
		ComputerCoreReleasingOutboundPort ccrop = new ComputerCoreReleasingOutboundPort(ccropURI, ComputerCoreReleasingI.class, this);
		addPort(ccrop);
		ccrop.publishPort();		
		ccrop.doConnection(ccripURI, ComputerCoreReleasingConnector.class.getCanonicalName());

		logMessage(ccropURI + " created and connected to " + ccropURI);
		logMessage("");

		// TODO AJOUT DES PORTS DANS LE NOEUD DU GRAPHE
		ComponentDataNode computerDataNode = new ComponentDataNode(computerURI);

		computerDataNode			.addPort(csipURI)
									.addPort(cssdipURI)
									.addPort(cdsdipURI)
									.addPort(ccripURI);

		admissionControllerDataNode	.addChild(computerDataNode)
									.trustedConnect(csopURI, csipURI)
									.trustedConnect(cssdopURI, cssdipURI)
									.trustedConnect(cdsdopURI, cdsdipURI)
									.trustedConnect(ccropURI, ccripURI);

		computersStaticStates.put(computerURI, (ComputerStaticStateI) cssdop.request());

		/**
		 * Création d'un port de management, d'introspection sur chaque
		 * processeur. Mappage des ports des processeurs en fonction de
		 * l'ordinateur et des processeurs présents dessus
		 * 
		 */

		Map<String, ProcessorManagementOutboundPort> pmops = new HashMap<String, ProcessorManagementOutboundPort>();
		Map<String, ProcessorIntrospectionOutboundPort> piops = new HashMap<String, ProcessorIntrospectionOutboundPort>();

		for (String processorURI : computersStaticStates.get(computerURI).getProcessorURIs().values()) {
			String processorManagementInboundPortURI = computersStaticStates
					.get(computerURI)
					.getProcessorPortMap()
					.get(processorURI)
					.get(ProcessorPortTypes.MANAGEMENT);
			String processorIntrospectionInboundPortURI = computersStaticStates
					.get(computerURI)
					.getProcessorPortMap()
					.get(processorURI)
					.get(ProcessorPortTypes.INTROSPECTION);

			if (!requiredInterfaces.contains(ProcessorManagementI.class))
				addRequiredInterface(ProcessorManagementI.class);

			final String pmopURI = "pmop-" + processorURI;
			ProcessorManagementOutboundPort pmop = new ProcessorManagementOutboundPort(pmopURI, this);
			this.addPort(pmop);
			pmop.publishPort();
			pmop.doConnection(processorManagementInboundPortURI, ProcessorManagementConnector.class.getCanonicalName());

			pmops.put(processorURI, pmop);

			if (!requiredInterfaces.contains(ProcessorIntrospectionI.class))
				addRequiredInterface(ProcessorIntrospectionI.class);

			final String piopURI = "piop-" + processorURI;
			ProcessorIntrospectionOutboundPort piop = new ProcessorIntrospectionOutboundPort(piopURI, this);
			this.addPort(piop);
			piop.publishPort();
			piop.doConnection(processorIntrospectionInboundPortURI,
					ProcessorIntrospectionConnector.class.getCanonicalName());

			piops.put(processorURI, piop);

			// TODO AJOUT DES PROCESSEURS ET DES PORTS CONNUS ASSOCIES
			ComponentDataNode processorDataNode = new ComponentDataNode(processorURI);

			computerDataNode
				.addChild(processorDataNode);

			processorDataNode	
				.addPort(processorIntrospectionInboundPortURI)
				.addPort(processorManagementInboundPortURI);

			admissionControllerDataNode	
				.trustedConnect(pmopURI, processorManagementInboundPortURI)
				.trustedConnect(piopURI, processorIntrospectionInboundPortURI);
		}

	}

	@Override
	public String generateURI(Object tag) {
		return uri + '_' + tag + '_' + Math.abs(new Random().nextInt());
	}

	@Override
	public String submitApplication() throws Exception {

		/**
		 * On alloue uniquement 1 coeur pour chaque application
		 */

		String computerURI = null;

		computerURI = findAvailableComputerForApplicationVMAllocation();

		/**
		 * Dans la cas où nous n'avons pas trouvé de d'ordinateurs de libres,
		 * alors computerURI est toujours nulle et nous ne pouvons pas donner
		 * suite à la demande d'hebergement d'application
		 */

		if (computerURI == null) {
			logMessage("Ressources leak, impossible to welcome the application");
			throw new Exception("Ressources leak, impossible to welcome the application");
		}

		logMessage("Ressources found on computer : " + computerURI);

		final String requestGeneratorURI = generateURI(Tag.REQUEST_GENERATOR);
		double meanInterArrivalTime = 500;
		long meanNumberOfInstructions = 10000000000L;
		final String requestGeneratorManagementInboundPortURI = generateURI(
				Tag.REQUEST_GENERATOR_MANAGEMENT_INBOUND_PORT);
		final String requestGeneratorRequestSubmissionOutboundPortURI = generateURI(
				Tag.REQUEST_SUBMISSION_OUTBOUND_PORT);
		final String requestGeneratorRequestNotificationInboundPortURI = generateURI(
				Tag.REQUEST_NOTIFICATION_INBOUND_PORT);

		RequestGenerator rgn = null;

		final String applicationVMURI = generateURI(Tag.APPLICATION_VM);
		final String applicationVMManagementInboundPortURI = generateURI(Tag.APPLICATION_VM_MANAGEMENT_INBOUND_PORT);
		final String applicationVMRequestSubmissionInboundPortURI = generateURI(Tag.REQUEST_SUBMISSION_INBOUND_PORT);
		final String applicationVMRequestNotificationOutboundPortURI = generateURI(
				Tag.REQUEST_NOTIFICATION_OUTBOUND_PORT);
		final String applicationVMCoreReleasingInboundPortURI = generateURI(
				Tag.APPLICATION_VM_CORE_RELEASING_INBOUND_PORT);
		final String applicationVMCoreReleasingNotificationOutboundPortURI = generateURI(
				Tag.APPLICATION_VM_CORE_RELEASING_NOTIFICATION_OUTBOUND_PORT);
		ApplicationVM avm = null;

		final String dispatcherURI = generateURI(Tag.DISPATCHER);
		final String dispatcherManagementInboundPortURI = generateURI(Tag.DISPATCHER_MANAGEMENT_INBOUND_PORT);

		Dispatcher dsp = null;

		List<AbstractComponent> components = deploy(rgn, requestGeneratorURI, meanInterArrivalTime,
				meanNumberOfInstructions, requestGeneratorManagementInboundPortURI,
				requestGeneratorRequestSubmissionOutboundPortURI, requestGeneratorRequestNotificationInboundPortURI,
				avm, applicationVMURI, applicationVMManagementInboundPortURI,
				applicationVMRequestSubmissionInboundPortURI, applicationVMRequestNotificationOutboundPortURI,
				applicationVMCoreReleasingInboundPortURI, applicationVMCoreReleasingNotificationOutboundPortURI, dsp,
				dispatcherURI, dispatcherManagementInboundPortURI);

		rgn = (RequestGenerator) components.get(0);
		avm = (ApplicationVM) components.get(1);
		dsp = (Dispatcher) components.get(2);

		/**
		 * L'AVM est fille de l'ordinateur. Comme une AVM ne peut tourner que sur un unique ordinateur, on peut se permettre
		 * de lier l'AVM à son ordinateur. Celà nous permettra par la suite de retrouver l'ordinateur sur lequel tourne l'AVM
		 * en se sachant que l'URI de l'AVM.
		 */

		ComponentDataNode cptdn = admissionControllerDataNode.findByURI(computerURI);
		ComponentDataNode avmdn = admissionControllerDataNode.findByURI(applicationVMURI);

		cptdn.addChild(avmdn);

		String rgmop = connect(rgn, requestGeneratorManagementInboundPortURI,
				requestGeneratorRequestNotificationInboundPortURI, requestGeneratorRequestSubmissionOutboundPortURI,
				avm, applicationVMManagementInboundPortURI, applicationVMRequestSubmissionInboundPortURI,
				applicationVMRequestNotificationOutboundPortURI, applicationVMCoreReleasingInboundPortURI,
				applicationVMCoreReleasingNotificationOutboundPortURI, dsp, dispatcherManagementInboundPortURI);

		/**
		 * Si à ce stade aucun coeurs n'est disponible alors nous nous trouvons
		 * dans un état incohérent et la soumission ne peut donc donner suite
		 */

		AllocatedCore[] cores = tryToAllocateCoreOn(computerURI);

		logMessage("Cores available found on computer : " + cores.length);

		if (cores.length == 0) {
			logMessage("GRAVE : no core busyless found on the selected computer " + computerURI);
			AbstractCVM.theCVM.removeDeployedComponent(rgn);
			AbstractCVM.theCVM.removeDeployedComponent(avm);
			AbstractCVM.theCVM.removeDeployedComponent(dsp);
			logMessage("Submission aborted due to incoherent an computer status on " + computerURI);
			throw new Exception("Submission aborted due to incoherent an computer status on " + computerURI);
		}

		avm.allocateCores(cores);

		logMessage(cores.length + " cores are successfully allocated for the applicationVM " + applicationVMURI);

		launch(rgn, avm, dsp);

		/** TODO TESTS **/

		System.out.println("\tComputerAvailableCores[" + computerURI + "] : " + computerAvailableCores(computerURI));
		
		allocateCores(computerURI, applicationVMURI, 15);
	
		System.out.println("\tComputerAvailableCores[" + computerURI + "] : " + computerAvailableCores(computerURI));

		return rgmop;
	}

	@Override
	public void submitApplication(AbstractApplication application, Class<?> submissionInterface) throws Exception {
		/**
		 * On suppose que le client voulant soumettre son application possède
		 * déjà un composant de type générateur de requêtes lui permettant de
		 * soumettre via son interface requise (implémentée par son
		 * ...submissionOutboundPort)
		 */

		/**
		 * Recherche parmis les ordinateurs disponibles du premier candidat
		 * possèdant les ressources suffisantes à l'allocation d'une AVM de
		 * taille arbitraire. Dans un premier temps nous allons salement allouer
		 * un processeur entier par AVM.
		 * 
		 */

		/**
		 * Parcours de tous les ordinateurs mis à disposition à la recherche
		 * d'un processeur à allouer
		 */

		String computerURI = null;
		int numberOfCores = 0;

		for (String port : admissionControllerDataNode.ports) {
			String cdsdopURI = null;
			if (port.contains(Tag.COMPUTER_DYNAMIC_STATE_DATA_OUTBOUND_PORT.toString()))
				cdsdopURI = port;
			else
				continue;

			ComputerDynamicStateDataOutboundPort cdsdop = (ComputerDynamicStateDataOutboundPort) findPortFromURI(
					cdsdopURI);
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
					 * A la détection du premier coeur non alloué, nous tenons
					 * notre machine pour l'allocation
					 */

					if (!coreAllocated) {
						computerURI = data.getComputerURI();
						numberOfCores = processorMap[proccessorIndex].length;
					}
				}
			}
		}

		/**
		 * Dans la cas où nous n'avons pas trouvé de d'ordinateurs de libres,
		 * alors computerURI est toujours nulle et nous ne pouvons pas donner
		 * suite à la demande d'hebergement d'application
		 */

		if (computerURI == null && numberOfCores == 0) {
			logMessage("Ressources leak, impossible to welcome the application");
			throw new Exception("Ressources leak, impossible to welcome the application");
		}

		logMessage("Ressources found, " + numberOfCores + " cores on computer : " + computerURI);

		/**
		 * Déclaration d'une nouvelle applicationVM
		 */

		final String applicationVMURI = generateURI(Tag.APPLICATION_VM);
		final String applicationVMManagementInboundPortURI = generateURI(Tag.APPLICATION_VM_MANAGEMENT_INBOUND_PORT);
		final String applicationVMRequestSubmissionInboundPortURI = generateURI(Tag.REQUEST_SUBMISSION_INBOUND_PORT);
		final String applicationVMRequestNotificationOutboundPortURI = generateURI(
				Tag.REQUEST_NOTIFICATION_OUTBOUND_PORT);
		final String applicationVMCoreReleasingInboundPortURI = generateURI(
				Tag.APPLICATION_VM_CORE_RELEASING_INBOUND_PORT);
		final String applicationVMCoreReleasingNotificationOutboundPortURI = generateURI(
				Tag.APPLICATION_VM_CORE_RELEASING_NOTIFICATION_OUTBOUND_PORT);

		ApplicationVM applicationVM = new ApplicationVM(applicationVMURI, applicationVMManagementInboundPortURI,
				applicationVMRequestSubmissionInboundPortURI, applicationVMRequestNotificationOutboundPortURI,
				applicationVMCoreReleasingInboundPortURI, applicationVMCoreReleasingNotificationOutboundPortURI);

		AbstractCVM.theCVM.addDeployedComponent(applicationVM);

		if (LOGGING_ALL | LOGGING_APPLICATION_VM) {
			applicationVM.toggleLogging();
			applicationVM.toggleTracing();
		}

		/**
		 * Déclaration d'un nouveau dispatcher
		 */

		final String dispatcherURI = generateURI(Tag.DISPATCHER);
		final String dispatcherManagementInboundPortURI = generateURI(Tag.DISPATCHER_MANAGEMENT_INBOUND_PORT);

		Dispatcher dispatcher = new Dispatcher(dispatcherURI, dispatcherManagementInboundPortURI);
		AbstractCVM.theCVM.addDeployedComponent(dispatcher);

		if (LOGGING_ALL | LOGGING_DISPATCHER) {
			dispatcher.toggleLogging();
			dispatcher.toggleTracing();
		}

		dispatcher.start();

		/**
		 * Tentative d'allocation du nombre de coeurs voulu pour l'applicationVM
		 */

		ComponentDataNode computerDataNode = admissionControllerDataNode.findByURI(computerURI);
		String csipURI = computerDataNode.getPortLike(Tag.COMPUTER_SERVICES_INBOUND_PORT);
		String csopURI = computerDataNode.getPortConnectedTo(csipURI);
		ComputerServicesOutboundPort csop = (ComputerServicesOutboundPort) findPortFromURI(csopURI);

		AllocatedCore[] cores = csop.allocateCores(numberOfCores);

		if (cores.length == numberOfCores)
			logMessage("(" + cores.length + "/" + numberOfCores + ") Amount of wanted cores successfully found on "
					+ computerURI);
		else
			logMessage("(" + cores.length + "/" + numberOfCores + ") Amount of wanted cores unfortunately not found on "
					+ computerURI);

		/**
		 * Si à ce stade aucun coeurs n'est disponible alors nous nous trouvons
		 * dans un état incohérent et la soumission ne peut donc donner suite
		 */

		if (cores.length == 0) {
			logMessage("GRAVE : no core busyless found on the selected computer " + computerURI);
			AbstractCVM.theCVM.removeDeployedComponent(applicationVM);
			AbstractCVM.theCVM.removeDeployedComponent(dispatcher);
			logMessage("Submission aborted due to incoherent an computer status on " + computerURI);
			throw new Exception("Submission aborted due to incoherent an computer status on " + computerURI);
		}

		applicationVM.allocateCores(cores);

		logMessage(cores.length + " cores are successfully allocated for the applicationVM " + applicationVMURI);

		/**
		 * Création d'un port de contrôle pour la gestion du dispatcher
		 */

		if (!requiredInterfaces.contains(DispatcherManagementI.class))
			addRequiredInterface(DispatcherManagementI.class);

		DispatcherManagementOutboundPort dmop = new DispatcherManagementOutboundPort(DispatcherManagementI.class, this);
		addPort(dmop);
		dmop.publishPort();
		dmop.doConnection(dispatcherManagementInboundPortURI, DispatcherManagementConnector.class.getCanonicalName());

		/**
		 * Connexion de l'application au dispatcher
		 */

		String rsipURI = dmop
				.connectToRequestGenerator(application.findPortURIsFromInterface(RequestNotificationI.class)[0]);
		RequestSubmissionOutboundPort rsop = (RequestSubmissionOutboundPort) application
				.findPortFromURI(application.findPortURIsFromInterface(RequestSubmissionI.class)[0]);
		rsop.doConnection(rsipURI, DynamicConnectorFactory
				.createConnector(RequestSubmissionI.class, RequestSubmissionI.class).getCanonicalName());

		logMessage(rsop.getOwner().toString() + " connected to " + dispatcherURI);

		/**
		 * Connexion de l'ApplicationVM au dispatcher
		 */

		String rnipURI = dmop.connectToApplicationVM(applicationVMRequestSubmissionInboundPortURI);
		RequestNotificationOutboundPort rnop = (RequestNotificationOutboundPort) applicationVM
				.findPortFromURI(applicationVMRequestNotificationOutboundPortURI);
		rnop.doConnection(rnipURI, RequestNotificationConnector.class.getCanonicalName());

		logMessage(applicationVMURI + " connected to " + dispatcherURI);

		/**
		 * Lancement de l'applicationVM
		 */

		applicationVM.start();

		logMessage(applicationVMURI + " launched and ready to receive requests");

	}

	@Override
	public void forceApplicationVMIncrementation() throws Exception {
		String[] dmopsURI = this.findOutboundPortURIsFromInterface(DispatcherManagementI.class);

		for (String dmopURI : dmopsURI) {

			String computerURI = null;
			int numberOfCores = 0;

			for (String port : admissionControllerDataNode.ports) {
				String cdsdopURI = null;
				if (port.contains(Tag.COMPUTER_DYNAMIC_STATE_DATA_OUTBOUND_PORT.toString()))
					cdsdopURI = port;
				else
					continue;

				ComputerDynamicStateDataOutboundPort cdsdop = (ComputerDynamicStateDataOutboundPort) findPortFromURI(
						cdsdopURI);
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
						 * A la détection du premier coeur non alloué, nous
						 * tenons notre machine pour l'allocation
						 */

						if (!coreAllocated) {
							computerURI = data.getComputerURI();
							numberOfCores = processorMap[proccessorIndex].length;
						}
					}
				}
			}

			/**
			 * Dans la cas où nous n'avons pas trouvé de d'ordinateurs de
			 * libres, alors computerURI est toujours nulle et nous ne pouvons
			 * pas donner suite à la demande d'hebergement d'application
			 */

			if (computerURI == null && numberOfCores == 0) {
				logMessage("Ressources leak, impossible to welcome the application");
				throw new Exception("Ressources leak, impossible to welcome the application");
			}

			logMessage("Ressources found, " + numberOfCores + " cores on computer : " + computerURI);

			/**
			 * Déclaration d'une nouvelle applicationVM
			 */

			final String applicationVMURI = generateURI(Tag.APPLICATION_VM);
			final String applicationVMManagementInboundPortURI = generateURI(
					Tag.APPLICATION_VM_MANAGEMENT_INBOUND_PORT);
			final String applicationVMRequestSubmissionInboundPortURI = generateURI(
					Tag.REQUEST_SUBMISSION_INBOUND_PORT);
			final String applicationVMRequestNotificationOutboundPortURI = generateURI(
					Tag.REQUEST_NOTIFICATION_OUTBOUND_PORT);
			final String applicationVMCoreReleasingInboundPortURI = generateURI(
					Tag.APPLICATION_VM_CORE_RELEASING_INBOUND_PORT);
			final String applicationVMCoreReleasingNotificationOutboundPortURI = generateURI(
					Tag.APPLICATION_VM_CORE_RELEASING_NOTIFICATION_OUTBOUND_PORT);

			ApplicationVM applicationVM = new ApplicationVM(applicationVMURI, applicationVMManagementInboundPortURI,
					applicationVMRequestSubmissionInboundPortURI, applicationVMRequestNotificationOutboundPortURI,
					applicationVMCoreReleasingInboundPortURI, applicationVMCoreReleasingNotificationOutboundPortURI);
			AbstractCVM.theCVM.addDeployedComponent(applicationVM);

			if (LOGGING_ALL | LOGGING_APPLICATION_VM) {
				applicationVM.toggleLogging();
				applicationVM.toggleTracing();
			}

			/**
			 * Tentative d'allocation du nombre de coeurs voulu pour
			 * l'applicationVM
			 */

			ComponentDataNode computerDataNode = admissionControllerDataNode.findByURI(computerURI);
			String csipURI = computerDataNode.getPortLike(Tag.COMPUTER_SERVICES_INBOUND_PORT);
			String csopURI = computerDataNode.getPortConnectedTo(csipURI);
			ComputerServicesOutboundPort csop = (ComputerServicesOutboundPort) findPortFromURI(csopURI);

			AllocatedCore[] cores = csop.allocateCores(numberOfCores);

			if (cores.length == numberOfCores)
				logMessage("(" + cores.length + "/" + numberOfCores + ") Amount of wanted cores successfully found on "
						+ computerURI);
			else
				logMessage("(" + cores.length + "/" + numberOfCores
						+ ") Amount of wanted cores unfortunately not found on " + computerURI);

			/**
			 * Si à ce stade aucun coeurs n'est disponible alors nous nous
			 * trouvons dans un état incohérent et la soumission ne peut donc
			 * donner suite
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
			RequestNotificationOutboundPort rnop = (RequestNotificationOutboundPort) applicationVM
					.findPortFromURI(applicationVMRequestNotificationOutboundPortURI);
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
	public void startDynamicStateDataPushing(int milliseconds) throws Exception {

		stopDynamicStateDataPushing();

		for (String port : admissionControllerDataNode.ports) {
			String cdsdopURI = null;
			if (port.contains(Tag.COMPUTER_DYNAMIC_STATE_DATA_OUTBOUND_PORT.toString()))
				cdsdopURI = port;
			else
				continue;

			ComputerDynamicStateDataOutboundPort cdsdop = (ComputerDynamicStateDataOutboundPort) findPortFromURI(
					cdsdopURI);

			cdsdop.startUnlimitedPushing(milliseconds);
		}

	}

	@Override
	public void stopDynamicStateDataPushing() throws Exception {

		for (String port : admissionControllerDataNode.ports) {
			String cdsdopURI = null;
			if (port.contains(Tag.COMPUTER_DYNAMIC_STATE_DATA_OUTBOUND_PORT.toString()))
				cdsdopURI = port;
			else
				continue;

			ComputerDynamicStateDataOutboundPort cdsdop = (ComputerDynamicStateDataOutboundPort) findPortFromURI(
					cdsdopURI);

			cdsdop.stopPushing();
		}

	}

	@Override
	public void acceptComputerStaticData(String computerURI, ComputerStaticStateI staticState) throws Exception {
		logMessage(computerURI + "submit some static data");
		computersStaticStates.put(computerURI, staticState);
	}

	@Override
	public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI currentDynamicState)
			throws Exception {
		logMessage(computerURI + "submit some dynamic data");
		computersDynamicStates.put(computerURI, currentDynamicState);
	}

	@Override
	public void increaseCoreFrequency(String computerURI, String processorURI, Integer coreNo) throws Exception {
		assert computerURI != null;
		assert processorURI != null;
		assert coreNo >= 0;

		ComputerStaticStateI computerStaticState = computersStaticStates.get(computerURI);
		ProcessorManagementOutboundPort pmop = (ProcessorManagementOutboundPort) this
				.findPortFromURI("pmop-" + processorURI);
		ProcessorIntrospectionOutboundPort piop = (ProcessorIntrospectionOutboundPort) this
				.findPortFromURI("piop-" + processorURI);
		ProcessorStaticState pss = (ProcessorStaticState) piop.getStaticState();
		ProcessorDynamicState pds = (ProcessorDynamicState) piop.getDynamicState();
		List<Integer> admissibleFrequencies = null;
		Integer frequency = null;
		Integer index = null;

		/**
		 * Vérification que le processeur demandé appartient bien à l'ordinateur
		 */

		if (!computerStaticState.getProcessorURIs().values().contains(processorURI))
			throw new Exception("Le processeur n'appartient pas à cet ordinateur");

		/**
		 * Vérifiaction que le processeur possède bien le numéro de coeur
		 * souhaité
		 */

		if (!(coreNo < computerStaticState.getNumberOfCoresPerProcessor()))
			throw new Exception("Numéro de coeur trop haut, le processeur n'en possède pas autant");

		/**
		 * Collecte de la fréquence courante du coeur
		 */

		frequency = pds.getCurrentCoreFrequency(coreNo);

		logMessage("On computer " + computerURI + ", on processor " + processorURI + ", the core " + coreNo
				+ ", the frequency is " + frequency);

		/**
		 * Constitution de la liste triée des fréquences admissible pour une
		 * augmentation en palier de fréquences admissibles plutôt de que
		 * tatonner à la recherche d'une fréquence admissible
		 */

		admissibleFrequencies = new ArrayList<>(pss.getAdmissibleFrequencies());
		Collections.sort(admissibleFrequencies);

		/**
		 * Collecte de l'index de fréquence possible de la fréquence actuelle et
		 * tentative d'incrémentation de cet index pour augmenter la fréquence
		 * d'un palier
		 */

		index = admissibleFrequencies.indexOf(frequency);
		
		if (index == -1)
			throw new Exception("La fréquence n'a pas été trouvée parmis les fréquences admissibles");

		index += 1;

		if (index >= admissibleFrequencies.size()) {
			logMessage("Already maxed");
			return;
		}

		/**
		 * Si il n'est pas possible de monter la fréquence du coeur souhaité,
		 * alors on augmente la fréquence de tous les coeurs
		 */

		if (!piop.isCurrentlyPossibleFrequencyForCore(coreNo, admissibleFrequencies.get(index))) {

			int targetIndex = index;

			logMessage("On computer " + computerURI + ", on processor " + processorURI + ", the core " + coreNo
					+ ", isn't possible to up to " + admissibleFrequencies.get(index));

			for (int i = 0; i < computerStaticState.getNumberOfCoresPerProcessor(); i++) {

				/**
				 * Il faut d'abord augmenter la fréquence des autres coeurs pour
				 * pouvoir augmenter celle du coeur cible
				 */

				if (coreNo == i)
					continue;

				frequency = pds.getCurrentCoreFrequency(i);
				index = admissibleFrequencies.indexOf(frequency);

				if (index == -1)
					throw new Exception("La fréquence n'a pas été trouvée parmis les fréquences admissibles");

				index += 1;

				/**
				 * Vérification que le plafond maximum n'a pas été atteint
				 */

				if (index >= admissibleFrequencies.size()) {
					logMessage("Frequency maxed");
					break;
				}

				logMessage("Computer " + computerURI + " core " + i + " frequency up to "
						+ admissibleFrequencies.get(index));

				pmop.setCoreFrequency(i, admissibleFrequencies.get(index));
			}

			logMessage("On computer " + computerURI + ", on processor " + processorURI + ", the core " + coreNo
					+ ", the frequency up to " + admissibleFrequencies.get(targetIndex));

			pmop.setCoreFrequency(coreNo, admissibleFrequencies.get(targetIndex));

		} else {

			/**
			 * Le coeur est possible à augmenter sans craindre une différence de
			 * fréquences trop haute
			 */

			logMessage("On computer " + computerURI + ", on processor " + processorURI + ", the core " + coreNo
					+ ", the frequency up to " + admissibleFrequencies.get(index));
			
			pmop.setCoreFrequency(coreNo, admissibleFrequencies.get(index));
		}
	}

	@Override
	public void decreaseCoreFrequency(String computerURI, String processorURI, Integer coreNo) throws Exception {
		assert computerURI != null;
		assert processorURI != null;
		assert coreNo >= 0;

		// void result = void.STAGNATED;
		ComputerStaticStateI computerStaticState = computersStaticStates.get(computerURI);
		ProcessorManagementOutboundPort pmop = (ProcessorManagementOutboundPort) this
				.findPortFromURI("pmop-" + processorURI);
		ProcessorIntrospectionOutboundPort piop = (ProcessorIntrospectionOutboundPort) this
				.findPortFromURI("piop-" + processorURI);
		ProcessorStaticState pss = (ProcessorStaticState) piop.getStaticState();
		ProcessorDynamicState pds = (ProcessorDynamicState) piop.getDynamicState();
		List<Integer> admissibleFrequencies = null;
		Integer frequency = null;
		Integer index = null;

		/**
		 * Vérification que le processeur demandé appartient bien à l'ordinateur
		 */

		if (!computerStaticState.getProcessorURIs().values().contains(processorURI))
			throw new Exception("Le processeur n'appartient pas à cet ordinateur");

		/**
		 * Vérifiaction que le processeur possède bien le numéro de coeur
		 * souhaité
		 */

		if (!(coreNo < computerStaticState.getNumberOfCoresPerProcessor()))
			throw new Exception("Numéro de coeur trop haut, le processeur n'en possède pas autant");

		/**
		 * Collecte de la fréquence courante du coeur
		 */

		frequency = pds.getCurrentCoreFrequency(coreNo);

		logMessage("On computer " + computerURI + ", on processor " + processorURI + ", the core " + coreNo
				+ ", the frequency is " + frequency);

		/**
		 * Constitution de la liste triée des fréquences admissible pour une
		 * augmentation en palier de fréquences admissibles plutôt de que
		 * tatonner à la recherche d'une fréquence admissible
		 */

		admissibleFrequencies = new ArrayList<>(pss.getAdmissibleFrequencies());
		Collections.sort(admissibleFrequencies);

		/**
		 * Collecte de l'index de fréquence possible de la fréquence actuelle et
		 * tentative d'incrémentation de cet index pour augmenter la fréquence
		 * d'un palier
		 */

		index = admissibleFrequencies.indexOf(frequency);

		if (index == -1)
			throw new Exception("La fréquence n'a pas été trouvée parmis les fréquences admissibles");

		index -= 1;

		if (index < 0) {
			logMessage("Already minified");
			return;
		}

		/**
		 * Si il n'est pas possible de monter la fréquence du coeur souhaité,
		 * alors on augmente la fréquence de tous les coeurs
		 */

		if (!piop.isCurrentlyPossibleFrequencyForCore(coreNo, admissibleFrequencies.get(index))) {

			int targetIndex = index;

			logMessage("On computer " + computerURI + ", on processor " + processorURI + ", the core " + coreNo
					+ ", isn't possible to up to " + admissibleFrequencies.get(index));

			for (int i = 0; i < computerStaticState.getNumberOfCoresPerProcessor(); i++) {

				/**
				 * Il faut d'abord augmenter la fréquence des autres coeurs pour
				 * pouvoir augmenter celle du coeur cible
				 */

				if (coreNo == i)
					continue;

				frequency = pds.getCurrentCoreFrequency(i);
				index = admissibleFrequencies.indexOf(frequency);

				if (index == -1)
					throw new Exception("La fréquence n'a pas été trouvée parmis les fréquences admissibles");

				index -= 1;

				/**
				 * Vérification que le seuil minimum n'a pas été atteint
				 */

				if (index < 0) {
					logMessage("Frequency minified");
					break;
				}

				logMessage("Computer " + computerURI + " core " + i + " frequency down to "
						+ admissibleFrequencies.get(index));

				pmop.setCoreFrequency(i, admissibleFrequencies.get(index));
			}

			logMessage("On computer " + computerURI + ", on processor " + processorURI + ", the core " + coreNo
					+ ", the frequency down to " + admissibleFrequencies.get(targetIndex));

			pmop.setCoreFrequency(coreNo, admissibleFrequencies.get(targetIndex));

		} else {

			/**
			 * Le coeur est possible à augmenter sans craindre une différence de
			 * fréquences trop haute
			 */

			logMessage("On computer " + computerURI + ", on processor " + processorURI + ", the core " + coreNo
					+ ", the frequency down to " + admissibleFrequencies.get(index));

			pmop.setCoreFrequency(coreNo, admissibleFrequencies.get(index));
		}
	}

	@Override
	public void increaseProcessorFrenquency(String computerURI, String processorURI) throws Exception {
		assert computerURI != null;
		assert processorURI != null;
		
		ComputerStaticStateI computerStaticState = computersStaticStates.get(computerURI);
		
		for (int i = 0; i < computerStaticState.getNumberOfCoresPerProcessor(); i++)
			increaseCoreFrequency(computerURI, processorURI, i);
		
	}

	@Override
	public void decreaseProcessorFrenquency(String computerURI, String processorURI) throws Exception {
		assert computerURI != null;
		assert processorURI != null;

		ComputerStaticStateI computerStaticState = computersStaticStates.get(computerURI);

		for (int i = 0; i < computerStaticState.getNumberOfCoresPerProcessor(); i++)
			decreaseCoreFrequency(computerURI, processorURI, i);

	}

	@Override
	public void increaseProcessorsFrenquencies(String computerURI) throws Exception {
		assert computerURI != null;

		ComputerStaticStateI computerStaticState = computersStaticStates.get(computerURI);

		for (String processorURI : computerStaticState.getProcessorURIs().values()) {
			increaseProcessorFrenquency(computerURI, processorURI);
		}
	}

	@Override
	public void decreaseProcessorsFrenquencies(String computerURI) throws Exception {
		assert computerURI != null;

		ComputerStaticStateI computerStaticState = computersStaticStates.get(computerURI);

		for (String processorURI : computerStaticState.getProcessorURIs().values()) {
			decreaseProcessorFrenquency(computerURI, processorURI);
		}

	}

	@Override
	public void allocateCores(String computerURI, String avmURI, int cores) throws Exception {
		assert computerURI != null;
		assert avmURI != null;
		assert cores > 0;


		if (hasAvailableCoresFromComputer(computerURI, cores)) {
			ComponentDataNode computerDataNode = admissionControllerDataNode.findByURI(computerURI);
			String csipURI = computerDataNode.getPortLike(Tag.COMPUTER_SERVICES_INBOUND_PORT);
			String csopURI = computerDataNode.getPortConnectedTo(csipURI);
			ComputerServicesOutboundPort csop = (ComputerServicesOutboundPort) findPortFromURI(csopURI);

			AllocatedCore[] acs = csop.allocateCores(cores);

			ComponentDataNode avmdn = admissionControllerDataNode.findByURI(avmURI);

			String avmmipURI = avmdn.getPortLike(Tag.APPLICATION_VM_MANAGEMENT_INBOUND_PORT.toString());
			String avmmopURI = avmdn.getPortConnectedTo(avmmipURI);
			ApplicationVMManagementOutboundPort avmmop = (ApplicationVMManagementOutboundPort) findPortFromURI(
					avmmopURI);

			avmmop.allocateCores(acs);

			logMessage("From " + computerURI + ", " + cores + " cores are been allocated to " + avmURI);
		} else {
			logMessage("No sufficient cores found on the computer : " + cores + "/" + computerAvailableCores(computerURI));
		}

	}

	@Override
	public void releaseCores(String computerURI, String avmURI, int cores) throws Exception {
		assert computerURI != null;
		assert avmURI != null;
		assert cores > 0;

		/**
		 * Alors attention ici, la demande de libération des coeurs n'est pas
		 * instantanée. Le processus peut être plus ou moins long. Il est
		 * nécessaire de d'utiliser un gestionnaire d'évenements pour être
		 * notifier du moment où le coeur est effectivement relaché pour pouvoir
		 * mettre à jour son état du côté des ordinateurs.
		 */

		ComponentDataNode avmdn = admissionControllerDataNode.findByURI(avmURI);
		String cripURI = avmdn.getPortLike(Tag.APPLICATION_VM_CORE_RELEASING_INBOUND_PORT.toString());
		String cropURI = avmdn.getPortConnectedTo(cripURI);

		ApplicationVMCoreReleasingOutboundPort avmcrop = (ApplicationVMCoreReleasingOutboundPort) this.findPortFromURI(cropURI);
		avmcrop.releaseCores(cores);

		logMessage("From " + computerURI + ", " + cores + " cores are been ask to be released from " + avmURI);

	}

	// @Override
	// public void increaseAVMs(String dispatcherURI) {
	//
	// }
	//
	// @Override
	// public void decreaseAVMs(String dispatcherURI) {
	//
	// }

	@Override
	public void acceptCoreReleasing(String avmURI, AllocatedCore allocatedCore) throws Exception {

		/**
		 * TODO Ajouter la méthode pour libérer le coeur du côté ordinateur. Il
		 * nous faut savoir quel allocatedCore vient d'être libéré ou au moins
		 * sa position dans l'ordinateur physique (proc, core)
		 */

		ComponentDataNode avmcdn = admissionControllerDataNode.findByURI(avmURI);
		ComponentDataNode cptcdn = null;
		for (ComponentDataNode node : avmcdn.parents) {
			if (node.uri.contains(Tag.COMPUTER.toString())) {
				cptcdn = node;
				break;
			}
		}

		if (cptcdn == null)
			throw new Exception("The AVM " + avmURI + " doesn't have a computer parent ! ");

		ComponentDataNode cpt = admissionControllerDataNode.findByURI(cptcdn.uri);
		String ccripURI = cpt.getPortLike(Tag.COMPUTER_CORE_RELEASING_INBOUND_PORT);
		String ccropURI = cpt.getPortConnectedTo(ccripURI);

		ComputerCoreReleasingOutboundPort ccrop = (ComputerCoreReleasingOutboundPort) this.findPortFromURI(ccropURI);
		ccrop.releaseCore(allocatedCore);

		System.out.println("\tComputerAvailableCores[" + cptcdn.uri + "] : " + computerAvailableCores(cptcdn.uri));

		logMessage("CORE RELEASING SUCCESSFUL FOR " + avmURI);

	}

	/**************************************************
	 * REFACTORING METHODS
	 **************************************************/

	/**
	 * Retourne le nombre de coeurs libre pour l'allocation au sein d'un
	 * ordinateur
	 * 
	 * @param computerURI
	 * @return
	 * @throws Exception
	 */

	protected int computerAvailableCores(String computerURI) throws Exception {
		ComponentDataNode computerDataNode = admissionControllerDataNode.findByURI(computerURI);
		String cdsdipURI = computerDataNode.getPortLike(Tag.COMPUTER_DYNAMIC_STATE_DATA_INBOUND_PORT.toString());
		String cdsdopURI = computerDataNode.getPortConnectedTo(cdsdipURI);
		ComputerDynamicStateDataOutboundPort cdsdop = (ComputerDynamicStateDataOutboundPort) findPortFromURI(cdsdopURI);

		ComputerDynamicState data = (ComputerDynamicState) cdsdop.request();
		boolean[][] cr = data.getCurrentCoreReservations();

		int computerAvailableCores = 0;

		for (int pi = 0; pi < cr.length; pi++)
			computerAvailableCores += processorAvailableCores(cr[pi]);

		return computerAvailableCores;
	}

	/**
	 * Retourne le nombre de coeurs libre au sein d'un processeur
	 * 
	 * @param coreReservations
	 * @return
	 * @throws Exception
	 */

	protected int processorAvailableCores(boolean[] coreReservations) throws Exception {
		int processorAvailableCores = 0;

		for (int ci = 0; ci < coreReservations.length; ci++)
			if (!coreReservations[ci])
				processorAvailableCores++;

		return processorAvailableCores;
	}

	/**
	 * Retourne vrai si l'ordinateur cible possède le montant de coeurs libre
	 * voulu
	 * 
	 * @param computerURI
	 * @param wanted
	 * @return
	 * @throws Exception
	 */

	protected boolean hasAvailableCoresFromComputer(String computerURI, int wanted) throws Exception {		
		return (wanted <= computerAvailableCores(computerURI));
	}

	/**
	 * Retourne l'URI du premier ordinateur trouvé disponible pour une
	 * allocation d'AVM avec le nombre de coeurs souhaités, null si aucun
	 * ordinateur ne possède de coeurs de libres
	 * 
	 * @param cores
	 * @return
	 * @throws Exception
	 */

	protected String findAvailableComputerForApplicationVMAllocation(int cores) throws Exception {
		assert admissionControllerDataNode.children.size() != 0;

		String computerURI = null;

		// TODO Magouille trouvé pour le problème des uris imbriqués ... à corriger plus tard 

		List<String> someURIs = new ArrayList<>(admissionControllerDataNode.getURIsLike(Tag.COMPUTER.toString()));
		List<String> computerURIs = new ArrayList<>();

		for (String uri : someURIs) {
			if (uri.contains("processor"))
				continue;
			computerURIs.add(uri);
		}

		// FIN DE MAGOUILLE

		for (String cURI : computerURIs) {
			if (hasAvailableCoresFromComputer(cURI, cores)) {
				computerURI = cURI;
				break;
			}
		}

		return computerURI;
	}

	/**
	 * Retourne l'URI du premier ordinateur trouvé disponible (avec un coeur de
	 * disponible) pour une allocation d'AVM, null si aucun ordinateur ne
	 * possède de coeurs de libres pour une AVM
	 * 
	 * @return
	 */

	protected String findAvailableComputerForApplicationVMAllocation() throws Exception {
		return findAvailableComputerForApplicationVMAllocation(1);
	}

	/**
	 * Tente d'allouer un maximum de coeurs. Un tableau vide est retourné si
	 * aucun coeur n'est disponible
	 * 
	 * @param computerURI
	 * @param wantedCores
	 * @return
	 * @throws Exception
	 */

	protected AllocatedCore[] tryToAllocateCoresOn(String computerURI, int wantedCores) throws Exception {
		assert computerURI != null;
		assert wantedCores > 0;

		ComponentDataNode computerDataNode = admissionControllerDataNode.findByURI(computerURI);
		String csipURI = computerDataNode.getPortLike(Tag.COMPUTER_SERVICES_INBOUND_PORT);
		String csopURI = computerDataNode.getPortConnectedTo(csipURI);
		ComputerServicesOutboundPort csop = (ComputerServicesOutboundPort) findPortFromURI(csopURI);

		AllocatedCore[] cores = csop.allocateCores(wantedCores);

		if (cores.length == wantedCores)
			logMessage("(" + cores.length + "/" + wantedCores + ") Amount of wanted cores successfully found on "
					+ computerURI);
		else
			logMessage("(" + cores.length + "/" + wantedCores + ") Amount of wanted cores unfortunately not found on "
					+ computerURI);

		return cores;
	}

	/**
	 * Tente d'allouer un coeur sur l'ordinateur cible
	 * 
	 * @param computerURI
	 * @return
	 * @throws Exception
	 */

	protected AllocatedCore[] tryToAllocateCoreOn(String computerURI) throws Exception {
		return tryToAllocateCoresOn(computerURI, 1);
	}

	/**
	 * Déploiement des composants pour la simulation de l'application
	 * 
	 * @param rgn
	 * @param requestGeneratorURI
	 * @param meanInterArrivalTime
	 * @param meanNumberOfInstructions
	 * @param requestGeneratorManagementInboundPortURI
	 * @param requestGeneratorRequestSubmissionOutboundPortURI
	 * @param requestGeneratorRequestNotificationInboundPortURI
	 * @param avm
	 * @param applicationVMURI
	 * @param applicationVMManagementInboundPortURI
	 * @param applicationVMRequestSubmissionInboundPortURI
	 * @param applicationVMRequestNotificationOutboundPortURI
	 * @param dsp
	 * @param dispatcherURI
	 * @param dispatcherManagementInboundPortURI
	 * @return
	 * @throws Exception
	 */

	protected List<AbstractComponent> deploy(RequestGenerator rgn, String requestGeneratorURI,
			double meanInterArrivalTime, long meanNumberOfInstructions, String requestGeneratorManagementInboundPortURI,
			String requestGeneratorRequestSubmissionOutboundPortURI,
			String requestGeneratorRequestNotificationInboundPortURI, ApplicationVM avm, String applicationVMURI,
			String applicationVMManagementInboundPortURI, String applicationVMRequestSubmissionInboundPortURI,
			String applicationVMRequestNotificationOutboundPortURI, String applicationVMCoreReleasingInboundPortURI,
			String applicationVMCoreReleasingNotificationOutboundPortURI, Dispatcher dsp, String dispatcherURI,
			String dispatcherManagementInboundPortURI) throws Exception {

		rgn = new RequestGenerator(requestGeneratorURI, meanInterArrivalTime, meanNumberOfInstructions,
				requestGeneratorManagementInboundPortURI, requestGeneratorRequestSubmissionOutboundPortURI,
				requestGeneratorRequestNotificationInboundPortURI);
		AbstractCVM.theCVM.addDeployedComponent(rgn);

		if (LOGGING_ALL | LOGGING_REQUEST_GENERATOR) {
			rgn.toggleLogging();
			rgn.toggleTracing();
		}

		avm = new ApplicationVM(applicationVMURI, applicationVMManagementInboundPortURI,
				applicationVMRequestSubmissionInboundPortURI, applicationVMRequestNotificationOutboundPortURI,
				applicationVMCoreReleasingInboundPortURI, applicationVMCoreReleasingNotificationOutboundPortURI);
		AbstractCVM.theCVM.addDeployedComponent(avm);

		if (LOGGING_ALL | LOGGING_APPLICATION_VM) {
			avm.toggleLogging();
			avm.toggleTracing();
		}

		dsp = new Dispatcher(dispatcherURI, dispatcherManagementInboundPortURI);
		AbstractCVM.theCVM.addDeployedComponent(dsp);

		if (LOGGING_ALL | LOGGING_DISPATCHER) {
			dsp.toggleLogging();
			dsp.toggleTracing();
		}

		List<AbstractComponent> components = new ArrayList<>();
		components.add(rgn);
		components.add(avm);
		components.add(dsp);

		// TODO AJOUTER LE DISPATCHER ET LES AUTRES COMPOSANTS AUX
		// COMPONENTDATANODES
		ComponentDataNode requestGeneratorDataNode = new ComponentDataNode(requestGeneratorURI);
		ComponentDataNode dispatcherDataNode = new ComponentDataNode(dispatcherURI);
		ComponentDataNode applicationVMDataNode = new ComponentDataNode(applicationVMURI);

		// Ajout des ports

		requestGeneratorDataNode.addPort(requestGeneratorRequestNotificationInboundPortURI)
		.addPort(requestGeneratorRequestSubmissionOutboundPortURI)
		.addPort(requestGeneratorManagementInboundPortURI);

		dispatcherDataNode.addPort(dispatcherManagementInboundPortURI);

		applicationVMDataNode.addPort(applicationVMManagementInboundPortURI)
		.addPort(applicationVMRequestSubmissionInboundPortURI)
		.addPort(applicationVMRequestNotificationOutboundPortURI)
		.addPort(applicationVMCoreReleasingInboundPortURI)
		.addPort(applicationVMCoreReleasingNotificationOutboundPortURI);
		// Ajout des liens

		dispatcherDataNode.addChild(requestGeneratorDataNode).addChild(applicationVMDataNode);

		admissionControllerDataNode.addChild(dispatcherDataNode);

		return components;
	}

	/**
	 * Connexion des composants créés pour la simulation de l'application avec
	 * collecte des ports de management indentifiés par l'URI du dispatcher
	 * 
	 * @param rgn
	 * @param requestGeneratorManagementInboundPortURI
	 * @param requestGeneratorRequestNotificationInboundPortURI
	 * @param requestGeneratorRequestSubmissionOutboundPortURI
	 * @param avm
	 * @param applicationVMRequestSubmissionInboundPortURI
	 * @param applicationVMRequestNotificationOutboundPortURI
	 * @param dispatcherManagementInboundPortURI
	 * @return
	 * @throws Exception
	 */

	protected String connect(RequestGenerator rgn, String requestGeneratorManagementInboundPortURI,
			String requestGeneratorRequestNotificationInboundPortURI,
			String requestGeneratorRequestSubmissionOutboundPortURI, ApplicationVM avm,
			String applicationVMManagementInboundPortURI, String applicationVMRequestSubmissionInboundPortURI,
			String applicationVMRequestNotificationOutboundPortURI, String applicationVMCoreReleasingInboundPortURI,
			String applicationVMCoreReleasingNotificationOutboundPortURI, Dispatcher dispatcher,
			String dispatcherManagementInboundPortURI) throws Exception {
		/**
		 * Création d'un port de contrôle pour la gestion du dispatcher
		 */

		if (!requiredInterfaces.contains(DispatcherManagementI.class))
			addRequiredInterface(DispatcherManagementI.class);

		DispatcherManagementOutboundPort dmop = new DispatcherManagementOutboundPort(DispatcherManagementI.class, this);
		addPort(dmop);
		dmop.publishPort();
		dmop.doConnection(dispatcherManagementInboundPortURI, DispatcherManagementConnector.class.getCanonicalName());

		/**
		 * Création du port de management pour la gestion du RequestGenerator
		 */

		if (!requiredInterfaces.contains(RequestGeneratorManagementI.class))
			addRequiredInterface(RequestGeneratorManagementI.class);

		final String rgmopURI = generateURI(Tag.REQUEST_GENERATOR_MANAGEMENT_OUTBOUND_PORT);
		RequestGeneratorManagementOutboundPort rgmop = new RequestGeneratorManagementOutboundPort(rgmopURI, this);
		addPort(rgmop);
		rgmop.publishPort();
		rgmop.doConnection(requestGeneratorManagementInboundPortURI,
				RequestGeneratorManagementConnector.class.getCanonicalName());

		/**
		 * Création du port de management pour la gestion de l'AVM
		 */

		if (!requiredInterfaces.contains(ApplicationVMCoreReleasingI.class))
			addRequiredInterface(ApplicationVMCoreReleasingI.class);

		final String avmmopURI = generateURI(Tag.APPLICATION_VM_MANAGEMENT_OUTBOUND_PORT);
		ApplicationVMManagementOutboundPort avmmop = new ApplicationVMManagementOutboundPort(avmmopURI, this);
		addPort(avmmop);
		avmmop.publishPort();
		avmmop.doConnection(applicationVMManagementInboundPortURI,
				ApplicationVMManagementConnector.class.getCanonicalName());

		Map<String, ApplicationVMManagementOutboundPort> avmmopsMap = new HashMap<>();
		avmmopsMap.put(avmmopURI, avmmop);

		/**
		 * Création du port d'émission des demande de libération de coeurs à
		 * l'AVM
		 */

		if (!requiredInterfaces.contains(ApplicationVMCoreReleasingI.class))
			requiredInterfaces.add(ApplicationVMCoreReleasingI.class);

		final String cropURI = generateURI(Tag.APPLICATION_VM_CORE_RELEASING_OUTBOUND_PORT);
		ApplicationVMCoreReleasingOutboundPort crop = new ApplicationVMCoreReleasingOutboundPort(cropURI, ApplicationVMCoreReleasingI.class, this);
		addPort(crop);
		crop.publishPort();

		/**
		 * Création du port de récéption des libération de coeurs par l'AVM
		 */

		if (!offeredInterfaces.contains(CoreReleasingNotificationI.class))
			addOfferedInterface(CoreReleasingNotificationI.class);

		final String crnipURI = generateURI(Tag.APPLICATION_VM_CORE_RELEASING_NOTIFICATION_INBOUND_PORT);
		CoreReleasingNotificationInboundPort crnip = new CoreReleasingNotificationInboundPort(crnipURI,
				CoreReleasingNotificationI.class, this);
		addPort(crnip);
		crnip.publishPort();

		/****************************************************************************************************/

		/**
		 * Connexion du RequestGenerator au dispatcher
		 */

		String rsipURI = dmop.connectToRequestGenerator(requestGeneratorRequestNotificationInboundPortURI);
		RequestSubmissionOutboundPort rsop = (RequestSubmissionOutboundPort) rgn
				.findPortFromURI(requestGeneratorRequestSubmissionOutboundPortURI);
		rsop.doConnection(rsipURI, RequestSubmissionConnector.class.getCanonicalName());

		logMessage(rsop.getClientPortURI() + " connected to " + rsop.getServerPortURI());

		/**
		 * Connexion de l'ApplicationVM au dispatcher
		 */

		String rnipURI = dmop.connectToApplicationVM(applicationVMRequestSubmissionInboundPortURI);
		RequestNotificationOutboundPort rnop = (RequestNotificationOutboundPort) avm
				.findPortFromURI(applicationVMRequestNotificationOutboundPortURI);
		rnop.doConnection(rnipURI, RequestNotificationConnector.class.getCanonicalName());

		logMessage(rnop.getClientPortURI() + " connected to " + rnop.getServerPortURI());

		/**
		 * Connexion du contrôleur d'admission à l'AVM pour le demandes de
		 * libération de coeurs
		 */
		crop.doConnection(applicationVMCoreReleasingInboundPortURI, ApplicationVMCoreReleasingConnector.class.getCanonicalName());

		logMessage(crop.getClientPortURI() + " connected to " + crop.getServerPortURI());

		/**
		 * Connexion de l'AVM au contrôleur d'admission pour la notification
		 * effective d'une libération de coeur
		 */

		CoreReleasingNotificationOutboundPort crnop = (CoreReleasingNotificationOutboundPort) avm
				.findPortFromURI(applicationVMCoreReleasingNotificationOutboundPortURI);
		crnop.doConnection(crnipURI, CoreReleasingNotifiactionConnector.class.getCanonicalName());

		logMessage(crnop.getClientPortURI() + " connected to " + crnop.getServerPortURI());

		// TODO Mise à jour des informations concernant les DataNodes (connexion
		// des ComponentDataNode)

		// On retrouve les ComponentDataNodes à partir de n'importe quelle
		// information détenue sur un noeud particulier

		ComponentDataNode requestGeneratorDataNode = admissionControllerDataNode
				.findByOwnedPort(requestGeneratorRequestSubmissionOutboundPortURI);
		ComponentDataNode dispatcherDataNode = admissionControllerDataNode.findByURI(dispatcher.getURI());
		ComponentDataNode avmDataNode = admissionControllerDataNode
				.findByOwnedPort(applicationVMCoreReleasingNotificationOutboundPortURI);

		dispatcherDataNode.addPort(dispatcher.getRequestNotificationInboundPortURI())
		.addPort(dispatcher.getRequestNotificationOutboundPortURI())
		.addPort(dispatcher.getRequestSubmissionInboundPortURI())
		.addPort(dispatcher.getRequestSubmissionOutboundPortURI());

		admissionControllerDataNode.addPort(cropURI).addPort(crnipURI);

		requestGeneratorDataNode.trustedConnect(requestGeneratorRequestSubmissionOutboundPortURI,
				dispatcher.getRequestSubmissionInboundPortURI());

		dispatcherDataNode.trustedConnect(dispatcher.getRequestNotificationOutboundPortURI(),
				requestGeneratorRequestNotificationInboundPortURI);
		dispatcherDataNode.trustedConnect(dispatcher.getRequestSubmissionOutboundPortURI(),
				applicationVMRequestSubmissionInboundPortURI);

		avmDataNode.trustedConnect(applicationVMRequestNotificationOutboundPortURI,
				dispatcher.getRequestNotificationInboundPortURI());
		avmDataNode.trustedConnect(applicationVMCoreReleasingNotificationOutboundPortURI, crnipURI);

		admissionControllerDataNode.trustedConnect(cropURI, applicationVMCoreReleasingInboundPortURI);
		admissionControllerDataNode.trustedConnect(rgmopURI, requestGeneratorManagementInboundPortURI);
		admissionControllerDataNode.trustedConnect(avmmopURI, applicationVMManagementInboundPortURI);

		/**
		 * Retour de l'URI du port de management du RequestGenerator
		 */

		return rgmopURI;
	}

	/**
	 * Lancement des composants à la soumission d'un nouvelle application
	 * 
	 * @param rgn
	 * @param avm
	 * @param dsp
	 * @throws Exception
	 */

	protected void launch(RequestGenerator rgn, ApplicationVM avm, Dispatcher dsp) throws Exception {
		rgn.start();
		avm.start();
		dsp.start();
	}

}
