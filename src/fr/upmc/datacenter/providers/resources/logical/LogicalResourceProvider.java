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
import fr.upmc.datacenter.data.interfaces.PhysicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.annotations.Ring;
import fr.upmc.datacenter.providers.resources.exceptions.NoApplicationVMException;
import fr.upmc.datacenter.providers.resources.exceptions.NoCoreException;
import fr.upmc.datacenter.providers.resources.exceptions.OrphaneApplicationVMException;
import fr.upmc.datacenter.providers.resources.logical.connectors.LogicalResourcesProviderRequestingConnector;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderManagementI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderRequestingI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderServicesI;
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
import fr.upmc.datacenter.software.enumerations.Tag;
import fr.upmc.datacenter.software.interfaces.CoreReleasingNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.CoreReleasingNotificationI;
import fr.upmc.datacenter.software.ports.CoreReleasingNotificationInboundPort;
import fr.upmc.datacenter.software.ports.CoreReleasingNotificationOutboundPort;
import fr.upmc.nodes.ComponentDataNode;

public class LogicalResourceProvider
extends		AbstractComponent
implements	LogicalResourcesProviderManagementI,
			LogicalResourcesProviderRequestingI,
			LogicalResourcesProviderServicesI,
			CoreReleasingNotificationHandlerI
{
	ComponentDataNode logicalResourcesProvider;
	
	public enum Branch {
		CONTROLLERS, 
		LOGICAL_RESOURCES_PROVIDERS, 
		PHYSICAL_RESOURCES_PROVIDERS, 
		APPLICATION_VM
	}
	
	public enum AAVMDataType {
		CORE_RELEASING_NOTIFICATION_OUTBOUND_PORT, 
		ALLOCATED_CORES,
		APPLICATION_VM
	}
	
	Map<AllocatedApplicationVM, Map<AAVMDataType, Object>> allocatedAVMs;
	List<AllocatedApplicationVM> waitingAVM;
	protected AllocatedApplicationVM lastAAVM;
	
	public LogicalResourceProvider(
			String uri,
			String lrpmipURI,
			String lrpripURI,
			String lrpsipURI) throws Exception 
	{
		super(2, 1);
		allocatedAVMs = new HashMap<>();
		waitingAVM = new ArrayList<>();
		logicalResourcesProvider = new ComponentDataNode(uri)
				.addPort(lrpmipURI)
				.addPort(lrpripURI)
				.addPort(lrpsipURI)
				.addChild(new ComponentDataNode(Branch.CONTROLLERS))
				.addChild(new ComponentDataNode(Branch.LOGICAL_RESOURCES_PROVIDERS))
				.addChild(new ComponentDataNode(Branch.PHYSICAL_RESOURCES_PROVIDERS))
				.addChild(new ComponentDataNode(Branch.APPLICATION_VM));
		
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
	}
	
	@Override
	public void connectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppdi) throws Exception {
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
		ComponentDataNode lrpdn = logicalResourcesProvider.findByURI(lrppdi.getUri());
		String 	lrpripURI = lrppdi.getLogicalResourcesProviderRequestingInboundPort(),
				lrpropURI = lrpdn.getPortConnectedTo(lrpripURI);
		LogicalResourcesProviderRequestingOutboundPort lrprop = 
				(LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
		lrprop.doDisconnection();
		removePort(lrprop);
		lrprop.unpublishPort();
		lrprop.destroyPort();

		logicalResourcesProvider.disconnect(lrpropURI);
		logicalResourcesProvider.findByURI(Branch.LOGICAL_RESOURCES_PROVIDERS).removeChild(lrpdn);
	}

	@Override
	public void increaseApplicationVMFrequency(AllocatedApplicationVM aavm) throws Exception {
		// TODO Auto-generated method stub
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			
			lrprop.increaseApplicationVMFrequency(logicalResourcesProvider.uri, aavm);
		} else {
			String prpsopURI = logicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
			PhysicalResourcesProviderServicesOutboundPort prpsop = (PhysicalResourcesProviderServicesOutboundPort) findPortFromURI(prpsopURI);
			
			Set<String> processorsURI = new HashSet<>();
			List<AllocatedCore> uniprocAcs = new ArrayList<>();
			
			/** Capturer les uris des processors sur les quels l'AVM en question tourne **/
			
			logReferencedApplicationVM();
			System.out.println(logicalResourcesProvider.uri);
			System.out.println(isLocal(aavm));
			
			logMessage(aavm.avmURI);
			
			for (AllocatedCore ac : getAllocatedCores(aavm) ) {
				if (processorsURI.contains(ac.processorURI))
					continue;
				else {
					processorsURI.add(ac.processorURI);
					uniprocAcs.add(ac);
				}
			}
			
			for (AllocatedCore ac : uniprocAcs)
				prpsop.increaseProcessorFrenquency(ac);
		}
	}

	@Override
	public void decreaseApplicationVMFrequency(AllocatedApplicationVM aavm) throws Exception {
		// TODO Auto-generated method stub
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			
			lrprop.decreaseApplicationVMFrequency(logicalResourcesProvider.uri, aavm);
		}
		else {
			String prpsopURI = logicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
			PhysicalResourcesProviderServicesOutboundPort prpsop = (PhysicalResourcesProviderServicesOutboundPort) findPortFromURI(prpsopURI);
			
			Set<String> processorsURI = new HashSet<>();
			List<AllocatedCore> uniprocAcs = new ArrayList<>();
			
			/** Capturer les uris des processors sur les quels l'AVM en question tourne **/
			
			for (AllocatedCore ac : getAllocatedCores(aavm) ) {
				if (processorsURI.contains(ac.processorURI))
					continue;
				else {
					processorsURI.add(ac.processorURI);
					uniprocAcs.add(ac);
				}
			}
			
			for (AllocatedCore ac : uniprocAcs)
				prpsop.decreaseProcessorFrenquency(ac);
		}
	}

	@Override
	public void increaseApplicationVMCores(AllocatedApplicationVM aavm, Integer coreCount) throws Exception {
		// TODO Auto-generated method stub
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			
			lrprop.increaseApplicationVMCores(logicalResourcesProvider.uri, aavm, coreCount);
		} else {
			String avmmopURI = logicalResourcesProvider.getPortConnectedTo(aavm.avmmipURI);
			ApplicationVMManagementOutboundPort avmmop = (ApplicationVMManagementOutboundPort) findPortFromURI(avmmopURI);
			String prpsopURI = logicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
			PhysicalResourcesProviderServicesOutboundPort prpsop = (PhysicalResourcesProviderServicesOutboundPort) findPortFromURI(prpsopURI);
			
			AllocatedCore[] acsArray = null;
			try {
				acsArray = prpsop.allocateCores(allocatedCoreListToArray(getAllocatedCores(aavm)), coreCount);
				logMessage("increased count by " + acsArray.length);
				getAllocatedCores(aavm).addAll(allocatedCoreArrayToList(acsArray));
				avmmop.allocateCores(acsArray);
			} catch (ExecutionException e) {
				if ( !e.getMessage().contains(NoCoreException.class.getCanonicalName()) )
					throw e;
				logMessage("IMPOSSIBLE TO ALLOCATE CORES !");
				/** TODO STRATEGIE ANTI AMV DORMANTES : élimination des de la moitié des AVM en attente -1 <3 **/
				
//				Integer waitingCount = waitingAVM.size() / 2;
//				
//				for ( int i = 0; i < waitingCount; i++ ) {
//					AllocatedApplicationVM waavm = waitingAVM.remove(0);
//					decreaseApplicationVMCores(waavm, -1);
//				}
			}
		}
	}

	@Override
	public void decreaseApplicationVMCores(AllocatedApplicationVM aavm, Integer coreCount) throws Exception {
		// TODO Auto-generated method stub
		if ( !isLocal(aavm) )
			decreaseApplicationVMCores(logicalResourcesProvider.uri, aavm, coreCount);
		else {
			String avmcropURI = logicalResourcesProvider.getPortConnectedTo(aavm.avmcripURI);
			ApplicationVMCoreReleasingOutboundPort avmcrop = (ApplicationVMCoreReleasingOutboundPort) findPortFromURI(avmcropURI);
			avmcrop.releaseCores(coreCount);
		}
	}

	@Override
	public AllocatedApplicationVM[] allocateApplicationVMs(Integer avmCount) throws Exception {
		String prpsopURI = logicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
		
		if ( !(prpsopURI != null) )
			throw new Exception();
		if ( !(avmCount > 0) )
			throw new Exception();
		
		AllocatedApplicationVM[] allocAVMs = new AllocatedApplicationVM[avmCount];
		int preallocated = 0;
		
		System.out.println("avm count : " + avmCount);
		System.out.println("waiting avm : " + waitingAVM.size());
		System.out.println("refs avm : " + allocatedAVMs.size());
		
		if (waitingAVM.size() > 0)
			for ( int i = 0; i < avmCount && !waitingAVM.isEmpty(); i++, preallocated++) {
				allocAVMs[i] = waitingAVM.remove(0);
				logMessage("The waiting avm [" + allocAVMs[i].avmURI + "] was selected");
			}
		
		for ( int i = preallocated; i < avmCount; i++ ) {
			AllocatedApplicationVM aavm = createAllocatedApplicationVM();
			
			try {
				
				PhysicalResourcesProviderServicesOutboundPort prpsop = (PhysicalResourcesProviderServicesOutboundPort) findPortFromURI(prpsopURI);
				AllocatedCore[] acs = prpsop.allocateCores(1);
				
				createApplicationVM(aavm);
				connectApplicationVM(aavm, getApplicationVMCoreReleasingNotificationOutboundPort(aavm));
				
				List<AllocatedCore> acsList = new ArrayList<>();
				acsList.add(acs[0]);
				setAllocatedCores(aavm, allocatedCoreListToArray(acsList));
				
				String avmmopURI = logicalResourcesProvider.getPortConnectedTo(aavm.avmmipURI);
				ApplicationVMManagementOutboundPort avmmop = (ApplicationVMManagementOutboundPort) findPortFromURI(avmmopURI);
				avmmop.allocateCores(acs);
				allocAVMs[i] = aavm;
				
				logMessage( "A new application VM is created with " + acs.length + " cores allocated");
				
			} catch (ExecutionException e) {
				
				if ( !e.getMessage().contains(NoCoreException.class.getCanonicalName()) ) {
					throw e;
				}
				
//				if ( allocatedAVMs.remove(aavm) == null) {
//					logMessage("Impossible to find the application VM uri in known uris");
//					throw new Exception("Remove failure");
//				}
//				
//				logMessage("CORELESS : " + logicalResourcesProvider.findByURI(aavm.avmURI).uri);
				
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
	public void releaseApplicationVMs(AllocatedApplicationVM[] avms) throws Exception {
		List<AllocatedApplicationVM> provided = new ArrayList<>();
		List<AllocatedApplicationVM> notProvided = new ArrayList<>();
		
		for ( int i = 0; i < avms.length; i++ ) {
			System.out.println(avms[i].avmURI);
			if ( isLocal(avms[i]) ) {
				provided.add(avms[i]);
			} else {
				notProvided.add(avms[i]);
			}
		}
		
		System.out.println("P : " + provided.size());
		System.out.println("NP : " + notProvided.size());
		logReferencedApplicationVM();
		
		for ( AllocatedApplicationVM aavm : provided ) {
			String 	avmcropURI = logicalResourcesProvider.getPortConnectedTo(aavm.avmcripURI);
			ApplicationVMCoreReleasingOutboundPort avmcrop = (ApplicationVMCoreReleasingOutboundPort) findPortFromURI(avmcropURI);
			avmcrop.releaseMaximumCores(); // On peut se permettre de réduire jusqu'à 1 le nombre de coeurs pour les autres AVM en travail.
			waitingAVM.add(aavm);
			
			logMessage("[" + aavm.avmURI + "] was released, reduced to 1 core and placed into waiting list");
		}
		
		if ( notProvided.size() > 0 ) {
			AllocatedApplicationVM[] npavms = new AllocatedApplicationVM[notProvided.size()];
			
			for ( int i = 0; i < notProvided.size(); i++ )
				npavms[i] = notProvided.get(i);
			
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			lrprop.releaseApplicationVMs(logicalResourcesProvider.uri, npavms);
		}
	}

	@Override
	public void increaseApplicationVMFrequency(String requesterUri, AllocatedApplicationVM aavm) throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println(">>> " + requesterUri + "/" + logicalResourcesProvider.uri + " and " + aavm.lrpURI);
		
		if ( logicalResourcesProvider.uri.equals(requesterUri) ) {
			logMessage("All logical resources providers have been requested. Unfortunately, the allocated AVM seems orphane.");
			throw new OrphaneApplicationVMException(" Orphane application VM : [" + aavm.avmURI + "]");
		}
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			lrprop.increaseApplicationVMFrequency(requesterUri, aavm);
		} else {
			increaseApplicationVMFrequency(aavm);
		}
	}

	@Override
	public void decreaseApplicationVMFrequency(String requesterUri, AllocatedApplicationVM aavm) throws Exception {
		// TODO Auto-generated method stub
		if ( logicalResourcesProvider.uri.equals(requesterUri) ) {
			logMessage("All logical resources providers have been requested. Unfortunately, the allocated AVM seems orphane.");
			throw new OrphaneApplicationVMException(" Orphane application VM : [" + aavm.avmURI + "]");
		}
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			lrprop.decreaseApplicationVMFrequency(requesterUri, aavm);
		} else {
			decreaseApplicationVMFrequency(aavm);
		}
	}

	@Override
	public void increaseApplicationVMCores(String requesterUri, AllocatedApplicationVM aavm, Integer coreCount) throws Exception {
		// TODO Auto-generated method stub
		
		if ( logicalResourcesProvider.uri.equals(requesterUri) ) {
			logMessage("All logical resources providers have been requested. Unfortunately, the allocated AVM seems orphane.");
			throw new OrphaneApplicationVMException(" Orphane application VM : [" + aavm.avmURI + "]");
		}
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			lrprop.increaseApplicationVMCores(requesterUri, aavm, coreCount);
		} else {
			increaseApplicationVMCores(aavm, coreCount);
		}
	}

	@Override
	public void decreaseApplicationVMCores(String requesterUri, AllocatedApplicationVM aavm, Integer coreCount) throws Exception {
		// TODO Auto-generated method stub
		
		if ( logicalResourcesProvider.uri.equals(requesterUri) ) {
			logMessage("All logical resources providers have been requested. Unfortunately, the allocated AVM seems orphane.");
			throw new OrphaneApplicationVMException(" Orphane application VM : [" + aavm.avmURI + "]");
		}
		
		if ( !isLocal(aavm) ) {
			String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
			lrprop.decreaseApplicationVMCores(requesterUri, aavm, coreCount);
		} else {
			decreaseApplicationVMCores(aavm, coreCount);
		}
	}

	@Override
	public AllocatedApplicationVM[] allocateApplicationVMs(String requesterUri, Integer avmCount) throws Exception {
		// TODO Auto-generated method stub
		
		if ( logicalResourcesProvider.uri.equals(requesterUri) ) {
			logMessage("All logical resources providers have been requested. Unfortunately, no AVM allocable");
			throw new NoApplicationVMException(" No application VM available in the ring network");
		}
		
		try {
			return allocateApplicationVMs(avmCount);
		} catch (ExecutionException e) {
			if (!e.getMessage().contains(NoCoreException.class.getCanonicalName()))
				throw e;
		}
		
		String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
		LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
		return lrprop.allocateApplicationVMs(requesterUri, avmCount);
		
	}

	@Override
	public void releaseApplicationVMs(String requesterUri, AllocatedApplicationVM[] avms) throws Exception {
		// TODO Auto-generated method stub
		
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
		
		System.out.println(provided.size());
		System.out.println(notProvided.size());
		
		if (provided.size() > 0)
			releaseApplicationVMs(allocatedApplicationVMListToArray(provided));
		
		String lrpropURI = logicalResourcesProvider.getPortLike(Tag.LOGICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
		LogicalResourcesProviderRequestingOutboundPort lrprop = (LogicalResourcesProviderRequestingOutboundPort) findPortFromURI(lrpropURI);
		
		if (notProvided.size() > 0) {
			lrprop.releaseApplicationVMs(requesterUri, allocatedApplicationVMListToArray(notProvided));
		}
		
	}

	@Override
	public boolean isLocal(Object o) throws Exception {
		return logicalResourcesProvider.findByURI( ((AllocatedApplicationVM) o).avmURI ) != null;
	}
	
	String generatePhysicalResourcesProviderUri(String lrpURI, Object tag) {
		return lrpURI + " : " + tag.toString() + "_" + (logicalResourcesProvider.findByURI(Branch.PHYSICAL_RESOURCES_PROVIDERS).children.size() + 1);
	}
	
	String generateLogicalResourcesProviderUri(String lrpURI, Object tag) {
		return lrpURI + " : " + tag.toString() + "_" + (logicalResourcesProvider.findByURI(Branch.LOGICAL_RESOURCES_PROVIDERS).children.size() + 1);
	}
	
	String generateApplicationVMUri(String avmURI, Object tag) {
		return avmURI + " : " + tag.toString() + "_" + (logicalResourcesProvider.findByURI(Branch.APPLICATION_VM).children.size() + 1);
	}
	
	AllocatedApplicationVM createAllocatedApplicationVM() throws Exception {
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
		
		return aavm;
	}
	
	AllocatedApplicationVM createApplicationVM(AllocatedApplicationVM aavm) throws Exception {
		
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
		
		setApplicationVMCoreReleasingNotificationOutboundPort(aavm, (CoreReleasingNotificationOutboundPort) avm.findPortFromURI(aavm.avmcrnopURI));
		
		return aavm;
	}
	
	void connectApplicationVM(AllocatedApplicationVM aavm, CoreReleasingNotificationOutboundPort avmcrnop) throws Exception {
		String	avmURI = aavm.avmURI,
				avmmipURI = aavm.avmmipURI,
				avmcripURI = aavm.avmcripURI,
				avmcrnopURI = aavm.avmcrnopURI;
		
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
		
		avmcrnop.doConnection(avmcrnipURI, CoreReleasingNotificationConnector.class.getCanonicalName());
		
		ComponentDataNode avmdn = logicalResourcesProvider.findByURI(avmURI);
		avmdn.trustedConnect(avmcrnopURI, avmcrnipURI);
	}

	@Override
	public void acceptCoreReleasing(String avmURI, AllocatedCore allocatedCore) throws Exception {
		logMessage("[" + avmURI + "] release [" + allocatedCore.processorURI + "]'s core (" + allocatedCore.coreNo + ")");
		
		String prpsopURI = logicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_SERVICES_OUTBOUND_PORT);
		PhysicalResourcesProviderServicesOutboundPort prpsop = (PhysicalResourcesProviderServicesOutboundPort) findPortFromURI(prpsopURI);
		
		AllocatedCore[] acsArray = new AllocatedCore[1]; 	
		acsArray[0] = allocatedCore;
		prpsop.releaseCores(acsArray);	
		
		AllocatedApplicationVM aavm = null;
		for ( AllocatedApplicationVM e : allocatedAVMs.keySet())
			if (e.avmURI.equals(avmURI))
				aavm = e;
		getAllocatedCores(aavm).removeAll(allocatedCoreArrayToList(acsArray));			
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
		AllocatedCore[] acs = new AllocatedCore[list.size()];
		
		for ( int i = 0; i < list.size(); i++ ) {
			acs[i] = list.get(i);
		}
		
		return acs;
	}
	
	public List<AllocatedCore> allocatedCoreArrayToList(AllocatedCore[] array) {
		List<AllocatedCore> acs = new ArrayList<>();
		
		for ( int i = 0; i < array.length; i++ ) {
			if (array[i] != null)
				acs.add(array[i]);
		}
		
		return acs;
	}
	
	public AllocatedApplicationVM[] allocatedApplicationVMListToArray(List<AllocatedApplicationVM> list) {
		AllocatedApplicationVM[] acs = new AllocatedApplicationVM[list.size()];
		
		for ( int i = 0; i < list.size(); i++ ) {
			acs[i] = list.get(i);
		}
		
		return acs;
	}
	
	public List<AllocatedApplicationVM> allocatedApplicationVMArrayToList(AllocatedApplicationVM[] array) {
		List<AllocatedApplicationVM> acs = new ArrayList<>();
		
		for ( int i = 0; i < array.length; i++ ) {
			if (array[i] != null)
				acs.add(array[i]);
		}
		
		return acs;
	}
	
	@SuppressWarnings("unchecked")
	protected List<AllocatedCore> getAllocatedCores(AllocatedApplicationVM aavm) {
		Object object = allocatedAVMs.get(aavm).get(AAVMDataType.ALLOCATED_CORES);
		List<AllocatedCore> list = (List<AllocatedCore>) object;
		return list;
		
	}
	
	protected void setAllocatedCores(AllocatedApplicationVM aavm, AllocatedCore[] acs) {
		Map<AAVMDataType, Object> map = new HashMap<>();
		map.put(AAVMDataType.ALLOCATED_CORES, allocatedCoreArrayToList(acs));
		allocatedAVMs.put(aavm, map);
	}
	
	protected CoreReleasingNotificationOutboundPort getApplicationVMCoreReleasingNotificationOutboundPort(AllocatedApplicationVM aavm) {
		Object object = allocatedAVMs.get(aavm).get(AAVMDataType.CORE_RELEASING_NOTIFICATION_OUTBOUND_PORT);
		CoreReleasingNotificationOutboundPort port = (CoreReleasingNotificationOutboundPort) object;
		return port;	
	}
	
	protected void setApplicationVMCoreReleasingNotificationOutboundPort(AllocatedApplicationVM aavm, CoreReleasingNotificationOutboundPort avmcrop) {
		Map<AAVMDataType, Object> map = new HashMap<>();
		map.put(AAVMDataType.CORE_RELEASING_NOTIFICATION_OUTBOUND_PORT, avmcrop);
		allocatedAVMs.put(aavm, map);
	}
	
	protected void setAllocatedApplicationVM(AllocatedApplicationVM aavm) {
		allocatedAVMs.put(aavm, null);
	}

}
