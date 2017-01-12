package fr.upmc.datacenter.software.controllers.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.ports.PortI;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.data.interfaces.PerformanceControllerPortsDataI;
import fr.upmc.datacenter.providers.resources.exceptions.NoApplicationVMException;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.connectors.LogicalResourcesProviderServicesConnector;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderServicesI;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderServicesOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.controllers.performance.interfaces.PerformanceControllerI;
import fr.upmc.datacenter.software.controllers.performance.interfaces.PerformanceControllerManagementI;
import fr.upmc.datacenter.software.controllers.performance.interfaces.PerformanceControllerServicesI;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenter.software.dispatcher.connectors.DispatcherManagementConnector;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherDynamicStateDataConsumerI;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherDynamicStateI;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.datacenter.software.dispatcher.ports.DispatcherDynamicStateDataOutboundPort;
import fr.upmc.datacenter.software.dispatcher.ports.DispatcherManagementOutboundPort;
import fr.upmc.datacenter.software.enumerations.Tag;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
import fr.upmc.nodes.ComponentDataNode;

public class PerformanceController 
extends 	AbstractComponent
implements 	PerformanceControllerI,
			PerformanceControllerManagementI,
			PerformanceControllerServicesI,
			DispatcherDynamicStateDataConsumerI
{

	public static boolean MULTIAPPLICATION = false;
	public static int DISPATCHER_PUSHING_INTERVAL = 1000; // ms
	
	public enum Branch {LOGICAL_RESOURCES_PROVIDERS, DISPATCHERS, REQUEST_GENERATORS, APPLICATION_VMS, PERFORMANCE_CONTROLLER}
	
	class RequestGeneratorInformation {
		
		public String			uri,
								rgmipURI,
								rsopURI,
								rnipURI;
		
		public RequestSubmissionOutboundPort	rsop;
		
	}
	
	class DispatcherInformation {
		
		public String			uri,
								dmipURI,
								rsipURI,
								rnopURI,
								rsopURI,
								rnipURI,
								dsdipURI;
		
		public RequestSubmissionOutboundPort	rsop;
		public RequestNotificationOutboundPort 	rnop;
		
	}
	
	boolean hasApplication = false;
	
	Map<String, DispatcherDynamicStateI> dspddsimap; 
	
	List<RequestGeneratorInformation> rgninfs;
	List<DispatcherInformation> dspinfs;
	
	ComponentDataNode performanceController;
	
	public PerformanceController(String uri) {
		super(3, 1);
		
		dspddsimap = new HashMap<>();
		rgninfs = new ArrayList<>();
		dspinfs = new ArrayList<>();
		
		performanceController = new ComponentDataNode(uri)
				.addChild(new ComponentDataNode(Branch.LOGICAL_RESOURCES_PROVIDERS))
				.addChild(new ComponentDataNode(Branch.DISPATCHERS))
				.addChild(new ComponentDataNode(Branch.REQUEST_GENERATORS))
				.addChild(new ComponentDataNode(Branch.APPLICATION_VMS));
	}
	
	String generateLogicalResourcesProviderUri(String ownerURI, Object tag) {
		return ownerURI + " : " + tag.toString() + "_" + (performanceController.findByURI(Branch.LOGICAL_RESOURCES_PROVIDERS).children.size() + 1);
	}
	
	String generatePerformanceControllerUri(String ownerURI, Object tag) {
		return ownerURI + " : " + tag.toString() + "_" + (performanceController.findByURI(Branch.PERFORMANCE_CONTROLLER).children.size() + 1);
	}
	
	String generateRequestGeneratorUri(String ownerURI, Object tag) {
		return ownerURI + " : " + tag.toString() + "_" + (performanceController.findByURI(Branch.REQUEST_GENERATORS).children.size() + 1);
	}
	
	String generateDispatcherUri(String ownerURI, Object tag) {
		return ownerURI + " : " + tag.toString() + "_" + (performanceController.findByURI(Branch.DISPATCHERS).children.size() + 1);
	}
	
	@Override
	public void connectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		String	lrpURI = lrppdi.getUri(),
				lrpsipURI = lrppdi.getLogicalResourcesProviderServicesInboundPort(),
				lrpsopURI = generateLogicalResourcesProviderUri(lrpURI, Tag.LOGICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
		ComponentDataNode lrpdn = new ComponentDataNode(lrpURI)
				.addPort(lrppdi.getLogicalResourcesProviderServicesInboundPort());
	
		performanceController.findByURI(Branch.LOGICAL_RESOURCES_PROVIDERS).addChild(lrpdn);
		
		if ( !requiredInterfaces.contains(LogicalResourcesProviderServicesI.class) )
			requiredInterfaces.add(LogicalResourcesProviderServicesI.class);
		
		LogicalResourcesProviderServicesOutboundPort lrpsop = 
				new LogicalResourcesProviderServicesOutboundPort(
						lrpsopURI, 
						LogicalResourcesProviderServicesI.class,
						this);
		addPort(lrpsop);
		lrpsop.publishPort();
		lrpsop.doConnection(lrppdi.getLogicalResourcesProviderServicesInboundPort(), LogicalResourcesProviderServicesConnector.class.getCanonicalName());
		
		performanceController.trustedConnect(lrpsopURI, lrpsipURI);
	}

	@Override
	public void disconnectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {		
		ComponentDataNode lrpdn = performanceController.findByURI(lrppdi.getUri());
		String	lrpsipURI = lrpdn.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_SERVICES_INBOUND_PORT),
				lrpsopURI = lrpdn.getPortConnectedTo(lrpsipURI);
		PortI	lrpsop = findPortFromURI(lrpsopURI);
		
		lrpsop.doDisconnection();
		lrpsop.destroyPort();
		
		performanceController.disconnect(lrpsopURI);
		performanceController.findByURI(Branch.LOGICAL_RESOURCES_PROVIDERS).removeChild(lrpdn);
	}

	@Override
	public void connectPerformanceController(PerformanceControllerPortsDataI cpdi) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnectPerformanceController(PerformanceControllerPortsDataI cpdi) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AllocatedRequestGenerator createAllocatedRequestGenerator() throws Exception {
		final String rgURI = generateRequestGeneratorUri(performanceController.uri, Tag.REQUEST_GENERATOR);
		double meanInterArrivalTime = 500;
		long meanNumberOfInstructions = 10000000000L;
		final String rgmipURI = generateRequestGeneratorUri(rgURI, Tag.REQUEST_GENERATOR_MANAGEMENT_INBOUND_PORT);
		final String rgrsopURI = generateRequestGeneratorUri(rgURI, Tag.REQUEST_SUBMISSION_OUTBOUND_PORT);
		final String rgrnipURI = generateRequestGeneratorUri(rgURI, Tag.REQUEST_NOTIFICATION_INBOUND_PORT);
		
		AllocatedRequestGenerator arg = new AllocatedRequestGenerator(
				rgURI, 
				meanInterArrivalTime, 
				meanNumberOfInstructions, 
				rgmipURI, 
				rgrsopURI, 
				rgrnipURI);
		
		return arg;
	}
	
	@Override
	public RequestGenerator createRequestGenerator(AllocatedRequestGenerator arg) throws Exception {
		RequestGenerator rgn = 
				new RequestGenerator(
						arg.rgURI, 
						arg.meanInterArrivalTime, 
						arg.meanNumberOfInstructions,
						arg.rgmipURI, 
						arg.rgrsopURI,
						arg.rgrnipURI);
		AbstractCVM.theCVM.addDeployedComponent(rgn);
		rgn.start();
		
		return rgn;
	}

	@Override
	public AllocatedDispatcher createAllocatedDispatcher() throws Exception {
		final String dspURI = generateDispatcherUri(performanceController.uri, Tag.DISPATCHER);
		final String dspmipURI = generateDispatcherUri(dspURI, Tag.DISPATCHER_MANAGEMENT_INBOUND_PORT);
		final String avmrnop = generateDispatcherUri(dspURI, Tag.APPLICATION_VM_RELEASING_NOTIFICATION_OUTBOUND_PORT);
		final String dspdsdip = generateDispatcherUri(dspURI, Tag.DISPATCHER_DYNAMIC_STATE_DATA_INBOUND_PORT);
		
		AllocatedDispatcher adsp = new AllocatedDispatcher(
				dspURI, 
				dspmipURI, 
				avmrnop, 
				dspdsdip);
		
		return adsp;
	}
	
	@Override
	public Dispatcher createDispatcher(AllocatedDispatcher adsp) throws Exception {		
		Dispatcher dsp = new Dispatcher(
				adsp.dspURI, 
				adsp.dspmipURI, 
				adsp.avmrnopURI,
				adsp.dspdsdipURI);
		AbstractCVM.theCVM.addDeployedComponent(dsp);
		dsp.start();
		
		return dsp;
	}

	@Override
	public void acceptApplication() throws Exception {
		
		if ( !MULTIAPPLICATION && hasApplication )
			throw new Exception("An application was already started on this controller");
		
		AllocatedRequestGenerator argn = createAllocatedRequestGenerator();
		AllocatedDispatcher adsp = createAllocatedDispatcher();
		RequestGenerator rgn = createRequestGenerator(argn);
		
		@SuppressWarnings("unused")
		Dispatcher dsp = createDispatcher(adsp);
		
		ComponentDataNode dspdn = new ComponentDataNode(adsp.dspURI)
				.addPort(adsp.avmrnopURI)
				.addPort(adsp.dspdsdipURI)
				.addPort(adsp.dspmipURI);
		
		ComponentDataNode rgdn = new ComponentDataNode(argn.rgURI)
				.addPort(argn.rgmipURI)
				.addPort(argn.rgrnipURI)
				.addPort(argn.rgrsopURI);
		
		Set<String> lrpsopUris = performanceController.getURIsLike(Tag.LOGICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
		
		if ( !(lrpsopUris.size() > 0) )
			throw new Exception("No logical provider connected to [" + performanceController.uri + "]");
		
		String lrpsopURI = null;
		
		for (String uri : lrpsopUris) {
			lrpsopURI = uri;
			break;
		}
		
		LogicalResourcesProviderServicesOutboundPort lrpsop = (LogicalResourcesProviderServicesOutboundPort) findPortFromURI(lrpsopURI);
		
		AllocatedApplicationVM[] aavms = lrpsop.allocateApplicationVMs(1);
		
		if (aavms.length < 1)
			throw new NoApplicationVMException("Cannot allocate new avm for this application");
		
		performanceController.findByURI(Branch.REQUEST_GENERATORS).children.add(rgdn);
		performanceController.findByURI(Branch.DISPATCHERS).children.add(dspdn);
		
		/**
		 * Ici la demande de resources à fonctionné et il faut maintenant connecter le dispatcher, à l'avm et au requestGenerator 
		 */
		
		String rgmopURI = generateRequestGeneratorUri(performanceController.uri, Tag.REQUEST_GENERATOR_MANAGEMENT_OUTBOUND_PORT);
		RequestGeneratorManagementOutboundPort rgmop = new RequestGeneratorManagementOutboundPort(rgmopURI, this);
		addPort(rgmop);
		rgmop.publishPort();
		rgmop.doConnection(argn.rgmipURI, RequestGeneratorManagementConnector.class.getCanonicalName());
		
		performanceController.trustedConnect(rgmopURI, argn.rgmipURI);
		
		String dspmopURI = generateDispatcherUri(performanceController.uri, Tag.DISPATCHER_MANAGEMENT_OUTBOUND_PORT);
		DispatcherManagementOutboundPort dspmop = 
				new DispatcherManagementOutboundPort(dspmopURI, DispatcherManagementI.class, this);
		addPort(dspmop);
		dspmop.publishPort();
		dspmop.doConnection(adsp.dspmipURI, DispatcherManagementConnector.class.getCanonicalName());
		
		performanceController.trustedConnect(dspmopURI, adsp.dspmipURI);
		
		String dspdsdopURI = generateDispatcherUri(performanceController.uri, Tag.DISPATCHER_DYNAMIC_STATE_DATA_OUTBOUND_PORT);
		DispatcherDynamicStateDataOutboundPort dspdsdop =
				new DispatcherDynamicStateDataOutboundPort(dspdsdopURI, this);
		addPort(dspdsdop);
		dspdsdop.publishPort();
		dspdsdop.doConnection(adsp.dspdsdipURI, ControlledDataConnector.class.getCanonicalName());
		
		/** Connection du dispatcher au autres composant pour la génération dynamique des inbounds port à connecter **/
		
		String dsprnipURI = dspmop.connectToApplicationVM(aavms[0].avmrsipURI);
		String dsprsipURI = dspmop.connectToRequestGenerator(argn.rgrnipURI);
		
		adsp.dsprnipURI = dsprnipURI;
		adsp.dsprsipURI = dsprsipURI;
		
		dspdn	.addPort(dsprnipURI)
				.addPort(dsprsipURI);
		
		/** Connexion du générateur de requêtes au dispatcher **/
		
		RequestSubmissionOutboundPort rgrsop = (RequestSubmissionOutboundPort) rgn.findPortFromURI(argn.rgrsopURI);
		rgrsop.doConnection(dsprsipURI, RequestSubmissionConnector.class.getCanonicalName());
		
		rgdn.trustedConnect(argn.rgrsopURI, dsprsipURI);
		
		/** Connexion de l'avm au dispatcher (action distante) **/
		
		lrpsop.connectApplicationVM(aavms[0], adsp);
		
		
		// TODO ? rgmop.startGeneration();
		// TODO ? dspdsdop.startUnlimitedPushing(DISPATCHER_PUSHING_INTERVAL);
		hasApplication = true;
	}
	
	@Override
	public void acceptDispatcherDynamicStateData(DispatcherDynamicStateI data) {
		dspddsimap.put(data.getDispatcherURI(), data);
	}

	@Override
	public void controlLaw() throws Exception {
		
		Set<String> lrpsopUris = performanceController.getURIsLike(Tag.LOGICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
		
		if ( !(lrpsopUris.size() > 0) )
			throw new Exception("No logical provider connected to [" + performanceController.uri + "]");
		
		String lrpsopURI = null;
		
		for (String uri : lrpsopUris) {
			lrpsopURI = uri;
			break;
		}
		
		LogicalResourcesProviderServicesOutboundPort lrpsop = (LogicalResourcesProviderServicesOutboundPort) findPortFromURI(lrpsopURI);
		
		for ( String dspURI : dspddsimap.keySet() ) {
			DispatcherDynamicStateI dspdsi = dspddsimap.get(dspURI);
			
			// TODO FAIRE QUELQUE CHOSE ... (CONTROLE)
			// IF ... lrpsop.<some method>
			
		}
		
	}
	
//	protected void allocateApplicationVM(int avmCount) throws Exception {
//		
//		Set<String> lrpsopUris = performanceController.getURIsLike(Tag.LOGICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
//		
//		if ( !(lrpsopUris.size() > 0) )
//			throw new Exception("No logical provider connected to [" + performanceController.uri + "]");
//		
//		String lrpsopURI = null;
//		
//		for (String uri : lrpsopUris) {
//			lrpsopURI = uri;
//			break;
//		}
//		
//		LogicalResourcesProviderServicesOutboundPort lrpsop = (LogicalResourcesProviderServicesOutboundPort) findPortFromURI(lrpsopURI);
//		
//	}
}
