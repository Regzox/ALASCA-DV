package fr.upmc.datacenter.providers.resources.logical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.ports.PortI;
import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.data.interfaces.PerformanceControllerPortsDataI;
import fr.upmc.datacenter.data.interfaces.PhysicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.annotations.Ring;
import fr.upmc.datacenter.providers.resources.exceptions.NoApplicationVMException;
import fr.upmc.datacenter.providers.resources.exceptions.NoCoreException;
import fr.upmc.datacenter.providers.resources.exceptions.OrphaneApplicationVMException;
import fr.upmc.datacenter.providers.resources.logical.connectors.LogicalResourcesProviderCoreReleasingNotificationConnector;
import fr.upmc.datacenter.providers.resources.logical.connectors.LogicalResourcesProviderCoreReleasingNotifyBackConnector;
import fr.upmc.datacenter.providers.resources.logical.connectors.LogicalResourcesProviderRequestingConnector;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderCoreReleasingNotificationI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderCoreReleasingNotifyBackHandlerI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderCoreReleasingNotifyBackI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderManagementI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderRequestingI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderServicesI;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderCoreReleasingNotificationOutboundPort;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderCoreReleasingNotifyBackInboundPort;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderCoreReleasingNotifyBackOutboundPort;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderManagementInboundPort;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderRequestingInboundPort;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderRequestingOutboundPort;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderServicesInboundPort;
import fr.upmc.datacenter.providers.resources.physical.connectors.PhysicalResourcesProviderServicesConnector;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderServicesI;
import fr.upmc.datacenter.providers.resources.physical.ports.PhysicalResourcesProviderServicesOutboundPort;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.extended.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.extended.connectors.ApplicationVMCoreReleasingConnector;
import fr.upmc.datacenter.software.applicationvm.extended.interfaces.ApplicationVMCoreReleasingI;
import fr.upmc.datacenter.software.applicationvm.extended.ports.ApplicationVMCoreReleasingOutboundPort;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.CoreReleasingNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.controllers.performance.AllocatedDispatcher;
import fr.upmc.datacenter.software.enumerations.Tag;
import fr.upmc.datacenter.software.interfaces.CoreReleasingNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.CoreReleasingNotificationI;
import fr.upmc.datacenter.software.ports.CoreReleasingNotificationInboundPort;
import fr.upmc.datacenter.software.ports.CoreReleasingNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.nodes.ComponentDataNode;

/**
 * <h2> Fournisseur de ressources logiques </h2>
 * 
 * <p>
 * 
 * @author Daniel RADEAU
 *
 */

