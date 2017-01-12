package fr.upmc.datacenter.software.admissioncontroller_old;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

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
import fr.upmc.datacenter.software.admissioncontroller_old.interfaces.AdmissionControllerI;
import fr.upmc.datacenter.software.admissioncontroller_old.interfaces.AdmissionControllerManagementI;
import fr.upmc.datacenter.software.admissioncontroller_old.ports.AdmissionControllerManagementInboundPort;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.extended.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.extended.connectors.ApplicationVMCoreReleasingConnector;
import fr.upmc.datacenter.software.applicationvm.extended.interfaces.ApplicationVMCoreReleasingI;
import fr.upmc.datacenter.software.applicationvm.extended.ports.ApplicationVMCoreReleasingOutboundPort;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.ApplicationVMReleasingNotificationConnector;
import fr.upmc.datacenter.software.connectors.CoreReleasingNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenter.software.dispatcher.connectors.DispatcherManagementConnector;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherDynamicStateDataConsumerI;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherDynamicStateI;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.datacenter.software.dispatcher.ports.DispatcherDynamicStateDataOutboundPort;
import fr.upmc.datacenter.software.dispatcher.ports.DispatcherManagementOutboundPort;
import fr.upmc.datacenter.software.dispatcher.statistics.ExponentialAverage;
import fr.upmc.datacenter.software.dispatcher.time.Duration;
import fr.upmc.datacenter.software.enumerations.Tag;
import fr.upmc.datacenter.software.interfaces.ApplicationVMReleasingNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.ApplicationVMReleasingNotificationI;
import fr.upmc.datacenter.software.interfaces.CoreReleasingNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.CoreReleasingNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.ApplicationVMReleasingNotificationInboundPort;
import fr.upmc.datacenter.software.ports.ApplicationVMReleasingNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.CoreReleasingNotificationInboundPort;
import fr.upmc.datacenter.software.ports.CoreReleasingNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
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
 * Le {@link AdmissionController} a pour r�le de r�cup�rer les demandes clientes
 * d'h�bergement d'applications. Pour cela il dispose d'un parc informatique {@link Stock}
 * mettant � sa disposition un certain nombre d'ordinateurs
 * {@link Computer} compos�s d'un certain nombre de processeurs
 * {@link Processor} disposant d'une puissance de calcul bas�e sur des mod�les
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
		CoreReleasingNotificationHandlerI,
		ApplicationVMReleasingNotificationHandlerI,
		DispatcherDynamicStateDataConsumerI
{
	public static boolean LOGGING_ALL = false;
	public static boolean LOGGING_REQUEST_GENERATOR = false;
	public static boolean LOGGING_APPLICATION_VM = false;
	public static boolean LOGGING_DISPATCHER = false;
	public static int pushingDelay = 1000;
	
	protected String uri;
	protected Map<String, ComputerStaticStateI> computersStaticStates;
	protected Map<String, ComputerDynamicStateI> computersDynamicStates;
	protected ComponentDataNode admissionControllerDataNode;
	
	protected Map<String, Dispatcher> dispatcherMap;
	protected Map<String, ApplicationVM> applicationVMMap;
	protected Map<String, RequestGenerator> requestGeneratorMap;
	protected List<ApplicationVM> unusedAVMs;

	protected Map<String, DispatcherDynamicStateI> dispatcherDynamicStateIMap;
	
	protected Thread control = new Thread(() -> { // TODO Implementation d'un contr�le possible ... 
		
		long delay = 2 * 1000L;
		
		int totalAlloc = 0;
		
		while (true) {
			
			// DEBUT DE CONTROLE
			
			/**
			 * Recherche des AVMs ayant pour temps moyen de service exponentiel de 7000 ms
			 * et allocation d'un coeur de plus.
			 */			
			
			for (DispatcherDynamicStateI dds : dispatcherDynamicStateIMap.values()) {
				for (String avmrsopURI : dds.getExponentialAverages().keySet()) {					
					ExponentialAverage ea = dds.getExponentialAverages().get(avmrsopURI);
					Duration duration = ea.getValue();
					
					System.out.println("avmrsopURI : " + avmrsopURI);
					
					System.out.println(admissionControllerDataNode.graphToString());
					
					if (duration == null) {
						System.out.println("La dur�e est nulle, la boucle de d'�valuation d'adaptation pour cette AVM s'est ex�cut�e avant m�me l'�chantillonage du premier r�sultat du dispatcher !");
						continue;
					}
					
					if (duration.getMilliseconds() > 5000) {
						ComponentDataNode avmnd = admissionControllerDataNode.findByConnectedPort(avmrsopURI);
						ComponentDataNode cptdn = null;
						
						for (ComponentDataNode node : avmnd.parents) {
							System.out.println(node.uri);
							if (node.uri.contains(Tag.COMPUTER.toString())) {
								cptdn = node;
								break;
							}
						}			
						
						try {
							increaseProcessorsFrenquencies(cptdn.uri);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						System.out.println(">> COMPUTER : " + cptdn.uri);
						System.out.println(">> AVM : " + avmnd.uri);
						
						try {
							if (duration.getMilliseconds() > 10000) {
								allocateCores(cptdn.uri, avmnd.uri, 8);
								totalAlloc += 8;
							} else if (duration.getMilliseconds() > 7500) {
								allocateCores(cptdn.uri, avmnd.uri, 4);
								totalAlloc += 4;
							} else if (duration.getMilliseconds() > 5000) {
								allocateCores(cptdn.uri, avmnd.uri, 2);
								totalAlloc += 2;
							}
						} catch (Exception e) {
							e.printStackTrace();
							//System.out.println(admissionControllerDataNode.graphToString());
							System.out.println(">> COMPUTER : " + cptdn.uri);
							System.out.println(">> AVM : " + avmnd.uri);
							System.exit(0);
						}
						finally {
							System.out.println("\t\t\t>>>>>TOTAL ALLOCATED BY LAW : " + totalAlloc);
						}
					} else {
						ComponentDataNode avmnd = admissionControllerDataNode.findByConnectedPort(avmrsopURI);
						ComponentDataNode cptdn = null;
						
						for (ComponentDataNode node : avmnd.parents) {
							System.out.println(node.uri);
							if (node.uri.contains(Tag.COMPUTER.toString())) {
								cptdn = node;
								break;
							}
						}
						
						try {
							decreaseProcessorsFrenquencies(cptdn.uri);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						System.out.println(">> COMPUTER : " + cptdn.uri);
						System.out.println(">> AVM : " + avmnd.uri);
						
						if (totalAlloc < 8)
							continue;
						
						try {
							if (duration.getMilliseconds() < 2000 && totalAlloc > dds.getPendingRequests().get(avmrsopURI).size()) {
								releaseCores(cptdn.uri, avmnd.uri, 8);
								totalAlloc -= 8;
							} else if (duration.getMilliseconds() < 3000 && totalAlloc > dds.getPendingRequests().get(avmrsopURI).size()) {
								releaseCores(cptdn.uri, avmnd.uri, 4);
								totalAlloc -= 4;
							} else if (duration.getMilliseconds() < 4000 && totalAlloc > dds.getPendingRequests().get(avmrsopURI).size()) {
								releaseCores(cptdn.uri, avmnd.uri, 2);
								totalAlloc -= 2;
							}
						} catch (Exception e) {
							e.printStackTrace();
							//System.out.println(admissionControllerDataNode.graphToString());
							System.out.println(">> COMPUTER : " + cptdn.uri);
							System.out.println(">> AVM : " + avmnd.uri);
							System.exit(0);
						}
						finally {
							System.out.println("\t\t\t>>>>>TOTAL ALLOCATED BY LAW : " + totalAlloc);
						}
					}
				}
			}
				
			// FIN DE CONTROLE
			
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});
	
	/**
	 * Construction d'un {@link AdmissionController} ayant pour nom <em>uri</em>
	 * et disposant d'un port de contr�le offert
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
		dispatcherMap = new HashMap<>();
		applicationVMMap = new HashMap<>();
		requestGeneratorMap = new HashMap<>();
		unusedAVMs = new Vector<>();
		dispatcherDynamicStateIMap = new HashMap<>();

		admissionControllerDataNode = new ComponentDataNode(uri);
		if (!offeredInterfaces.contains(AdmissionControllerManagementI.class))
			addOfferedInterface(AdmissionControllerManagementI.class);

		AdmissionControllerManagementInboundPort acmip = new AdmissionControllerManagementInboundPort(
				AdmissionControllerManagementInboundPortURI, 
				AdmissionControllerManagementI.class, 
				this);
		addPort(acmip);
		acmip.publishPort();
		
		control.start(); //TODO
	}

	/**
	 * Traitements en interne
	 */

	@Override
	public void connectToComputer(String computerURI, String csipURI, String cssdipURI, String cdsdipURI, String ccripURI)
			throws Exception {

		/**
		 * Cr�ation d'un port de sortie ComputerServicesOutboundPort Connexion
		 * au port d'entr�e ComputerServicesInboundPort
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
		 * Cr�ation d'un port de management, d'introspection sur chaque
		 * processeur. Mappage des ports des processeurs en fonction de
		 * l'ordinateur et des processeurs pr�sents dessus
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
		 * Dans la cas o� nous n'avons pas trouv� de d'ordinateurs de libres,
		 * alors computerURI est toujours nulle et nous ne pouvons pas donner
		 * suite � la demande d'hebergement d'application
		 */

		if (computerURI == null) {
			logMessage("Ressources leak, impossible to welcome the application");
			throw new Exception("Ressources leak, impossible to welcome the application");
		}

		logMessage("Ressources found on computer : " + computerURI);

		final String requestGeneratorURI = generateURI(Tag.REQUEST_GENERATOR);
		double meanInterArrivalTime = 500;
		long meanNumberOfInstructions = 10000000000L;
		final String requestGeneratorManagementInboundPortURI = generateURI(Tag.REQUEST_GENERATOR_MANAGEMENT_INBOUND_PORT);
		final String requestGeneratorRequestSubmissionOutboundPortURI = generateURI(Tag.REQUEST_SUBMISSION_OUTBOUND_PORT);
		final String requestGeneratorRequestNotificationInboundPortURI = generateURI(Tag.REQUEST_NOTIFICATION_INBOUND_PORT);

		RequestGenerator rgn = null;

		final String applicationVMURI = generateURI(Tag.APPLICATION_VM);
		final String applicationVMManagementInboundPortURI = generateURI(Tag.APPLICATION_VM_MANAGEMENT_INBOUND_PORT);
		final String applicationVMRequestSubmissionInboundPortURI = generateURI(Tag.REQUEST_SUBMISSION_INBOUND_PORT);
		final String applicationVMRequestNotificationOutboundPortURI = generateURI(Tag.REQUEST_NOTIFICATION_OUTBOUND_PORT);
		final String applicationVMCoreReleasingInboundPortURI = generateURI(Tag.APPLICATION_VM_CORE_RELEASING_INBOUND_PORT);
		final String applicationVMCoreReleasingNotificationOutboundPortURI = generateURI(Tag.APPLICATION_VM_CORE_RELEASING_NOTIFICATION_OUTBOUND_PORT);
		ApplicationVM avm = null;

		final String dispatcherURI = generateURI(Tag.DISPATCHER);
		final String dispatcherManagementInboundPortURI = generateURI(Tag.DISPATCHER_MANAGEMENT_INBOUND_PORT);
		final String dispatcherApplicationVMReleasingNotificationOutboundPortURI = generateURI(Tag.APPLICATION_VM_RELEASING_NOTIFICATION_OUTBOUND_PORT);
		final String dispatcherDynamicStateDataInboundPortURI = generateURI(Tag.DISPATCHER_DYNAMIC_STATE_DATA_INBOUND_PORT);

		Dispatcher dsp = null;

		List<AbstractComponent> components = deploy(
				rgn, 
				requestGeneratorURI, 
				meanInterArrivalTime,
				meanNumberOfInstructions, 
				requestGeneratorManagementInboundPortURI,
				requestGeneratorRequestSubmissionOutboundPortURI, 
				requestGeneratorRequestNotificationInboundPortURI,
				avm, 
				applicationVMURI, 
				applicationVMManagementInboundPortURI,
				applicationVMRequestSubmissionInboundPortURI, 
				applicationVMRequestNotificationOutboundPortURI,
				applicationVMCoreReleasingInboundPortURI, 
				applicationVMCoreReleasingNotificationOutboundPortURI, 
				dsp,
				dispatcherURI, 
				dispatcherManagementInboundPortURI,
				dispatcherApplicationVMReleasingNotificationOutboundPortURI,
				dispatcherDynamicStateDataInboundPortURI);

		rgn = (RequestGenerator) components.get(0);
		avm = (ApplicationVM) components.get(1);
		dsp = (Dispatcher) components.get(2);

		/**
		 * L'AVM est fille de l'ordinateur. Comme une AVM ne peut tourner que sur un unique ordinateur, on peut se permettre
		 * de lier l'AVM � son ordinateur. Cel� nous permettra par la suite de retrouver l'ordinateur sur lequel tourne l'AVM
		 * en se sachant que l'URI de l'AVM.
		 */

		ComponentDataNode cptdn = admissionControllerDataNode.findByURI(computerURI);
		ComponentDataNode avmdn = admissionControllerDataNode.findByURI(applicationVMURI);

		cptdn.addChild(avmdn);

		String rgmop = connect(
				rgn, 
				requestGeneratorManagementInboundPortURI,
				requestGeneratorRequestNotificationInboundPortURI, 
				requestGeneratorRequestSubmissionOutboundPortURI,
				avm, 
				applicationVMManagementInboundPortURI, 
				applicationVMRequestSubmissionInboundPortURI,
				applicationVMRequestNotificationOutboundPortURI, 
				applicationVMCoreReleasingInboundPortURI,
				applicationVMCoreReleasingNotificationOutboundPortURI, 
				dsp, 
				dispatcherManagementInboundPortURI,
				dispatcherApplicationVMReleasingNotificationOutboundPortURI,
				dispatcherDynamicStateDataInboundPortURI);

		/**
		 * Si � ce stade aucun coeurs n'est disponible alors nous nous trouvons
		 * dans un �tat incoh�rent et la soumission ne peut donc donner suite
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
		
		
		
//		allocateCores(computerURI, applicationVMURI, 1);
		
//		increaseProcessorsFrenquencies(computerURI);
//		increaseProcessorsFrenquencies(computerURI);
//		increaseProcessorsFrenquencies(computerURI);
//		increaseProcessorsFrenquencies(computerURI);
		
		
//		Thread delayed = new Thread(() -> {
//			try {
//				Thread.sleep(3000);
//				increaseAVMs(dispatcherURI);
//				increaseAVMs(dispatcherURI);
//				
//				Thread.sleep(3000);
//				decreaseAVMs(dispatcherURI);
//				
//				Thread.sleep(3000);
//				increaseAVMs(dispatcherURI);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
		
//		delayed.start();
		
//		for (int i = 31; i > 0; i--) {
//			increaseAVMs(dispatcherURI);
//		}
				
		
		
		System.out.println("\tComputerAvailableCores[" + computerURI + "] : " + computerAvailableCores(computerURI));

		return rgmop;
	}

	@Deprecated /** NON A JOUR AVEC LES COMPONENTDATANODES **/
	@Override
	public void submitApplication(AbstractApplication application, Class<?> submissionInterface) throws Exception {
		/**
		 * On suppose que le client voulant soumettre son application poss�de
		 * d�j� un composant de type g�n�rateur de requ�tes lui permettant de
		 * soumettre via son interface requise (impl�ment�e par son
		 * ...submissionOutboundPort)
		 */

		/**
		 * Recherche parmis les ordinateurs disponibles du premier candidat
		 * poss�dant les ressources suffisantes � l'allocation d'une AVM de
		 * taille arbitraire. Dans un premier temps nous allons salement allouer
		 * un processeur entier par AVM.
		 * 
		 */

		/**
		 * Parcours de tous les ordinateurs mis � disposition � la recherche
		 * d'un processeur � allouer
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
					 * A la d�tection du premier coeur non allou�, nous tenons
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
		 * Dans la cas o� nous n'avons pas trouv� de d'ordinateurs de libres,
		 * alors computerURI est toujours nulle et nous ne pouvons pas donner
		 * suite � la demande d'hebergement d'application
		 */

		if (computerURI == null && numberOfCores == 0) {
			logMessage("Ressources leak, impossible to welcome the application");
			throw new Exception("Ressources leak, impossible to welcome the application");
		}

		logMessage("Ressources found, " + numberOfCores + " cores on computer : " + computerURI);

		/**
		 * D�claration d'une nouvelle applicationVM
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
		 * D�claration d'un nouveau dispatcher
		 */

		final String dispatcherURI = generateURI(Tag.DISPATCHER);
		final String dispatcherManagementInboundPortURI = generateURI(Tag.DISPATCHER_MANAGEMENT_INBOUND_PORT);
		final String dispatcherApplicationVMReleasingNotificationOutboundPortURI = generateURI(Tag.APPLICATION_VM_RELEASING_NOTIFICATION_OUTBOUND_PORT);
		final String dispatcherDynamicStateDataInboundPortURI = generateURI(Tag.DISPATCHER_DYNAMIC_STATE_DATA_INBOUND_PORT);

		Dispatcher dispatcher = new Dispatcher(
				dispatcherURI, 
				dispatcherManagementInboundPortURI, 
				dispatcherApplicationVMReleasingNotificationOutboundPortURI, 
				dispatcherDynamicStateDataInboundPortURI);
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
		 * Si � ce stade aucun coeurs n'est disponible alors nous nous trouvons
		 * dans un �tat incoh�rent et la soumission ne peut donc donner suite
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
		 * Cr�ation d'un port de contr�le pour la gestion du dispatcher
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
						 * A la d�tection du premier coeur non allou�, nous
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
			 * Dans la cas o� nous n'avons pas trouv� de d'ordinateurs de
			 * libres, alors computerURI est toujours nulle et nous ne pouvons
			 * pas donner suite � la demande d'hebergement d'application
			 */

			if (computerURI == null && numberOfCores == 0) {
				logMessage("Ressources leak, impossible to welcome the application");
				throw new Exception("Ressources leak, impossible to welcome the application");
			}

			logMessage("Ressources found, " + numberOfCores + " cores on computer : " + computerURI);

			/**
			 * D�claration d'une nouvelle applicationVM
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
			 * Si � ce stade aucun coeurs n'est disponible alors nous nous
			 * trouvons dans un �tat incoh�rent et la soumission ne peut donc
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
		 * V�rification que le processeur demand� appartient bien � l'ordinateur
		 */

		if (!computerStaticState.getProcessorURIs().values().contains(processorURI))
			throw new Exception("Le processeur n'appartient pas � cet ordinateur");

		/**
		 * V�rifiaction que le processeur poss�de bien le num�ro de coeur
		 * souhait�
		 */

		if (!(coreNo < computerStaticState.getNumberOfCoresPerProcessor()))
			throw new Exception("Num�ro de coeur trop haut, le processeur n'en poss�de pas autant");

		/**
		 * Collecte de la fr�quence courante du coeur
		 */

		frequency = pds.getCurrentCoreFrequency(coreNo);

		logMessage("On computer " + computerURI + ", on processor " + processorURI + ", the core " + coreNo
				+ ", the frequency is " + frequency);

		/**
		 * Constitution de la liste tri�e des fr�quences admissible pour une
		 * augmentation en palier de fr�quences admissibles plut�t de que
		 * tatonner � la recherche d'une fr�quence admissible
		 */

		admissibleFrequencies = new ArrayList<>(pss.getAdmissibleFrequencies());
		Collections.sort(admissibleFrequencies);

		/**
		 * Collecte de l'index de fr�quence possible de la fr�quence actuelle et
		 * tentative d'incr�mentation de cet index pour augmenter la fr�quence
		 * d'un palier
		 */

		index = admissibleFrequencies.indexOf(frequency);
		
		if (index == -1)
			throw new Exception("La fr�quence n'a pas �t� trouv�e parmis les fr�quences admissibles");

		index += 1;

		if (index >= admissibleFrequencies.size()) {
			logMessage("Already maxed");
			return;
		}

		/**
		 * Si il n'est pas possible de monter la fr�quence du coeur souhait�,
		 * alors on augmente la fr�quence de tous les coeurs
		 */

		if (!piop.isCurrentlyPossibleFrequencyForCore(coreNo, admissibleFrequencies.get(index))) {

			int targetIndex = index;

			logMessage("On computer " + computerURI + ", on processor " + processorURI + ", the core " + coreNo
					+ ", isn't possible to up to " + admissibleFrequencies.get(index));

			for (int i = 0; i < computerStaticState.getNumberOfCoresPerProcessor(); i++) {

				/**
				 * Il faut d'abord augmenter la fr�quence des autres coeurs pour
				 * pouvoir augmenter celle du coeur cible
				 */

				if (coreNo == i)
					continue;

				frequency = pds.getCurrentCoreFrequency(i);
				index = admissibleFrequencies.indexOf(frequency);

				if (index == -1)
					throw new Exception("La fr�quence n'a pas �t� trouv�e parmis les fr�quences admissibles");

				index += 1;

				/**
				 * V�rification que le plafond maximum n'a pas �t� atteint
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
			 * Le coeur est possible � augmenter sans craindre une diff�rence de
			 * fr�quences trop haute
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
		 * V�rification que le processeur demand� appartient bien � l'ordinateur
		 */

		if (!computerStaticState.getProcessorURIs().values().contains(processorURI))
			throw new Exception("Le processeur n'appartient pas � cet ordinateur");

		/**
		 * V�rifiaction que le processeur poss�de bien le num�ro de coeur
		 * souhait�
		 */

		if (!(coreNo < computerStaticState.getNumberOfCoresPerProcessor()))
			throw new Exception("Num�ro de coeur trop haut, le processeur n'en poss�de pas autant");

		/**
		 * Collecte de la fr�quence courante du coeur
		 */

		frequency = pds.getCurrentCoreFrequency(coreNo);

		logMessage("On computer " + computerURI + ", on processor " + processorURI + ", the core " + coreNo
				+ ", the frequency is " + frequency);

		/**
		 * Constitution de la liste tri�e des fr�quences admissible pour une
		 * augmentation en palier de fr�quences admissibles plut�t de que
		 * tatonner � la recherche d'une fr�quence admissible
		 */

		admissibleFrequencies = new ArrayList<>(pss.getAdmissibleFrequencies());
		Collections.sort(admissibleFrequencies);

		/**
		 * Collecte de l'index de fr�quence possible de la fr�quence actuelle et
		 * tentative d'incr�mentation de cet index pour augmenter la fr�quence
		 * d'un palier
		 */

		index = admissibleFrequencies.indexOf(frequency);

		if (index == -1)
			throw new Exception("La fr�quence n'a pas �t� trouv�e parmis les fr�quences admissibles");

		index -= 1;

		if (index < 0) {
			logMessage("Already minified");
			return;
		}

		/**
		 * Si il n'est pas possible de monter la fr�quence du coeur souhait�,
		 * alors on augmente la fr�quence de tous les coeurs
		 */

		if (!piop.isCurrentlyPossibleFrequencyForCore(coreNo, admissibleFrequencies.get(index))) {

			int targetIndex = index;

			logMessage("On computer " + computerURI + ", on processor " + processorURI + ", the core " + coreNo
					+ ", isn't possible to up to " + admissibleFrequencies.get(index));

			for (int i = 0; i < computerStaticState.getNumberOfCoresPerProcessor(); i++) {

				/**
				 * Il faut d'abord augmenter la fr�quence des autres coeurs pour
				 * pouvoir augmenter celle du coeur cible
				 */

				if (coreNo == i)
					continue;

				frequency = pds.getCurrentCoreFrequency(i);
				index = admissibleFrequencies.indexOf(frequency);

				if (index == -1)
					throw new Exception("La fr�quence n'a pas �t� trouv�e parmis les fr�quences admissibles");

				index -= 1;

				/**
				 * V�rification que le seuil minimum n'a pas �t� atteint
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
			 * Le coeur est possible � augmenter sans craindre une diff�rence de
			 * fr�quences trop haute
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
		 * Alors attention ici, la demande de lib�ration des coeurs n'est pas
		 * instantan�e. Le processus peut �tre plus ou moins long. Il est
		 * n�cessaire de d'utiliser un gestionnaire d'�venements pour �tre
		 * notifier du moment o� le coeur est effectivement relach� pour pouvoir
		 * mettre � jour son �tat du c�t� des ordinateurs.
		 */

		ComponentDataNode avmdn = admissionControllerDataNode.findByURI(avmURI);
		String cripURI = avmdn.getPortLike(Tag.APPLICATION_VM_CORE_RELEASING_INBOUND_PORT.toString());
		String cropURI = avmdn.getPortConnectedTo(cripURI);

		ApplicationVMCoreReleasingOutboundPort avmcrop = (ApplicationVMCoreReleasingOutboundPort) this.findPortFromURI(cropURI);
		avmcrop.releaseCores(cores);

		logMessage("From " + computerURI + ", " + cores + " cores are been ask to be released from " + avmURI);

	}

	 @Override
	 public void increaseAVMs(String dispatcherURI) throws Exception {
		 ComponentDataNode dspdn = admissionControllerDataNode.findByURI(dispatcherURI);
		 String dmipURI = dspdn.getPortLike(Tag.DISPATCHER_MANAGEMENT_INBOUND_PORT);
		 String dmopURI = dspdn.getPortConnectedTo(dmipURI);
		 DispatcherManagementOutboundPort dmop = (DispatcherManagementOutboundPort) findPortFromURI(dmopURI);
		 
		 /** TODO
		  * Voir si dans la liste des AVM non utilis�es il y a un membre.
		  * Le connecter au dispatcher si oui.
		  * Voir s'il y a un ordinateur avec un coeur de libre.
		  * Allouer ce coeur.
		  * Cr�er une AVM, la d�ployer sur la CVM.
		  * Connecter l'AVM et le Dispatcher.
		  * Mettre les ComponentDataNodes � jour
		  * 
		  * IL restera ensuite � faire les ports de communication des donn�es dynamiques
		  * envoy�s par les dispatchers au contr�leur d'admission, puis d'�crire les m�thodes de
		  * d'analyse/d�cision pour induire une action.  
		  */
		 
		 /**
		  * Cas o� il y a une AVM en attente, cherche et connexion de cette AVM au dispatcher
		  */
		 
		 if (unusedAVMs.size() > 0) {
			 ApplicationVM avm = unusedAVMs.get(0);
			 String avmURI = null;
			 
			 for (String uri : applicationVMMap.keySet()) {
				 if (applicationVMMap.get(uri) == avm) {
					 avmURI = uri;
					 break;
				 }
			 }
			 
			 if (avmURI == null)
				 throw new Exception("No AVM referenced for the last unused one");
			 
			 ComponentDataNode avmdn = admissionControllerDataNode.findByURI(avmURI);
			 
			 dspdn
			 	.addChild(avmdn);
			 
			 String rsipURI = avmdn.getPortLike(Tag.REQUEST_SUBMISSION_INBOUND_PORT);
			 String rnopURI = avmdn.getPortLike(Tag.REQUEST_NOTIFICATION_OUTBOUND_PORT);
			 
			 Dispatcher dsp = dispatcherMap.get(dispatcherURI);
			 List<String> rsopURIsBefore = new ArrayList<>(dsp.getRequestSubmissionOutboundPortURIs());
			 String rnipURI = dmop.connectToApplicationVM(rsipURI);
			 RequestNotificationOutboundPort rnop = (RequestNotificationOutboundPort) avm.findPortFromURI(rnopURI);
			 rnop.doConnection(rnipURI, RequestNotificationConnector.class.getCanonicalName());
			 List<String> rsopURIsAfter = new ArrayList<>(dsp.getRequestSubmissionOutboundPortURIs());
			 rsopURIsAfter.removeAll(rsopURIsBefore);
			 if (rsopURIsAfter.size() != 1)
				 throw new Exception("Dispatcher some connections increase the rsops counts more than expected (1) : " + rsopURIsAfter.size());
			 String rsopURI = rsopURIsAfter.get(0);
			 
			 dspdn
			 	.addPort(rnipURI)
			 	.trustedConnect(rsopURI, rsipURI);
			 
			 avmdn
			 	.trustedConnect(rnopURI, rnipURI);
			 
		 } 
		 
		 /**
		  * Si aucune AVM n'est disponible dans la file d'attente, on en cr�� une que
		  * l'on connectera au dispatcher
		  */
		 
		 else {
			 final String avmURI = generateURI(Tag.APPLICATION_VM);
			 final String avmmipURI = generateURI(Tag.APPLICATION_VM_MANAGEMENT_INBOUND_PORT);
			 final String rsipURI = generateURI(Tag.REQUEST_SUBMISSION_INBOUND_PORT);
			 final String rnopURI = generateURI(Tag.REQUEST_NOTIFICATION_OUTBOUND_PORT);
			 final String avmcripURI = generateURI(Tag.APPLICATION_VM_CORE_RELEASING_INBOUND_PORT);
			 final String crnopURI = generateURI(Tag.APPLICATION_VM_CORE_RELEASING_NOTIFICATION_OUTBOUND_PORT);
			 			 
			 ApplicationVM avm = new ApplicationVM(
					 avmURI, 
					 avmmipURI, 
					 rsipURI, 
					 rnopURI, 
					 avmcripURI, 
					 crnopURI);
			 AbstractCVM.theCVM.addDeployedComponent(avm);
			 applicationVMMap.put(avmURI, avm);
			 
			 ComponentDataNode avmdn = new ComponentDataNode(avmURI);
			 
			 avmdn
			 	.addPort(avmmipURI)
			 	.addPort(rsipURI)
			 	.addPort(rnopURI)
			 	.addPort(avmcripURI)
			 	.addPort(crnopURI);
			 
			 dspdn	// Important de rendre connexe le graphe pour les trustedConnect()
			 	.addChild(avmdn);
			 
			 /**
			  * Cr�ation et connexion du port de soumission des demandes de lib�rations de coeurs
			  */
			 
			 if (!requiredInterfaces.contains(ApplicationVMCoreReleasingI.class))
				 requiredInterfaces.add(ApplicationVMCoreReleasingI.class);
			 
			 final String avmcropURI = generateURI(Tag.APPLICATION_VM_CORE_RELEASING_OUTBOUND_PORT);
			 
			 ApplicationVMCoreReleasingOutboundPort avmcrop = new ApplicationVMCoreReleasingOutboundPort(avmcropURI, ApplicationVMCoreReleasingI.class, this);
			 addPort(avmcrop);
			 avmcrop.publishPort();
			 avmcrop.doConnection(avmcripURI, ApplicationVMCoreReleasingConnector.class.getCanonicalName());
			 
			 admissionControllerDataNode
			 	.trustedConnect(avmcropURI, avmcripURI);
			 
			 /**
			  * Cr�ation et connexion du port de notification des lib�rations de coeurs
			  */
			 
			 if (!offeredInterfaces.contains(CoreReleasingNotificationI.class))
				 offeredInterfaces.add(CoreReleasingNotificationI.class);
			 
			 final String crnipURI = generateURI(Tag.APPLICATION_VM_CORE_RELEASING_NOTIFICATION_INBOUND_PORT);
			 
			 CoreReleasingNotificationInboundPort crnip = new CoreReleasingNotificationInboundPort(crnipURI, CoreReleasingNotificationI.class, this);
			 addPort(crnip);
			 crnip.publishPort();
			 
			 CoreReleasingNotificationOutboundPort crnop = (CoreReleasingNotificationOutboundPort) avm.findPortFromURI(crnopURI);
			 crnop.doConnection(crnipURI, CoreReleasingNotificationConnector.class.getCanonicalName());
			 
			 admissionControllerDataNode
			 	.trustedConnect(crnipURI, crnopURI);
			 
			 /**
			  * Cr�ation et connexion du port de gestion de l'AVM (Allocation des coeurs)
			  */
			 
			 if (!requiredInterfaces.contains(ApplicationVMManagementI.class))
				 requiredInterfaces.add(ApplicationVMManagementI.class);
			 
			 final String avmmopURI = generateURI(Tag.APPLICATION_VM_MANAGEMENT_OUTBOUND_PORT);
			 
			 ApplicationVMManagementOutboundPort avmmop = new ApplicationVMManagementOutboundPort(avmmopURI, this);
			 addPort(avmmop);
			 avmmop.publishPort();
			 avmmop.doConnection(avmmipURI, ApplicationVMManagementConnector.class.getCanonicalName());
			 
			 admissionControllerDataNode
			 	.trustedConnect(avmmopURI, avmmipURI);
			 
			 /**
			  * Connexion de l'AVM au Dispatcher
			  */
			 
			 Dispatcher dsp = dispatcherMap.get(dispatcherURI);
			 List<String> rsopURIsBefore = new ArrayList<>(dsp.getRequestSubmissionOutboundPortURIs());
			 System.out.println("AC" + rsopURIsBefore);
			 String rnipURI = dmop.connectToApplicationVM(rsipURI);
			 RequestNotificationOutboundPort rnop = (RequestNotificationOutboundPort) avm.findPortFromURI(rnopURI);
			 rnop.doConnection(rnipURI, RequestNotificationConnector.class.getCanonicalName());
			 List<String> rsopURIsAfter = new ArrayList<>(dsp.getRequestSubmissionOutboundPortURIs());
			 
			 System.out.println("AC" + rsopURIsBefore);
			 System.out.println("AC" + rsopURIsAfter);
			 
			 rsopURIsAfter.removeAll(rsopURIsBefore);
			 if (rsopURIsAfter.size() != 1)
				 throw new Exception("Dispatcher some connections increase the rsops counts more than expected (1) : " + rsopURIsAfter.size());
			 String rsopURI = rsopURIsAfter.get(0);
			 
			 dspdn
			 	.addPort(rnipURI)
			 	.trustedConnect(rsopURI, rsipURI);
			 
			 avmdn
			 	.trustedConnect(rnopURI, rnipURI);
			 
			 /**
			  * Allocation d'un coeur pour l'AVM
			  */
			 
			 String computerURI = findAvailableComputerForApplicationVMAllocation();
			 
			 if (computerURI == null)
				 throw new Exception("No available core on datacenter");
			 
			 allocateCores(computerURI, avmURI, 1);
		 }
		
	 }
	
	 @Override
	 public void decreaseAVMs(String dispatcherURI) throws Exception {
		 ComponentDataNode dspdn = admissionControllerDataNode.findByURI(dispatcherURI);
		 String dmipURI = dspdn.getPortLike(Tag.DISPATCHER_MANAGEMENT_INBOUND_PORT);
		 String dmopURI = dspdn.getPortConnectedTo(dmipURI);
		 DispatcherManagementOutboundPort dmop = (DispatcherManagementOutboundPort) findPortFromURI(dmopURI);
		 
		 dmop.disconnectFromApplicationVM();
	 }

	@Override
	public void acceptCoreReleasing(String avmURI, AllocatedCore allocatedCore) throws Exception {

		/**
		 * TODO Ajouter la m�thode pour lib�rer le coeur du c�t� ordinateur. Il
		 * nous faut savoir quel allocatedCore vient d'�tre lib�r� ou au moins
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
	
	@Override
	public void acceptApplicationVMReleasing(String dispatcherURI, String rsopURI, String rnipURI) throws Exception {
		// TODO Auto-generated method stub		
		
		ComponentDataNode dspdn = admissionControllerDataNode.findByURI(dispatcherURI);
		ComponentDataNode avmdn = admissionControllerDataNode.findByConnectedPort(rsopURI);
		
		ApplicationVM avm = applicationVMMap.get(avmdn.uri);
		String avmrnopURI = avmdn.getPortLike(Tag.REQUEST_NOTIFICATION_OUTBOUND_PORT);
		RequestNotificationOutboundPort avmrnop = (RequestNotificationOutboundPort) avm.findPortFromURI(avmrnopURI);
		
		/**
		 * D�connexion de l'AVM du dispatcher
		 */
		
		avmrnop.doDisconnection();
		
		/**
		 * Destruction du port de r�ception des notifications de requ�tes du dispatcher 
		 */
		
		Dispatcher dsp = dispatcherMap.get(dispatcherURI);
		RequestNotificationInboundPort rnip = (RequestNotificationInboundPort) dsp.findPortFromURI(rnipURI);
		rnip.unpublishPort();
		dsp.removePort(rnip);
		rnip.destroyPort();
		
		dspdn
			.disconnect(rsopURI)
			.disconnect(rnipURI)
			.removeChild(avmdn);	
		
		System.out.println(dspdn);
		
		unusedAVMs.add(avm);
		
		logMessage("AVM RELEASING SUCCESSFUL FOR " + dispatcherURI);
	}
	
	@Override
	public void acceptDispatcherDynamicStateData(DispatcherDynamicStateI data) {
		
		/**
		 * Tiens uniquement � jour les donn�es dynamiques arrivant des dispatchers.
		 * Mise en place de l'intervalle dans la m�thode connect(...)
		 */
		
		dispatcherDynamicStateIMap.put(data.getDispatcherURI(), data);
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
	 * Retourne vrai si l'ordinateur cible poss�de le montant de coeurs libre
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
	 * Retourne l'URI du premier ordinateur trouv� disponible pour une
	 * allocation d'AVM avec le nombre de coeurs souhait�s, null si aucun
	 * ordinateur ne poss�de de coeurs de libres
	 * 
	 * @param cores
	 * @return
	 * @throws Exception
	 */

	protected String findAvailableComputerForApplicationVMAllocation(int cores) throws Exception {
		assert admissionControllerDataNode.children.size() != 0;

		String computerURI = null;

		// TODO Magouille trouv� pour le probl�me des uris imbriqu�s ... � corriger plus tard 

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
	 * Retourne l'URI du premier ordinateur trouv� disponible (avec un coeur de
	 * disponible) pour une allocation d'AVM, null si aucun ordinateur ne
	 * poss�de de coeurs de libres pour une AVM
	 * 
	 * @return
	 */

	protected String findAvailableComputerForApplicationVMAllocation() throws Exception {
		return findAvailableComputerForApplicationVMAllocation(1);
	}

	/**
	 * Tente d'allouer un maximum de coeurs. Un tableau vide est retourn� si
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
	 * D�ploiement des composants pour la simulation de l'application
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

	protected List<AbstractComponent> deploy(
			RequestGenerator rgn, 
			String requestGeneratorURI,
			double meanInterArrivalTime, 
			long meanNumberOfInstructions, 
			String requestGeneratorManagementInboundPortURI,
			String requestGeneratorRequestSubmissionOutboundPortURI,
			String requestGeneratorRequestNotificationInboundPortURI, 
			ApplicationVM avm, 
			String applicationVMURI,
			String applicationVMManagementInboundPortURI, 
			String applicationVMRequestSubmissionInboundPortURI,
			String applicationVMRequestNotificationOutboundPortURI, 
			String applicationVMCoreReleasingInboundPortURI,
			String applicationVMCoreReleasingNotificationOutboundPortURI, 
			Dispatcher dsp, 
			String dispatcherURI,
			String dispatcherManagementInboundPortURI,
			String dispatcherApplicationVMReleasingOutboundPortURI,
			String dispatcherDynamicStateDataInboundPortURI) throws Exception {

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

		dsp = new Dispatcher(
				dispatcherURI, 
				dispatcherManagementInboundPortURI, 
				dispatcherApplicationVMReleasingOutboundPortURI,
				dispatcherDynamicStateDataInboundPortURI);
		AbstractCVM.theCVM.addDeployedComponent(dsp);
		
		if (LOGGING_ALL | LOGGING_DISPATCHER) {
			dsp.toggleLogging();
			dsp.toggleTracing();
		}

		List<AbstractComponent> components = new ArrayList<>();
		components.add(rgn);
		components.add(avm);
		components.add(dsp);
		
		requestGeneratorMap.put(requestGeneratorURI, rgn);
		applicationVMMap.put(applicationVMURI, avm);
		dispatcherMap.put(dispatcherURI, dsp);

		// TODO AJOUTER LE DISPATCHER ET LES AUTRES COMPOSANTS AUX
		// COMPONENTDATANODES
		ComponentDataNode requestGeneratorDataNode = new ComponentDataNode(requestGeneratorURI);
		ComponentDataNode dispatcherDataNode = new ComponentDataNode(dispatcherURI);
		ComponentDataNode applicationVMDataNode = new ComponentDataNode(applicationVMURI);

		// Ajout des ports

		requestGeneratorDataNode
		.addPort(requestGeneratorRequestNotificationInboundPortURI)
		.addPort(requestGeneratorRequestSubmissionOutboundPortURI)
		.addPort(requestGeneratorManagementInboundPortURI);

		dispatcherDataNode
		.addPort(dispatcherManagementInboundPortURI)
		.addPort(dispatcherApplicationVMReleasingOutboundPortURI)
		.addPort(dispatcherDynamicStateDataInboundPortURI);

		applicationVMDataNode
		.addPort(applicationVMManagementInboundPortURI)
		.addPort(applicationVMRequestSubmissionInboundPortURI)
		.addPort(applicationVMRequestNotificationOutboundPortURI)
		.addPort(applicationVMCoreReleasingInboundPortURI)
		.addPort(applicationVMCoreReleasingNotificationOutboundPortURI);
		// Ajout des liens

		dispatcherDataNode
		.addChild(requestGeneratorDataNode)
		.addChild(applicationVMDataNode);

		admissionControllerDataNode
		.addChild(dispatcherDataNode);

		return components;
	}

	/**
	 * Connexion des composants cr��s pour la simulation de l'application avec
	 * collecte des ports de management indentifi�s par l'URI du dispatcher
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
			String dispatcherManagementInboundPortURI,
			String dispatcherApplicationVMReleasingNotificationOutboundPortURI,
			String dispatcherDynamicStateDataInboundPortURI) throws Exception {
		/**
		 * Cr�ation d'un port de contr�le pour la gestion du dispatcher
		 */

		if (!requiredInterfaces.contains(DispatcherManagementI.class))
			addRequiredInterface(DispatcherManagementI.class);

		final String dmopURI = generateURI(Tag.DISPATCHER_MANAGEMENT_OUTBOUND_PORT);
		DispatcherManagementOutboundPort dmop = new DispatcherManagementOutboundPort(dmopURI, DispatcherManagementI.class, this);
		addPort(dmop);
		dmop.publishPort();
		dmop.doConnection(dispatcherManagementInboundPortURI, DispatcherManagementConnector.class.getCanonicalName());

		/**
		 * Cr�ation du port de management pour la gestion du RequestGenerator
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
		 * Cr�ation du port de management pour la gestion de l'AVM
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
		 * Cr�ation du port d'�mission des demande de lib�ration de coeurs �
		 * l'AVM
		 */

		if (!requiredInterfaces.contains(ApplicationVMCoreReleasingI.class))
			requiredInterfaces.add(ApplicationVMCoreReleasingI.class);

		final String cropURI = generateURI(Tag.APPLICATION_VM_CORE_RELEASING_OUTBOUND_PORT);
		ApplicationVMCoreReleasingOutboundPort crop = new ApplicationVMCoreReleasingOutboundPort(cropURI, ApplicationVMCoreReleasingI.class, this);
		addPort(crop);
		crop.publishPort();

		/**
		 * Cr�ation du port de r�c�ption des lib�ration de coeurs par l'AVM
		 */

		if (!offeredInterfaces.contains(CoreReleasingNotificationI.class))
			addOfferedInterface(CoreReleasingNotificationI.class);

		final String crnipURI = generateURI(Tag.APPLICATION_VM_CORE_RELEASING_NOTIFICATION_INBOUND_PORT);
		CoreReleasingNotificationInboundPort crnip = new CoreReleasingNotificationInboundPort(crnipURI,
				CoreReleasingNotificationI.class, this);
		addPort(crnip);
		crnip.publishPort();
		
		/**
		 * Cr�ation du port de r�c�ption des lib�ration d'AVM
		 */
		
		if (!offeredInterfaces.contains(ApplicationVMReleasingNotificationI.class))
			addOfferedInterface(ApplicationVMReleasingNotificationI.class);
		
		final String avmrnipURI = generateURI(Tag.APPLICATION_VM_RELEASING_NOTIFICATION_INBOUND_PORT);
		
		ApplicationVMReleasingNotificationInboundPort avmrnip = 
				new ApplicationVMReleasingNotificationInboundPort(avmrnipURI, ApplicationVMReleasingNotificationI.class, this);
		addPort(avmrnip);
		avmrnip.publishPort();
		
		/**
		 * Cr�ation du port de r�ception des donn�es dynamiques depuis le dispatcher
		 */
		
		if (!requiredInterfaces.contains(ControlledPullI.class))
			requiredInterfaces.add(ControlledPullI.class);
		
		final String ddsdopURI = generateURI(Tag.DISPATCHER_DYNAMIC_STATE_DATA_OUTBOUND_PORT);
		
		DispatcherDynamicStateDataOutboundPort ddsdop = new DispatcherDynamicStateDataOutboundPort(ddsdopURI, this);
		addPort(ddsdop);
		ddsdop.publishPort();
		ddsdop.doConnection(dispatcherDynamicStateDataInboundPortURI, ControlledDataConnector.class.getCanonicalName());
		
		ddsdop.startUnlimitedPushing(pushingDelay); // TODO
		
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
		 * Connexion du contr�leur d'admission � l'AVM pour le demandes de
		 * lib�ration de coeurs
		 */
		crop.doConnection(applicationVMCoreReleasingInboundPortURI, ApplicationVMCoreReleasingConnector.class.getCanonicalName());

		logMessage(crop.getClientPortURI() + " connected to " + crop.getServerPortURI());

		/**
		 * Connexion de l'AVM au contr�leur d'admission pour la notification
		 * effective d'une lib�ration de coeur
		 */

		CoreReleasingNotificationOutboundPort crnop = (CoreReleasingNotificationOutboundPort) avm
				.findPortFromURI(applicationVMCoreReleasingNotificationOutboundPortURI);
		crnop.doConnection(crnipURI, CoreReleasingNotificationConnector.class.getCanonicalName());

		logMessage(crnop.getClientPortURI() + " connected to " + crnop.getServerPortURI());

		/**
		 * Connexion du dispatcher au contr�leur d'admission pour l'�mission 
		 * des notifications de lib�ration d'AVM
		 */
		
		ApplicationVMReleasingNotificationOutboundPort avmrnop = (ApplicationVMReleasingNotificationOutboundPort) dispatcher
				.findPortFromURI(dispatcher.getApplicationVMReleasingNotificationOutboundPortURI());
		avmrnop.doConnection(avmrnipURI, ApplicationVMReleasingNotificationConnector.class.getCanonicalName());
		
		// TODO Mise � jour des informations concernant les DataNodes (connexion
		// des ComponentDataNode)

		// On retrouve les ComponentDataNodes � partir de n'importe quelle
		// information d�tenue sur un noeud particulier

		ComponentDataNode requestGeneratorDataNode = admissionControllerDataNode
				.findByOwnedPort(requestGeneratorRequestSubmissionOutboundPortURI);
		ComponentDataNode dispatcherDataNode = admissionControllerDataNode
				.findByURI(dispatcher.getURI());
		ComponentDataNode avmDataNode = admissionControllerDataNode
				.findByOwnedPort(applicationVMCoreReleasingNotificationOutboundPortURI);

		dispatcherDataNode
		.addPort(dispatcherManagementInboundPortURI)
		.addPort(dispatcher.getRequestNotificationInboundPortURIs().get(0))
		.addPort(dispatcher.getRequestNotificationOutboundPortURI())
		.addPort(dispatcher.getRequestSubmissionInboundPortURI())
		.addPort(dispatcher.getRequestSubmissionOutboundPortURIs().get(0))
		.addPort(dispatcher.getApplicationVMReleasingNotificationOutboundPortURI());

		admissionControllerDataNode
		.addPort(cropURI)
		.addPort(crnipURI)
		.addPort(dmopURI)
		.addPort(avmrnipURI)
		.addPort(ddsdopURI);

		requestGeneratorDataNode
		.trustedConnect(requestGeneratorRequestSubmissionOutboundPortURI, dispatcher.getRequestSubmissionInboundPortURI());

		dispatcherDataNode
		.trustedConnect(dispatcher.getRequestNotificationOutboundPortURI(),	requestGeneratorRequestNotificationInboundPortURI)
		.trustedConnect(dispatcher.getRequestSubmissionOutboundPortURIs().get(0), applicationVMRequestSubmissionInboundPortURI)
		.trustedConnect(dispatcher.getApplicationVMReleasingNotificationOutboundPortURI(), avmrnipURI);

		avmDataNode
		.trustedConnect(applicationVMRequestNotificationOutboundPortURI, dispatcher.getRequestNotificationInboundPortURIs().get(0))
		.trustedConnect(applicationVMCoreReleasingNotificationOutboundPortURI, crnipURI);

		admissionControllerDataNode
		.trustedConnect(dmopURI, dispatcherManagementInboundPortURI)
		.trustedConnect(cropURI, applicationVMCoreReleasingInboundPortURI)
		.trustedConnect(rgmopURI, requestGeneratorManagementInboundPortURI)
		.trustedConnect(avmmopURI, applicationVMManagementInboundPortURI)
		.trustedConnect(ddsdopURI, dispatcherDynamicStateDataInboundPortURI);

		/**
		 * Retour de l'URI du port de management du RequestGenerator
		 */

		return rgmopURI;
	}

	/**
	 * Lancement des composants � la soumission d'un nouvelle application
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