public class LogicalResourceProvider
extends		AbstractComponent
implements	LogicalResourcesProviderManagementI,
			LogicalResourcesProviderRequestingI,
			LogicalResourcesProviderServicesI,
			CoreReleasingNotificationHandlerI,
			LogicalResourcesProviderCoreReleasingNotifyBackHandlerI
{
	ComponentDataNode logicalResourcesProvider;
	
	public enum Branch {
		CONTROLLERS, 
		LOGICAL_RESOURCES_PROVIDERS, 
		PHYSICAL_RESOURCES_PROVIDERS, 
		APPLICATION_VM,
		DISPATCHER,
		PERFORMANCE_CONTROLLER,
		LOGICAL_RESOURCES_PROVIDERS_NOTIFY_BACK
	}
	
	public enum AAVMDataType {
		REQUEST_NOTIFICATION_OUTBOUND_PORT,
		CORE_RELEASING_NOTIFICATION_OUTBOUND_PORT, 
		ALLOCATED_CORES
	}
	
	class AllocatedApplicationVMCoreReleasingRequest {
		String requesterUri;
		AllocatedApplicationVM aavm;
		Integer coreCount;
		
		AllocatedApplicationVMCoreReleasingRequest(
				String requesterUri,
				AllocatedApplicationVM aavm,
				Integer coreCount) 
		{
			this.requesterUri = requesterUri;
			this.aavm = aavm;
			this.coreCount = coreCount;
		}
	}
	
	List<AllocatedApplicationVMCoreReleasingRequest> aavmcrrs;
	
	LogicalResourcesProviderDynamicState lrpds;
	
	List<AllocatedApplicationVM> waitingAVM;
	protected AllocatedApplicationVM lastAAVM;
	
	public LogicalResourceProvider(
			String uri,
			String lrpmipURI,
			String lrpripURI,
			String lrpsipURI,
			String lrpcrnbipURI) throws Exception 
	{
		super(2, 1);
		aavmcrrs = new ArrayList<>();
		lrpds = new LogicalResourcesProviderDynamicState();
		waitingAVM = new ArrayList<>();
		logicalResourcesProvider = new ComponentDataNode(uri)
				.addPort(lrpmipURI)
				.addPort(lrpripURI)
				.addPort(lrpsipURI)
				.addPort(lrpcrnbipURI)
				.addChild(new ComponentDataNode(Branch.CONTROLLERS))
				.addChild(new ComponentDataNode(Branch.LOGICAL_RESOURCES_PROVIDERS))
				.addChild(new ComponentDataNode(Branch.PHYSICAL_RESOURCES_PROVIDERS))
				.addChild(new ComponentDataNode(Branch.APPLICATION_VM))
				.addChild(new ComponentDataNode(Branch.DISPATCHER)
				.addChild(new ComponentDataNode(Branch.PERFORMANCE_CONTROLLER)))
				.addChild(new ComponentDataNode(Branch.LOGICAL_RESOURCES_PROVIDERS_NOTIFY_BACK));
		
		if ( !offeredInterfaces.contains(LogicalResourcesProviderManagementI.class) )
			addOfferedInterface(LogicalResourcesProviderManagementI.class);
		
		LogicalResourcesProviderManagementInboundPort lrpmip = 
				new LogicalResourcesProviderManagementInboundPort(
						lrpmipURI,
						LogicalResourcesProviderManagementI.class,
						this);
		addPort(lrpmip);
		lrpmip.publishPort();
		
		if ( !offeredInterfaces.contains(LogicalResourcesProviderRequestingI.class) )
			addOfferedInterface(LogicalResourcesProviderRequestingI.class);
		
		LogicalResourcesProviderRequestingInboundPort lrprip = 
				new LogicalResourcesProviderRequestingInboundPort(
						lrpripURI, 
						LogicalResourcesProviderRequestingI.class, 
						this);
		addPort(lrprip);
		lrprip.publishPort();
		
		if ( !offeredInterfaces.contains(LogicalResourcesProviderServicesI.class) )
			addOfferedInterface(LogicalResourcesProviderServicesI.class);
		
		LogicalResourcesProviderServicesInboundPort lrpsip = 
				new LogicalResourcesProviderServicesInboundPort(
						lrpsipURI, 
						LogicalResourcesProviderServicesI.class, 
						this);
		addPort(lrpsip);
		lrpsip.publishPort();
		
		if ( !offeredInterfaces.contains(LogicalResourcesProviderCoreReleasingNotifyBackI.class) )
			addOfferedInterface(LogicalResourcesProviderCoreReleasingNotifyBackI.class);
		
		LogicalResourcesProviderCoreReleasingNotifyBackInboundPort lrpcrnbip = 
				new LogicalResourcesProviderCoreReleasingNotifyBackInboundPort(
						lrpcrnbipURI, 
						LogicalResourcesProviderCoreReleasingNotifyBackI.class, 
						this);
		addPort(lrpcrnbip);
		lrpcrnbip.publishPort();
	}
	
	@Override
	public void connectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppdi) throws Exception {
		assert prppdi != null;
		
		String 	prpURI = prppdi.getUri(),
				prpsipURI = prppdi.getPhysicalResourcesProviderServicesInboundPort();
		ComponentDataNode prpdn = new ComponentDataNode(prpURI)
				.addPort(prpsipURI);
		String 	prpsopURI = generatePhysicalResourcesProviderUri(prpURI, Tag.PHYSICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);

		logicalResourcesProvider.findByURI(Branch.PHYSICAL_RESOURCES_PROVIDERS).addChild(prpdn);

		if ( !requiredInterfaces.contains(PhysicalResourcesProviderServicesI.class) )
			requiredInterfaces.add(PhysicalResourcesProviderServicesI.class);
				
		PhysicalResourcesProviderServicesOutboundPort prpsop = 
				new PhysicalResourcesProviderServicesOutboundPort(
						prpsopURI,
						PhysicalResourcesProviderServicesI.class,
						this);
		addPort(prpsop);
		prpsop.publishPort();
		prpsop.doConnection(prpsipURI, PhysicalResourcesProviderServicesConnector.class.getCanonicalName());		

		logicalResourcesProvider.trustedConnect(prpsopURI, prpsipURI);
	}

	@Override
	public void disconnectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppdi) throws Exception {
		assert prppdi != null;
		
		ComponentDataNode prpdn = logicalResourcesProvider.findByURI(prppdi.getUri());
		String 	prpsipURI = prpdn.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_SERVICES_INBOUND_PORT),
				prpsopURI = prpdn.getPortConnectedTo(prpsipURI);
		PortI 	prpsop = findPortFromURI(prpsopURI);

		prpsop.doDisconnection();
		removePort(prpsop);
		prpsop.unpublishPort();
		prpsop.destroyPort();

		logicalResourcesProvider.disconnect(prpsopURI);
		logicalResourcesProvider.findByURI(Branch.PHYSICAL_RESOURCES_PROVIDERS).removeChild(prpdn);
	}

	@Override
	public void connectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		assert lrppdi != null;
		
		String 	lrpURI = lrppdi.getUri(),
				lrpripURI = lrppdi.getLogicalResourcesProviderRequestingInboundPort();
		ComponentDataNode lrpdn = new ComponentDataNode(lrpURI)
				.addPort(lrpripURI);
		String 	lrpropURI = generateLogicalResourcesProviderUri(lrpURI, Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

		logicalResourcesProvider.findByURI(Branch.LOGICAL_RESOURCES_PROVIDERS).addChild(lrpdn);

		if ( !requiredInterfaces.contains(LogicalResourcesProviderRequestingI.class) )
			requiredInterfaces.add(LogicalResourcesProviderRequestingI.class);
				
		LogicalResourcesProviderRequestingOutboundPort lrprop = 
				new LogicalResourcesProviderRequestingOutboundPort(
						lrpropURI,
						LogicalResourcesProviderRequestingI.class,
						this);
		addPort(lrprop);
		lrprop.publishPort();
		lrprop.doConnection(lrpripURI, LogicalResourcesProviderRequestingConnector.class.getCanonicalName());		

		logicalResourcesProvider.trustedConnect(lrpropURI, lrpripURI);
	}

	@Override
	public void disconnectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		assert lrppdi != null;
		
		ComponentDataNode lrpdn = logicalResourcesProvider.findByURI(lrppdi.getUri());
		String 	lrpripURI = lrppdi.getLogicalResourcesProviderRequestingInboundPort(),
				lrpropURI = lrpdn.getPortConnectedTo(lrpripURI);
		LogicalResourcesProviderRequestingOutboundPort lrprop = 
				(LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
		lrprop.doDisconnection();
		lrprop.destroyPort();

		logicalResourcesProvider.disconnect(lrpropURI);
		logicalResourcesProvider.findByURI(Branch.LOGICAL_RESOURCES_PROVIDERS).removeChild(lrpdn);
	}

	@Override
	@Ring
	public Integer[] increaseApplicationVMFrequency(AllocatedApplicationVM aavm) throws Exception {
		assert aavm != null;
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			
			return lrprop.increaseApplicationVMFrequency(logicalResourcesProvider.uri, aavm);
		} else {
			String prpsopURI = logicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
			PhysicalResourcesProviderServicesOutboundPort prpsop = (PhysicalResourcesProviderServicesOutboundPort) findPortFromURI(prpsopURI);
			
			Set<String> processorsURI = new HashSet<>();
			List<AllocatedCore> uniprocAcs = new ArrayList<>();
			
			/** Capturer les uris des processors sur les quels l'AVM en question tourne **/
			
//			for (AllocatedCore ac : getAllocatedCores(aavm) ) {
			for (AllocatedCore ac : lrpds.getAllocatedCores(aavm) ) {
				if (processorsURI.contains(ac.processorURI))
					continue;
				else {
					processorsURI.add(ac.processorURI);
					uniprocAcs.add(ac);
				}
			}
			
			Map<String, Integer[]> processorsFrequencies = new HashMap<>();
			
			for (AllocatedCore ac : uniprocAcs) {
				processorsFrequencies.put(ac.processorURI, prpsop.increaseProcessorFrenquency(ac));
			}
			
			List<Integer> avmFrequencies = new ArrayList<>();
			
			for ( String processorURI : processorsFrequencies.keySet() ) {
//				for ( AllocatedCore ac : getAllocatedCores(aavm) ) {
				for (AllocatedCore ac : lrpds.getAllocatedCores(aavm) ) {
					if ( processorURI.equals(ac.processorURI) ) {
						avmFrequencies.add(processorsFrequencies.get(processorURI)[ac.coreNo]);
					}
				}
			}
			
			Integer[] result = new Integer[avmFrequencies.size()];
			
			for ( int i = 0; i < avmFrequencies.size(); i++ )
				result[i] = avmFrequencies.get(i);
			
			return result;
		}
	}

	@Override
	@Ring
	public Integer[] decreaseApplicationVMFrequency(AllocatedApplicationVM aavm) throws Exception {
		assert aavm != null;
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			
			return lrprop.decreaseApplicationVMFrequency(logicalResourcesProvider.uri, aavm);
		}
		else {
			String prpsopURI = logicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
			PhysicalResourcesProviderServicesOutboundPort prpsop = (PhysicalResourcesProviderServicesOutboundPort) findPortFromURI(prpsopURI);
			
			Set<String> processorsURI = new HashSet<>();
			List<AllocatedCore> uniprocAcs = new ArrayList<>();
			
			/** Capturer les uris des processors sur les quels l'AVM en question tourne **/
			
//			for (AllocatedCore ac : getAllocatedCores(aavm) ) {
			for (AllocatedCore ac : lrpds.getAllocatedCores(aavm) ) {
				if (processorsURI.contains(ac.processorURI))
					continue;
				else {
					processorsURI.add(ac.processorURI);
					uniprocAcs.add(ac);
				}
			}
			
			Map<String, Integer[]> processorsFrequencies = new HashMap<>();
			
			for (AllocatedCore ac : uniprocAcs) {
				processorsFrequencies.put(ac.processorURI, prpsop.decreaseProcessorFrenquency(ac));
			}
			
			List<Integer> avmFrequencies = new ArrayList<>();
			
			for ( String processorURI : processorsFrequencies.keySet() ) {
//				for ( AllocatedCore ac : getAllocatedCores(aavm) ) {
				for (AllocatedCore ac : lrpds.getAllocatedCores(aavm) ) {
					if ( processorURI.equals(ac.processorURI) ) {
						avmFrequencies.add(processorsFrequencies.get(processorURI)[ac.coreNo]);
					}
				}
			}
			
			Integer[] result = new Integer[avmFrequencies.size()];
			
			for ( int i = 0; i < avmFrequencies.size(); i++ )
				result[i] = avmFrequencies.get(i);
			
			return result;
		}
	}

	@Override
	@Ring
	public Integer increaseApplicationVMCores(AllocatedApplicationVM aavm, Integer coreCount) throws Exception {
		assert aavm != null;
		assert coreCount > 0;
		
		showCorePerAavm(); // TODO

		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			
			return lrprop.increaseApplicationVMCores(logicalResourcesProvider.uri, aavm, coreCount);
		} else {
			String avmmopURI = logicalResourcesProvider.getPortConnectedTo(aavm.avmmipURI);
			ApplicationVMManagementOutboundPort avmmop = (ApplicationVMManagementOutboundPort) findPortFromURI(avmmopURI);
			String prpsopURI = logicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
			PhysicalResourcesProviderServicesOutboundPort prpsop = (PhysicalResourcesProviderServicesOutboundPort) findPortFromURI(prpsopURI);
			
			AllocatedCore[] acs = null;
			try {
//				acsArray = prpsop.allocateCores(allocatedCoreListToArray(getAllocatedCores(aavm)), coreCount);
				acs = prpsop.allocateCores(lrpds.getAllocatedCores(aavm).toArray(new AllocatedCore[0]), coreCount);
				logMessage("increased count by " + acs.length);
//				getAllocatedCores(aavm).addAll(allocatedCoreArrayToList(acsArray));
				lrpds.addAllocatedCores(aavm, acs);
				avmmop.allocateCores(acs);
			} catch (ExecutionException e) {
//				e.printStackTrace();
				if ( !e.getMessage().contains(NoCoreException.class.getCanonicalName()) )
					throw e;
				logMessage("Impossible to allocated core on the same computer because has not enought cores");
			}
			
			return (acs != null) ? acs.length : 0;
		}
	}

	@Override
	@Ring
	public Integer decreaseApplicationVMCores(AllocatedApplicationVM aavm, Integer coreCount) throws Exception {
		assert aavm != null;
		assert coreCount > 0;
		
		showCorePerAavm(); // TODO
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			
			return lrprop.decreaseApplicationVMCores(logicalResourcesProvider.uri, aavm, coreCount);
		} else {
			String avmcropURI = logicalResourcesProvider.getPortConnectedTo(aavm.avmcripURI);
			ApplicationVMCoreReleasingOutboundPort avmcrop = (ApplicationVMCoreReleasingOutboundPort) findPortFromURI(avmcropURI);
			
			/**
			 * Si l'on veut libérer plus de coeurs que l'AVM n'en possède, l'AVM est alors vidée de ses coeurs,
			 * On réajustera le paramètre pour ne pas demander une libération impossible.
			 */
			
			System.out.println(lrpds.getAllocatedCores(aavm).size() + " - " + coreCount + " = " + (lrpds.getAllocatedCores(aavm).size() - coreCount));
			
			final int substraction = (lrpds.getAllocatedCores(aavm).size() - coreCount);
			
			if ( substraction <= 0 ) {
				avmcrop.releaseCores(lrpds.getAllocatedCores(aavm).size());
				return 0;				
			} else {
				avmcrop.releaseCores(coreCount);
				return substraction;
			}
			
		}
	}

	@Override
	@Ring
	public AllocatedApplicationVM[] allocateApplicationVMs(Integer avmCount) throws Exception {
		assert avmCount > 0;
		
		showCorePerAavm(); // TODO
		
		String prpsopURI = logicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
		
		if ( !(prpsopURI != null) )
			throw new Exception();
		if ( !(avmCount > 0) )
			throw new Exception();
		
		AllocatedApplicationVM[] allocAVMs = new AllocatedApplicationVM[avmCount];
		int preallocated = 0;
		
//		System.out.println("avm count : " + avmCount);
//		System.out.println("waiting avm : " + waitingAVM.size());
//		System.out.println("refs avm : " + allocatedAVMs.size());
		
		if (waitingAVM.size() > 0) {
			for ( int i = 0; i < avmCount && !waitingAVM.isEmpty(); i++, preallocated++) {
				allocAVMs[i] = waitingAVM.remove(0);
				logMessage("The waiting avm [" + allocAVMs[i].avmURI + "] was selected");
				logMessage("The selected avm has (" + lrpds.getAllocatedCores(allocAVMs[i]).size() + ") cores for running tasks");
				
				if ( lrpds.getAllocatedCores(allocAVMs[i]).size() == 0 ) {
					PhysicalResourcesProviderServicesOutboundPort prpsop = (PhysicalResourcesProviderServicesOutboundPort) findPortFromURI(prpsopURI);
					AllocatedCore[] acs = prpsop.allocateCores(1);
					logMessage("(" + acs.length + ") cores are been allocated for [" + allocAVMs[i].avmURI + "]");
					List<AllocatedCore> acsList = new ArrayList<>();
					acsList.add(acs[0]);
					lrpds.addAllocatedCores(allocAVMs[i], acsList.toArray(new AllocatedCore[0]));
					String avmmopURI = logicalResourcesProvider.getPortConnectedTo(allocAVMs[i].avmmipURI);
					ApplicationVMManagementOutboundPort avmmop = (ApplicationVMManagementOutboundPort) findPortFromURI(avmmopURI);
					avmmop.allocateCores(acs);
				}
			}
		}
		
		for ( int i = preallocated; i < avmCount; i++ ) {			
			try {
				allocAVMs[i] = createApplicationVM();
			} catch (ExecutionException e) {
				
				if ( !e.getMessage().contains(NoCoreException.class.getCanonicalName()) ) {
					throw e;
				}
				
				String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
				LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
								
				AllocatedApplicationVM[] aavms = lrprop.allocateApplicationVMs(logicalResourcesProvider.uri, avmCount - i);
				System.out.println("AAVMS GRABED : " + aavms.length);
				
				List<AllocatedApplicationVM> provided = allocatedApplicationVMArrayToList(allocAVMs);
				if (provided.contains(null))
					System.out.println("NULL DETCTED");
				List<AllocatedApplicationVM> notProvided = allocatedApplicationVMArrayToList(aavms);		
				provided.addAll(notProvided);
				
				return allocatedApplicationVMListToArray(provided);
			}
			
		}

		return allocAVMs;
	}

	@Override
	@Ring
	public AllocatedApplicationVM[] releaseApplicationVMs(AllocatedApplicationVM[] avms) throws Exception {
		assert avms != null;
		assert avms.length > 0;
		
		showCorePerAavm(); // TODO
		
		List<AllocatedApplicationVM> provided = new ArrayList<>();
		List<AllocatedApplicationVM> notProvided = new ArrayList<>();
		
		for ( int i = 0; i < avms.length; i++ ) {
//			System.out.println(avms[i].avmURI);
			if ( isLocal(avms[i]) ) {
				provided.add(avms[i]);
			} else {
				notProvided.add(avms[i]);
			}
		}
		
//		System.out.println("P : " + provided.size());
//		System.out.println("NP : " + notProvided.size());
		logReferencedApplicationVM();
		
		List<AllocatedApplicationVM> released = new ArrayList<>();
		
		for ( AllocatedApplicationVM aavm : provided ) {
			String 	avmcropURI = logicalResourcesProvider.getPortConnectedTo(aavm.avmcripURI);
			ApplicationVMCoreReleasingOutboundPort avmcrop = (ApplicationVMCoreReleasingOutboundPort) findPortFromURI(avmcropURI);
			avmcrop.releaseMaximumCores(); // On relache les coeurs des AVM pour garantir un meilleur service
			waitingAVM.add(aavm);
			
			logMessage("[" + aavm.avmURI + "] was released, reduced to 0 core and placed into waiting list");
		}
		
		released.addAll(provided);
		
		if ( notProvided.size() > 0 ) {
			AllocatedApplicationVM[] npavms = new AllocatedApplicationVM[notProvided.size()];
			
			for ( int i = 0; i < notProvided.size(); i++ )
				npavms[i] = notProvided.get(i);
			
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			AllocatedApplicationVM[] releasedElsewhere = lrprop.releaseApplicationVMs(logicalResourcesProvider.uri, npavms);
			
			released.addAll(allocatedApplicationVMArrayToList(releasedElsewhere));
		}
		
		return allocatedApplicationVMListToArray(released);
	}
	
	@Override
	@Ring
	public void connectApplicationVM(AllocatedApplicationVM aavm, AllocatedDispatcher adsp) throws Exception {
		assert aavm != null;
		assert adsp != null;
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			lrprop.connectApplicationVM(logicalResourcesProvider.uri, aavm, adsp);
		} else {
			//TODO
			RequestNotificationOutboundPort avmrnop = lrpds.getRequestNotificationOutboundPort(aavm);
			
			if ( avmrnop == null )
				throw new Exception("avmrnop not found !");
			
			
			ComponentDataNode dspdn = logicalResourcesProvider.findByURI(adsp.dspURI);
			
			if ( dspdn == null ) {
				dspdn = new ComponentDataNode(adsp.dspURI)
						.addPort(adsp.avmrnopURI)
						.addPort(adsp.dspdsdipURI)
						.addPort(adsp.dspmipURI)
						.addPort(adsp.dsprnipURI)
						.addPort(adsp.dsprsipURI);
				logicalResourcesProvider.findByURI(Branch.DISPATCHER).addChild(dspdn);
			} else {
				dspdn.addPort(adsp.dsprnipURI);
			}
			
//			System.out.println(dspdn);
			
			ComponentDataNode avmdn = logicalResourcesProvider.findByURI(aavm.avmURI);
			
			avmrnop.doConnection(adsp.dsprnipURI, RequestNotificationConnector.class.getCanonicalName());
	
			avmdn.trustedConnect(aavm.avmrnopURI, adsp.dsprnipURI);
		}
	}
	
	@Override
	@Ring
	public void disconnectApplicationVM(AllocatedApplicationVM aavm, AllocatedDispatcher adsp) throws Exception {
		assert aavm != null;
		assert adsp != null;
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			lrprop.disconnectApplicationVM(logicalResourcesProvider.uri, aavm, adsp);
		} else {
			//TODO
			RequestNotificationOutboundPort avmrnop = lrpds.getRequestNotificationOutboundPort(aavm);
			
			if ( avmrnop == null )
				throw new Exception("avmrnop not found !");
			
			ComponentDataNode dspdn = logicalResourcesProvider.findByURI(adsp.dspURI);
			ComponentDataNode avmdn = logicalResourcesProvider.findByURI(aavm.avmURI);
			avmrnop.doDisconnection();
//			System.out.println(avmdn);
//			System.out.println(dspdn);
			String dsprnipURI = avmdn.getPortConnectedTo(aavm.avmrnopURI);
			avmdn.disconnect(aavm.avmrnopURI);
			
			dspdn.removePort(dsprnipURI);
		}
	}

	@Override
	@Ring
	public Integer[] increaseApplicationVMFrequency(String requesterUri, AllocatedApplicationVM aavm) throws Exception {
		assert requesterUri != null;
		assert aavm != null;
		
//		System.out.println(">>> " + requesterUri + "/" + logicalResourcesProvider.uri + " and " + aavm.lrpURI);
		
		if ( logicalResourcesProvider.uri.equals(requesterUri) ) {
			logMessage("All logical resources providers have been requested. Unfortunately, the allocated AVM seems orphane.");
			throw new OrphaneApplicationVMException(" Orphane application VM : [" + aavm.avmURI + "]");
		}
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			return lrprop.increaseApplicationVMFrequency(requesterUri, aavm);
		} else {
			return increaseApplicationVMFrequency(aavm);
		}
	}

	@Override
	@Ring
	public Integer[] decreaseApplicationVMFrequency(String requesterUri, AllocatedApplicationVM aavm) throws Exception {
		assert requesterUri != null;
		assert aavm != null;
		
		if ( logicalResourcesProvider.uri.equals(requesterUri) ) {
			logMessage("All logical resources providers have been requested. Unfortunately, the allocated AVM seems orphane.");
			throw new OrphaneApplicationVMException(" Orphane application VM : [" + aavm.avmURI + "]");
		}
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			return lrprop.decreaseApplicationVMFrequency(requesterUri, aavm);
		} else {
			return decreaseApplicationVMFrequency(aavm);
		}
	}

	@Override
	@Ring
	public Integer increaseApplicationVMCores(String requesterUri, AllocatedApplicationVM aavm, Integer coreCount) throws Exception {
		assert requesterUri != null;
		assert aavm != null;
		assert coreCount > 0;
		
		if ( logicalResourcesProvider.uri.equals(requesterUri) ) {
			logMessage("All logical resources providers have been requested. Unfortunately, the allocated AVM seems orphane.");
			throw new OrphaneApplicationVMException(" Orphane application VM : [" + aavm.avmURI + "]");
		}
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			return lrprop.increaseApplicationVMCores(requesterUri, aavm, coreCount);
		} else {
			return increaseApplicationVMCores(aavm, coreCount);
		}
	}

	@Override
	@Ring
	public Integer decreaseApplicationVMCores(String requesterUri, AllocatedApplicationVM aavm, Integer coreCount) throws Exception {
		assert requesterUri != null;
		assert aavm != null;
		assert coreCount > 0;
		
		if ( logicalResourcesProvider.uri.equals(requesterUri) ) {
			logMessage("All logical resources providers have been requested. Unfortunately, the allocated AVM seems orphane.");
			throw new OrphaneApplicationVMException(" Orphane application VM : [" + aavm.avmURI + "]");
		}
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			return lrprop.decreaseApplicationVMCores(requesterUri, aavm, coreCount);
		} else {
			AllocatedApplicationVMCoreReleasingRequest aavmcrr = new AllocatedApplicationVMCoreReleasingRequest(requesterUri, aavm, coreCount);
			aavmcrrs.add(aavmcrr);
			return decreaseApplicationVMCores(aavm, coreCount);
		}
	}

	@Override
	@Ring
	public AllocatedApplicationVM[] allocateApplicationVMs(String requesterUri, Integer avmCount) throws Exception {
		assert requesterUri != null;
		assert avmCount > 0;
		
//		System.out.println("###### RQ : " + requesterUri + " count : " + avmCount );
		
		if ( logicalResourcesProvider.uri.equals(requesterUri) ) {
			logMessage("All logical resources providers have been requested. "
					+ "Unfortunately, (" + avmCount + ") cores for allocated (" + avmCount + ") avms are unavailables");
			return new AllocatedApplicationVM[0];
		}
		
		String prpsopURI = logicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
		
		if ( !(prpsopURI != null) )
			throw new Exception();
		if ( !(avmCount > 0) )
			throw new Exception();
		
		AllocatedApplicationVM[] allocAVMs = new AllocatedApplicationVM[avmCount];
		int preallocated = 0;
		
//		System.out.println("avm count : " + avmCount);
//		System.out.println("waiting avm : " + waitingAVM.size());
//		System.out.println("refs avm : " + allocatedAVMs.size());
		
		if (waitingAVM.size() > 0) {
			for ( int i = 0; i < avmCount && !waitingAVM.isEmpty(); i++, preallocated++) {
				allocAVMs[i] = waitingAVM.remove(0);
				logMessage("The waiting avm [" + allocAVMs[i].avmURI + "] was selected");
				logMessage("The selected avm has (" + lrpds.getAllocatedCores(allocAVMs[i]).size() + ") cores for running tasks");
				
				if ( lrpds.getAllocatedCores(allocAVMs[i]).size() == 0 ) {
					PhysicalResourcesProviderServicesOutboundPort prpsop = (PhysicalResourcesProviderServicesOutboundPort) findPortFromURI(prpsopURI);
					AllocatedCore[] acs = prpsop.allocateCores(1);
					lrpds.addAllocatedCores(allocAVMs[i], acs);
					String avmmopURI = logicalResourcesProvider.getPortConnectedTo(allocAVMs[i].avmmipURI);
					ApplicationVMManagementOutboundPort avmmop = (ApplicationVMManagementOutboundPort) findPortFromURI(avmmopURI);
					avmmop.allocateCores(acs);
				}
			}
		}
		
		for ( int i = preallocated; i < avmCount; i++ ) {
			try {			
				allocAVMs[i] = createApplicationVM();
			} catch (ExecutionException e) {
				
				if ( !e.getMessage().contains(NoCoreException.class.getCanonicalName()) ) {
					throw e;
				}
				
				String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
				LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
								
				AllocatedApplicationVM[] aavms = lrprop.allocateApplicationVMs(requesterUri, avmCount - i);
				System.out.println("AAVMS GRABED : " + aavms.length);
				
				List<AllocatedApplicationVM> provided = allocatedApplicationVMArrayToList(allocAVMs);
				if (provided.contains(null))
					System.out.println("NULL DETCTED");
				List<AllocatedApplicationVM> notProvided = allocatedApplicationVMArrayToList(aavms);		
				provided.addAll(notProvided);
				
				return allocatedApplicationVMListToArray(provided);
			}
			
		}

		return allocAVMs;
	}

	@Override
	@Ring
	public AllocatedApplicationVM[] releaseApplicationVMs(String requesterUri, AllocatedApplicationVM[] avms) throws Exception {
		assert requesterUri != null;
		assert avms != null;
		assert avms.length > 0;
		
		if ( logicalResourcesProvider.uri.equals(requesterUri) ) {
			logMessage("All logical resources providers have been requested. Unfortunately, no AVM allocable");
			throw new NoApplicationVMException(" No application VM available in the ring network");
		}
		
		List<AllocatedApplicationVM> provided = new ArrayList<>();
		List<AllocatedApplicationVM> notProvided = new ArrayList<>();
		
		for ( int i = 0; i < avms.length; i++ ) {
			if ( isLocal(avms[i]) ) {
				provided.add(avms[i]);
			} else {
				notProvided.add(avms[i]);
			}
		}
		
//		System.out.println(provided.size());
//		System.out.println(notProvided.size());
		
		List<AllocatedApplicationVM> released = new ArrayList<>();
		
		if (provided.size() > 0) {
			AllocatedApplicationVM[] aavms = releaseApplicationVMs(allocatedApplicationVMListToArray(provided));
			released.addAll(allocatedApplicationVMArrayToList(aavms));
		}
		
		String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
		LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
		
		if (notProvided.size() > 0) {
			AllocatedApplicationVM[] aavms = lrprop.releaseApplicationVMs(requesterUri, allocatedApplicationVMListToArray(notProvided));
			released.addAll(allocatedApplicationVMArrayToList(aavms));
		}
		
		return allocatedApplicationVMListToArray(released);
		
	}
	
	@Override
	@Ring
	public void connectApplicationVM(String requesterUri, AllocatedApplicationVM aavm, AllocatedDispatcher adsp)	
			throws Exception {
		assert requesterUri != null;
		assert aavm != null;
		assert adsp != null;
		
		if ( logicalResourcesProvider.uri.equals(requesterUri) ) {
			logMessage("All logical resources providers have been requested. Unfortunately, AVM seems not exist");
			throw new NoApplicationVMException(" No application VM existance in the ring network");
		}
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			lrprop.connectApplicationVM(requesterUri, aavm, adsp);
		} else {
			connectApplicationVM(aavm, adsp);
		}
	}
	
	@Override
	public void disconnectApplicationVM(String requesterUri, AllocatedApplicationVM aavm, AllocatedDispatcher adsp)
			throws Exception {
		assert requesterUri != null;
		assert aavm != null;
		assert adsp != null;
		
		if ( logicalResourcesProvider.uri.equals(requesterUri) ) {
			logMessage("All logical resources providers have been requested. Unfortunately, AVM seems not exist");
			throw new NoApplicationVMException(" No application VM existance in the ring network");
		}
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			lrprop.disconnectApplicationVM(requesterUri, aavm, adsp);
		} else {
			disconnectApplicationVM(aavm, adsp);
		}
	}

	@Override
	public boolean isLocal(Object o) throws Exception {
		assert o != null;
		return logicalResourcesProvider.findByURI( ((AllocatedApplicationVM) o).avmURI ) != null;
	}
	
	String generatePhysicalResourcesProviderUri(String lrpURI, Object tag) {
		return lrpURI + " : " + tag.toString() + "_" + (logicalResourcesProvider.findByURI(Branch.PHYSICAL_RESOURCES_PROVIDERS).children.size() + 1);
	}
	
	String generateLogicalResourcesProviderUri(String lrpURI, Object tag) {
		return lrpURI + " : " + tag.toString() + "_" + (logicalResourcesProvider.findByURI(Branch.LOGICAL_RESOURCES_PROVIDERS).children.size() + 1);
	}
	
	String generatePerformanceControllerUri(String lrpURI, Object tag) {
		return lrpURI + " : " + tag.toString() + "_" + (logicalResourcesProvider.findByURI(Branch.PERFORMANCE_CONTROLLER).children.size() + 1);
	}
	
	String generateLogicalResourcesProviderNotifyBackUri(String lrpURI, Object tag) {
		return lrpURI + " : " + tag.toString() + "_" + (logicalResourcesProvider.findByURI(Branch.LOGICAL_RESOURCES_PROVIDERS_NOTIFY_BACK).children.size() + 1);
	}
	
	String generateApplicationVMUri(String avmURI, Object tag) {
		return avmURI + " : " + tag.toString() + "_" + (logicalResourcesProvider.findByURI(Branch.APPLICATION_VM).children.size() + 1);
	}
		
	/**
	 * Création d'un AVM à partir des informations de l'AllocatedApplicationVM
	 * 
	 * @param aavm
	 * @return
	 * @throws Exception
	 */
	
	AllocatedApplicationVM createApplicationVM() throws Exception {
		
		String	avmURI = generateApplicationVMUri(logicalResourcesProvider.uri, Tag.APPLICATION_VM),
				avmmipURI = generateApplicationVMUri(avmURI, Tag.APPLICATION_VM_MANAGEMENT_INBOUND_PORT),
				avmrsipURI = generateApplicationVMUri(avmURI, Tag.REQUEST_SUBMISSION_INBOUND_PORT),
				avmrnopURI = generateApplicationVMUri(avmURI, Tag.REQUEST_NOTIFICATION_OUTBOUND_PORT),
				avmcripURI = generateApplicationVMUri(avmURI, Tag.APPLICATION_VM_CORE_RELEASING_INBOUND_PORT),
				avmcrnopURI = generateApplicationVMUri(avmURI, Tag.APPLICATION_VM_CORE_RELEASING_NOTIFICATION_OUTBOUND_PORT);
		
		AllocatedApplicationVM aavm = new AllocatedApplicationVM(
				avmURI, 
				logicalResourcesProvider.uri, 
				avmmipURI, 
				avmrsipURI, 
				avmrnopURI, 
				avmcripURI, 
				avmcrnopURI);
		
		ComponentDataNode avmdn = new ComponentDataNode(aavm.avmURI)
				.addPort(aavm.avmmipURI)
				.addPort(aavm.avmrsipURI)
				.addPort(aavm.avmrnopURI)
				.addPort(aavm.avmcripURI)
				.addPort(aavm.avmcrnopURI);
		
		logicalResourcesProvider.findByURI(Branch.APPLICATION_VM).addChild(avmdn);
		
		ApplicationVM avm = new ApplicationVM(
				aavm.avmURI, 
				aavm.avmmipURI, 
				aavm.avmrsipURI, 
				aavm.avmrnopURI, 
				aavm.avmcripURI, 
				aavm.avmcrnopURI);
		AbstractCVM.theCVM.addDeployedComponent(avm);
		avm.start();
		
		RequestNotificationOutboundPort rnop = (RequestNotificationOutboundPort) avm.findPortFromURI(aavm.avmrnopURI);
		CoreReleasingNotificationOutboundPort crnop = (CoreReleasingNotificationOutboundPort) avm.findPortFromURI(aavm.avmcrnopURI);
		
		if ( !requiredInterfaces.contains(ApplicationVMManagementI.class) )
			requiredInterfaces.add(ApplicationVMManagementI.class);

		final String avmmopURI = generateApplicationVMUri(avmURI, Tag.APPLICATION_VM_MANAGEMENT_OUTBOUND_PORT);

		ApplicationVMManagementOutboundPort avmmop = new ApplicationVMManagementOutboundPort(avmmopURI, this);
		addPort(avmmop);
		avmmop.publishPort();
		avmmop.doConnection(avmmipURI, ApplicationVMManagementConnector.class.getCanonicalName());

		logicalResourcesProvider.trustedConnect(avmmopURI, avmmipURI);
		
		if ( !requiredInterfaces.contains(ApplicationVMCoreReleasingI.class) )
			requiredInterfaces.add(ApplicationVMCoreReleasingI.class);

		final String avmcropURI = generateApplicationVMUri(avmURI, Tag.APPLICATION_VM_CORE_RELEASING_OUTBOUND_PORT);

		ApplicationVMCoreReleasingOutboundPort avmcrop = new ApplicationVMCoreReleasingOutboundPort(avmcropURI, ApplicationVMCoreReleasingI.class, this);
		addPort(avmcrop);
		avmcrop.publishPort();
		avmcrop.doConnection(avmcripURI, ApplicationVMCoreReleasingConnector.class.getCanonicalName());

		logicalResourcesProvider.trustedConnect(avmcropURI, avmcripURI);
		
		if ( !offeredInterfaces.contains(CoreReleasingNotificationI.class) )
			offeredInterfaces.add(CoreReleasingNotificationI.class);
		
		final String avmcrnipURI = generateApplicationVMUri(avmURI, Tag.APPLICATION_VM_CORE_RELEASING_NOTIFICATION_INBOUND_PORT);
		
		logicalResourcesProvider.addPort(avmcrnipURI);
		
		CoreReleasingNotificationInboundPort avmcrnip = new CoreReleasingNotificationInboundPort(avmcrnipURI, CoreReleasingNotificationI.class, this);
		addPort(avmcrnip);
		avmcrnip.publishPort();
		
		crnop.doConnection(avmcrnipURI, CoreReleasingNotificationConnector.class.getCanonicalName());
		
		avmdn.trustedConnect(avmcrnopURI, avmcrnipURI);
		
		String prpsopURI = logicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
		PhysicalResourcesProviderServicesOutboundPort prpsop = (PhysicalResourcesProviderServicesOutboundPort) findPortFromURI(prpsopURI);
		AllocatedCore[] acs = prpsop.allocateCores(1);
		
		avmmop.allocateCores(acs);
		
		lrpds.addAllocatedApplicationVM(aavm, acs, rnop, crnop);		
		
		return aavm;
	}
	
	/**
	 * Connecte le fournisseur de ressources logiques à l'avm.
	 * Les ports concernés sont ceux de gestion, libération de coeurs et de notification en cas de libération coeurs.
	 * 
	 * @param aavm
	 * @param avmcrnop
	 * @throws Exception
	 */
	
	void connectApplicationVM(AllocatedApplicationVM aavm, CoreReleasingNotificationOutboundPort avmcrnop) throws Exception {
		assert aavm != null;
		assert avmcrnop != null;
		
		
	}

	@Override
	public void acceptCoreReleasing(String avmURI, AllocatedCore allocatedCore) throws Exception {
		assert avmURI != null;
		assert allocatedCore != null;
		
		showCorePerAavm(); // TODO
		
		logMessage("[" + avmURI + "] release [" + allocatedCore.processorURI + "]'s core (" + allocatedCore.coreNo + ")");
		
		String prpsopURI = logicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
		PhysicalResourcesProviderServicesOutboundPort prpsop = (PhysicalResourcesProviderServicesOutboundPort) findPortFromURI(prpsopURI);
		
		AllocatedCore[] acsArray = new AllocatedCore[1]; 	
		acsArray[0] = allocatedCore;
		prpsop.releaseCores(acsArray);	
		
		AllocatedApplicationVM aavm = null;
		for ( AllocatedApplicationVM e : lrpds.getAllocatedApplicationVMSet())
			if (e.avmURI.equals(avmURI))
				aavm = e;
		
		logMessage("LRPDS RELEASING BEFORE");
		
		lrpds.removeAllocatedCores(aavm, acsArray);
		
		logMessage("LRPDS RELEASING PASSED");
		
		showCorePerAavm();
		
		/**
		 * Ici nous vérifions qu'il ne s'agit pas d'un demande de libération de coeurs issue d'un autre fournisseur
		 * de resources logiques. Car dans le cas d'une saturation d'un fournisseur, une avm étrangère peut être 
		 * attribue pour palier le manque. Cette avm étrangère va surement faire l'objet de manipulation de coeurs
		 * et de ce fait il sera nécessaire de retransmettre la notification de libération au détenteur de cette avm. 
		 * Dans ce cas il va falloir sauvegarder cette requête pour permettre de retransmettre l'évenement de 
		 * désallocation de coeur sur l'AVM distante. Cette opération réalise une notification en anneau à la recherche
		 * de l'émetteur de la requête de libération.
		 */
		
		AllocatedApplicationVMCoreReleasingRequest target = null;
		for (AllocatedApplicationVMCoreReleasingRequest aavmcrr : aavmcrrs) {
			if ( aavmcrr.aavm.avmURI.equals(avmURI) ) {
				target = aavmcrr;
				String lrpcrnbopURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_CORE_RELEASING_NOTIFY_BACK_OUTBOUND_PORT);
				LogicalResourcesProviderCoreReleasingNotifyBackOutboundPort lrpcrnbop = (LogicalResourcesProviderCoreReleasingNotifyBackOutboundPort) findPortFromURI(lrpcrnbopURI);
				lrpcrnbop.notifyBackCoreReleasing(aavmcrr.requesterUri, logicalResourcesProvider.uri, aavmcrr.aavm, allocatedCore);
				
				break;
			}
		}
		
		if ( target != null ) {
			target.coreCount--;
			
			System.out.println("TARGET");
			
			if ( target.coreCount == 0 )
				aavmcrrs.remove(target);
			
		} else {
			String lrpcrnopURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_CORE_RELEASING_NOTIFICATION_OUTBOUND_PORT);
			LogicalResourcesProviderCoreReleasingNotificationOutboundPort lrpcrnop = (LogicalResourcesProviderCoreReleasingNotificationOutboundPort) findPortFromURI(lrpcrnopURI);
			lrpcrnop.notifyCoreReleasing(aavm);
		}
	}
	
	public void logReferencedApplicationVM() {
		StringBuffer sb = new StringBuffer();
		int x = 1;
		sb.append("\t[" + logicalResourcesProvider.uri + "] AVM COUNT : " + logicalResourcesProvider.findByURI(Branch.APPLICATION_VM).children.size() + "\n");
		for (ComponentDataNode dn : logicalResourcesProvider.findByURI(Branch.APPLICATION_VM).children) {
			sb.append("\t\t[" + dn.uri + "]\t");
			if (x % 4 == 0)
				sb.append("\n");
			x++;
		}
		sb.append('\n');
		logMessage(sb.toString());
	}
	
	public AllocatedCore[] allocatedCoreListToArray(List<AllocatedCore> list) {
		assert !list.contains(null);
		
		AllocatedCore[] acs = new AllocatedCore[list.size()];
		
		for ( int i = 0; i < list.size(); i++ ) {
			acs[i] = list.get(i);
		}
		
		return acs;
	}
	
	public List<AllocatedCore> allocatedCoreArrayToList(AllocatedCore[] array) throws Exception {
		assert array != null;
		
		List<AllocatedCore> acs = new ArrayList<>();
		
		for ( int i = 0; i < array.length; i++ ) {
			if (array[i] == null)
				throw new Exception("Null allocated application VM the list");
			acs.add(array[i]);
		}
		
		return acs;
	}
	
	public AllocatedApplicationVM[] allocatedApplicationVMListToArray(List<AllocatedApplicationVM> list) {
		assert list != null;
		assert !list.contains(null);
		
		AllocatedApplicationVM[] acs = new AllocatedApplicationVM[list.size()];
		
		for ( int i = 0; i < list.size(); i++ ) {
			acs[i] = list.get(i);
		}
		
		return acs;
	}
	
	public List<AllocatedApplicationVM> allocatedApplicationVMArrayToList(AllocatedApplicationVM[] array) throws Exception {
		assert array != null;
		
		List<AllocatedApplicationVM> acs = new ArrayList<>();
		
		for ( int i = 0; i < array.length; i++ ) {
			if (array[i] != null)
				acs.add(array[i]);
		}
		
		assert !acs.contains(null);
		
		return acs;
	}
	

	
	protected void showCorePerAavm() throws Exception {
		StringBuilder sb = new StringBuilder();
		int i = 1;
		
		sb.append("\t").append(logicalResourcesProvider.uri).append(" CORES PER AVM : \n\n");
		
		for (AllocatedApplicationVM aavm : lrpds.getAllocatedApplicationVMSet()) {
			List<AllocatedCore> list = (List<AllocatedCore>) lrpds.getAllocatedCores(aavm);
			
			sb.append("\t\t").append(aavm.avmURI);
			if (waitingAVM.contains(aavm))
				sb.append("(Zzz)");
			sb.append(" : ").append(list.size());
			if ( (i++ % 4) == 0 )
				sb.append("\n");
		}
		sb.append("\n");
		logMessage(sb.toString());
	}

	@Override
	public void connectPerformanceController(PerformanceControllerPortsDataI pcpdi) throws Exception {		
		assert pcpdi != null;
		
		String 	pcURI = pcpdi.getUri(),
				pcrnipURI = pcpdi.getPerformanceControllerCoreReleasingNotificationInboundPortURI();
		ComponentDataNode pcdn = new ComponentDataNode(pcURI)
				.addPort(pcrnipURI);
		String 	lrpcrnopURI = generatePerformanceControllerUri(logicalResourcesProvider.uri, Tag.LOGICAL_RESOURCES_PROVIDER_CORE_RELEASING_NOTIFICATION_OUTBOUND_PORT);

		logicalResourcesProvider.findByURI(Branch.PERFORMANCE_CONTROLLER).addChild(pcdn);

		if ( !requiredInterfaces.contains(LogicalResourcesProviderCoreReleasingNotificationI.class) )
			requiredInterfaces.add(LogicalResourcesProviderCoreReleasingNotificationI.class);
				
		LogicalResourcesProviderCoreReleasingNotificationOutboundPort lrpcnrop = 
				new LogicalResourcesProviderCoreReleasingNotificationOutboundPort(
						lrpcrnopURI,
						LogicalResourcesProviderCoreReleasingNotificationI.class,
						this);
		addPort(lrpcnrop);
		lrpcnrop.publishPort();
		lrpcnrop.doConnection(pcrnipURI, LogicalResourcesProviderCoreReleasingNotificationConnector.class.getCanonicalName());		

		logicalResourcesProvider.trustedConnect(lrpcrnopURI, pcrnipURI);
	}

	@Override
	public void disconnectPerformanceController(PerformanceControllerPortsDataI pcpdi) throws Exception {
		assert pcpdi != null;
		
		ComponentDataNode pcdn = logicalResourcesProvider.findByURI(pcpdi.getUri());
		String 	pccrnipURI = pcpdi.getPerformanceControllerCoreReleasingNotificationInboundPortURI(),
				lrpcrnopURI = pcdn.getPortConnectedTo(pccrnipURI);
		LogicalResourcesProviderCoreReleasingNotificationOutboundPort lrpcnrop = 
				(LogicalResourcesProviderCoreReleasingNotificationOutboundPort) findPortFromURI(lrpcrnopURI);
		lrpcnrop.doDisconnection();
		lrpcnrop.destroyPort();

		logicalResourcesProvider.disconnect(lrpcrnopURI);
		logicalResourcesProvider.findByURI(Branch.PERFORMANCE_CONTROLLER).removeChild(pcdn);
	}

	@Override
	public void connectLogicalResourcesProviderNotifyBack(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		assert lrppdi != null;
		
		String 	lrpURI = lrppdi.getUri(),
				lrpcrnbipURI = lrppdi.getLogicalResourcesProviderCoreReleasingNotifyBackInboundPort();
		ComponentDataNode lrpdn = new ComponentDataNode(lrpURI)
				.addPort(lrpcrnbipURI);
		String 	lrpcrnbopURI = generatePerformanceControllerUri(logicalResourcesProvider.uri, Tag.LOGICAL_RESOURCES_PROVIDER_CORE_RELEASING_NOTIFY_BACK_OUTBOUND_PORT);

		logicalResourcesProvider.findByURI(Branch.LOGICAL_RESOURCES_PROVIDERS_NOTIFY_BACK).addChild(lrpdn);

		if ( !requiredInterfaces.contains(LogicalResourcesProviderCoreReleasingNotifyBackI.class) )
			requiredInterfaces.add(LogicalResourcesProviderCoreReleasingNotifyBackI.class);
				
		LogicalResourcesProviderCoreReleasingNotifyBackOutboundPort lrpcrnbop = 
				new LogicalResourcesProviderCoreReleasingNotifyBackOutboundPort(
						lrpcrnbopURI,
						LogicalResourcesProviderCoreReleasingNotifyBackI.class,
						this);
		addPort(lrpcrnbop);
		lrpcrnbop.publishPort();
		lrpcrnbop.doConnection(lrpcrnbipURI, LogicalResourcesProviderCoreReleasingNotifyBackConnector.class.getCanonicalName());		

		logicalResourcesProvider.trustedConnect(lrpcrnbopURI, lrpcrnbipURI);
	}

	@Override
	public void disconnectLogicalResourcesProviderNotifyBack(LogicalResourcesProviderPortsDataI lrppdi)
			throws Exception {
		assert lrppdi != null;
		
		ComponentDataNode lrpdn = logicalResourcesProvider.findByURI(lrppdi.getUri());
		String 	lrpcrnbipURI = lrppdi.getLogicalResourcesProviderCoreReleasingNotifyBackInboundPort(),
				lrpcrnbopURI = lrpdn.getPortConnectedTo(lrpcrnbipURI);
		LogicalResourcesProviderCoreReleasingNotificationOutboundPort lrpcrnbop = 
				(LogicalResourcesProviderCoreReleasingNotificationOutboundPort) findPortFromURI(lrpcrnbopURI);
		lrpcrnbop.doDisconnection();
		lrpcrnbop.destroyPort();

		logicalResourcesProvider.disconnect(lrpcrnbopURI);
		logicalResourcesProvider.findByURI(Branch.PERFORMANCE_CONTROLLER).removeChild(lrpdn);
	}

	@Override
	public void acceptBackCoreReleasing(String requesterUri, String answererUri, AllocatedApplicationVM aavm, AllocatedCore ac)
			throws Exception {
		if ( logicalResourcesProvider.uri.equals(answererUri) )
			throw new Exception("No notify back target found in the ring");
		if ( logicalResourcesProvider.uri.equals(requesterUri) ) {
			String lrpcrnopURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_CORE_RELEASING_NOTIFICATION_OUTBOUND_PORT);
			LogicalResourcesProviderCoreReleasingNotificationOutboundPort lrpcrnop = (LogicalResourcesProviderCoreReleasingNotificationOutboundPort) findPortFromURI(lrpcrnopURI);
			lrpcrnop.notifyCoreReleasing(aavm);
		} else {
			String lrpcrnbopURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_CORE_RELEASING_NOTIFY_BACK_OUTBOUND_PORT);
			LogicalResourcesProviderCoreReleasingNotifyBackOutboundPort lrpcrnbop = (LogicalResourcesProviderCoreReleasingNotifyBackOutboundPort) findPortFromURI(lrpcrnbopURI);
			lrpcrnbop.notifyBackCoreReleasing(requesterUri, answererUri, aavm, ac);
		}
	}

	
	
}
