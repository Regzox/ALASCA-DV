package fr.upmc.datacenter.providers.resources.physical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.interfaces.DataRequiredI.PullI;
import fr.upmc.components.ports.PortI;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.data.interfaces.ComputerPortsDataI;
import fr.upmc.datacenter.data.interfaces.PhysicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.hardware.computer.extended.Computer;
import fr.upmc.datacenter.hardware.computer.extended.connectors.ComputerCoreReleasingConnector;
import fr.upmc.datacenter.hardware.computer.extended.interfaces.ComputerCoreReleasingI;
import fr.upmc.datacenter.hardware.computer.extended.ports.ComputerCoreReleasingOutboundPort;
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
import fr.upmc.datacenter.providers.resources.annotations.Ring;
import fr.upmc.datacenter.providers.resources.exceptions.NoCoreException;
import fr.upmc.datacenter.providers.resources.exceptions.OriginException;
import fr.upmc.datacenter.providers.resources.exceptions.OrphaneAllocatedCoreException;
import fr.upmc.datacenter.providers.resources.exceptions.RingException;
import fr.upmc.datacenter.providers.resources.physical.connectors.PhysicalResourcesProviderRequestingConnector;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderManagementI;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderRequestingI;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderServicesI;
import fr.upmc.datacenter.providers.resources.physical.ports.PhysicalResourcesProviderManagementInboundPort;
import fr.upmc.datacenter.providers.resources.physical.ports.PhysicalResourcesProviderRequestingInboundPort;
import fr.upmc.datacenter.providers.resources.physical.ports.PhysicalResourcesProviderRequestingOutboundPort;
import fr.upmc.datacenter.providers.resources.physical.ports.PhysicalResourcesProviderServicesInboundPort;
import fr.upmc.datacenter.software.enumerations.Tag;
import fr.upmc.datacenter.software.enumerations.Variation;
import fr.upmc.nodes.ComponentDataNode;

public class PhysicalResourcesProvider 
extends 	AbstractComponent
implements	PhysicalResourcesProviderManagementI,
PhysicalResourcesProviderServicesI,
PhysicalResourcesProviderRequestingI,
ComputerStateDataConsumerI
{
	public static boolean LOGGING = false;

	public ComponentDataNode physicalResourcesProvider;

	private enum Branch {COMPUTERS, PHYSICAL_RESOURCES_PROVIDERS, LOGICAL_RESOURCES_PROVIDERS}

	protected Map<String, ComputerStaticStateI> computerStaticStates;
	protected Map<String, ComputerDynamicStateI> computerDynamicStates;

	public PhysicalResourcesProvider(
			String uri,
			String physicalResourceProviderManagementInboundPortURI,
			String physicalResourceProviderRequestingInboundPortURI,
			String physicalResourceProviderServicesInboundPortURI) throws Exception
	{
		super(2, 1);
		physicalResourcesProvider = new ComponentDataNode(uri)
				.addPort(physicalResourceProviderManagementInboundPortURI)
				.addPort(physicalResourceProviderRequestingInboundPortURI)
				.addPort(physicalResourceProviderServicesInboundPortURI)
				.addChild(new ComponentDataNode(Branch.COMPUTERS))
				.addChild(new ComponentDataNode(Branch.PHYSICAL_RESOURCES_PROVIDERS))
				.addChild(new ComponentDataNode(Branch.LOGICAL_RESOURCES_PROVIDERS));

		if ( !offeredInterfaces.contains(PhysicalResourcesProviderManagementI.class) )
			offeredInterfaces.add(PhysicalResourcesProviderManagementI.class);

		PhysicalResourcesProviderManagementInboundPort prpmip = 
				new PhysicalResourcesProviderManagementInboundPort(
						physicalResourceProviderManagementInboundPortURI,
						PhysicalResourcesProviderManagementI.class, 
						this);
		addPort(prpmip);
		prpmip.publishPort();

		if ( !offeredInterfaces.contains(PhysicalResourcesProviderRequestingI.class) )
			offeredInterfaces.add(PhysicalResourcesProviderRequestingI.class);

		PhysicalResourcesProviderRequestingInboundPort prprip = 
				new PhysicalResourcesProviderRequestingInboundPort(
						physicalResourceProviderRequestingInboundPortURI, 
						PhysicalResourcesProviderRequestingI.class, 
						this);
		addPort(prprip);
		prprip.publishPort();

		if ( !offeredInterfaces.contains(PhysicalResourcesProviderServicesI.class) )
			offeredInterfaces.add(PhysicalResourcesProviderServicesI.class);
		
		PhysicalResourcesProviderServicesInboundPort prpsip = 
				new PhysicalResourcesProviderServicesInboundPort(
						physicalResourceProviderServicesInboundPortURI, 
						PhysicalResourcesProviderServicesI.class, 
						this);
		addPort(prpsip);
		prpsip.publishPort();

		computerStaticStates = new HashMap<>();
		computerDynamicStates = new HashMap<>();
	}

	private String generateComputerUri(String computerURI, Object tag) {
		return computerURI + " : " + tag.toString() + "_" + (physicalResourcesProvider.findByURI(Branch.COMPUTERS).children.size() + 1);
	}

	private String generatePhysicalResourcesProviderUri(String physicalResourcesProviderUri, Object tag) {
		return physicalResourcesProviderUri + " : " + tag.toString() + "_" + (physicalResourcesProvider.findByURI(Branch.PHYSICAL_RESOURCES_PROVIDERS).children.size() + 1);
	}

	/**
	 * Nombre de coeurs disponibles en fonction d'un {@link Computer}
	 * 
	 * @param computerURI
	 * @return
	 * @throws Exception
	 */

	protected int computerAvailableCores(String computerURI) throws Exception {
		ComponentDataNode computerDataNode = physicalResourcesProvider.findByURI(computerURI);
		String 	cdsdipURI = computerDataNode.getPortLike(Tag.COMPUTER_DYNAMIC_STATE_DATA_INBOUND_PORT),
				cdsdopURI = computerDataNode.getPortConnectedTo(cdsdipURI);

		ComputerDynamicStateDataOutboundPort cdsdop = (ComputerDynamicStateDataOutboundPort) findPortFromURI(cdsdopURI);

		ComputerDynamicState data = (ComputerDynamicState) cdsdop.request();
		boolean[][] cr = data.getCurrentCoreReservations();

		int computerAvailableCores = 0;

		for ( int pi = 0; pi < cr.length; pi++ )
			computerAvailableCores += processorAvailableCores(cr[pi]);

		return computerAvailableCores;
	}

	/**
	 * Nombre de coeurs disponibles en fonction d'un �tat de r�servation d'un processeur.
	 * 
	 * @param coreReservations �tat de r�servation des coeurs d'un processeur.
	 * @return le nombre de coeurs disponibles pour ce processeur.
	 * @throws Exception
	 */

	protected int processorAvailableCores(boolean[] coreReservations) throws Exception {
		int processorAvailableCores = 0;

		for ( int ci = 0; ci < coreReservations.length; ci++ )
			if (!coreReservations[ci])
				processorAvailableCores++;

		return processorAvailableCores;
	}

	/**
	 * Retourne vrai si l'ordinateur cible poss�de le montant de coeurs libre voulu
	 * 
	 * @param computerURI
	 * @param wanted
	 * @return
	 * @throws Exception
	 */

	protected boolean hasAvailableCoresFromComputer(String computerURI, int wanted) throws Exception {		
		return ( wanted <= computerAvailableCores(computerURI) );
	}

	/**
	 * Trouve l'uri d'un {@link Computer} poss�dant le montant de coeurs disponibles souhait�s
	 * sinon null
	 * 
	 * @param cores
	 * @return
	 * @throws Exception
	 */

	protected String findAvailableComputerForCoreAllocation(int cores) throws Exception {
		String computerURI = null;

		for ( String cURI : physicalResourcesProvider.getURIsLike(Tag.COMPUTER) ) {
			if ( cURI.contains("processor") || cURI.contains(Branch.COMPUTERS.toString()) )
				continue;

			if ( hasAvailableCoresFromComputer(cURI, cores) ) {
				computerURI = cURI;
				break;
			}
		}

		return computerURI;
	}

	@Override
	public void connectComputer(ComputerPortsDataI cpd) throws Exception {
		String 	computerURI = cpd.getUri(),
				csipURI = cpd.getComputerServicesInboundPort(),
				cssdipURI = cpd.getComputerStaticStateDataInboundPort(),
				cdsdipURI = cpd.getComputerDynamicStateDataInboundPort(),
				ccripURI = cpd.getComputerCoreReleasingInboundPort();

		ComponentDataNode cptdn = new ComponentDataNode(computerURI)
				.addPort(csipURI)
				.addPort(cssdipURI)
				.addPort(cdsdipURI)
				.addPort(ccripURI);

		String 	csopURI = generateComputerUri(computerURI, Tag.COMPUTER_SERVICES_OUTBOUND_PORT),
				cssdopURI = generateComputerUri(computerURI, Tag.COMPUTER_STATIC_STATE_DATA_OUTBOUND_PORT),
				cdsdopURI = generateComputerUri(computerURI, Tag.COMPUTER_DYNAMIC_STATE_DATA_OUTBOUND_PORT),
				ccropURI = generateComputerUri(computerURI, Tag.COMPUTER_CORE_RELEASING_OUTBOUND_PORT);

		physicalResourcesProvider.findByURI(Branch.COMPUTERS).addChild(cptdn);
	
		if ( !requiredInterfaces.contains(ComputerServicesI.class) )
			requiredInterfaces.add(ComputerServicesI.class);
		
		ComputerServicesOutboundPort csop = new ComputerServicesOutboundPort(csopURI, this);
		this.addPort(csop);
		csop.publishPort();
		csop.doConnection(csipURI, ComputerServicesConnector.class.getCanonicalName());

		physicalResourcesProvider.trustedConnect(csopURI, csipURI);
		
		if ( !requiredInterfaces.contains(PullI.class) )
			requiredInterfaces.add(PullI.class);
		
		ComputerStaticStateDataOutboundPort cssdop = new ComputerStaticStateDataOutboundPort(cssdopURI, this, computerURI);
		addPort(cssdop);
		cssdop.publishPort();
		cssdop.doConnection(cssdipURI, DataConnector.class.getCanonicalName());		

		physicalResourcesProvider.trustedConnect(cssdopURI, cssdipURI);

		if ( !requiredInterfaces.contains(ControlledPullI.class) )
			requiredInterfaces.add(ControlledPullI.class);
			
		ComputerDynamicStateDataOutboundPort cdsdop = new ComputerDynamicStateDataOutboundPort(cdsdopURI, this,	computerURI);
		addPort(cdsdop);
		cdsdop.publishPort();
		cdsdop.doConnection(cdsdipURI, ControlledDataConnector.class.getCanonicalName());

		physicalResourcesProvider.trustedConnect(cdsdopURI, cdsdipURI);

		if ( !requiredInterfaces.contains(ComputerCoreReleasingI.class) )
			requiredInterfaces.add(ComputerCoreReleasingI.class);
		
		ComputerCoreReleasingOutboundPort ccrop = new ComputerCoreReleasingOutboundPort(ccropURI, ComputerCoreReleasingI.class, this);
		addPort(ccrop);
		ccrop.publishPort();		
		ccrop.doConnection(ccripURI, ComputerCoreReleasingConnector.class.getCanonicalName());

		physicalResourcesProvider.trustedConnect(ccropURI, ccripURI);

		ComputerStaticStateI css =  (ComputerStaticStateI) cssdop.request();
		computerStaticStates.put(computerURI, css);

		for ( String processorURI : css.getProcessorURIs().values() ) {
			String 	pmipURI = css
					.getProcessorPortMap()
					.get(processorURI)
					.get(ProcessorPortTypes.MANAGEMENT),
					piipURI = css
					.getProcessorPortMap()
					.get(processorURI)
					.get(ProcessorPortTypes.INTROSPECTION);

			ComponentDataNode pdn = new ComponentDataNode(processorURI)
					.addPort(pmipURI)
					.addPort(piipURI);

			String 	pmopURI = processorURI + "-pmop",
					piopURI = processorURI + "-piop";

			cptdn.addChild(pdn);			

			if ( !requiredInterfaces.contains(ProcessorManagementI.class) )
				requiredInterfaces.add(ProcessorManagementI.class);
			
			ProcessorManagementOutboundPort pmop = new ProcessorManagementOutboundPort(pmopURI, this);
			this.addPort(pmop);
			pmop.publishPort();
			pmop.doConnection(pmipURI, ProcessorManagementConnector.class.getCanonicalName());

			physicalResourcesProvider.trustedConnect(pmopURI, pmipURI);

			if ( !requiredInterfaces.contains(ProcessorIntrospectionI.class) )
				requiredInterfaces.add(ProcessorIntrospectionI.class);
			
			ProcessorIntrospectionOutboundPort piop = new ProcessorIntrospectionOutboundPort(piopURI, this);
			this.addPort(piop);
			piop.publishPort();
			piop.doConnection(piipURI, ProcessorIntrospectionConnector.class.getCanonicalName());

			physicalResourcesProvider.trustedConnect(piopURI, piipURI);
		}
	}

	@Override
	public void disconnectComputer(ComputerPortsDataI cpd) throws Exception {
		ComponentDataNode cptdn = physicalResourcesProvider.findByURI(cpd.getUri());
		String 	csipURI = cptdn.getPortLike(Tag.COMPUTER_SERVICES_INBOUND_PORT),
				cssdipURI = cptdn.getPortLike(Tag.COMPUTER_STATIC_STATE_DATA_INBOUND_PORT),
				cdsdipURI = cptdn.getPortLike(Tag.COMPUTER_DYNAMIC_STATE_DATA_INBOUND_PORT),
				ccripURI = cptdn.getPortLike(Tag.COMPUTER_CORE_RELEASING_INBOUND_PORT),
				csopURI = cptdn.getPortConnectedTo(csipURI),
				cssdopURI = cptdn.getPortConnectedTo(cssdipURI),
				cdsdopURI = cptdn.getPortConnectedTo(cdsdipURI),
				ccropURI = cptdn.getPortConnectedTo(ccripURI);
		PortI 	csop = findPortFromURI(csopURI),
				cssdop = findPortFromURI(cssdopURI),
				cdsdop = findPortFromURI(cdsdopURI),
				ccrop = findPortFromURI(ccropURI);

		csop.doDisconnection();
		removePort(csop);
		csop.unpublishPort();
		csop.destroyPort();

		physicalResourcesProvider.disconnect(csopURI);

		cssdop.doDisconnection();
		removePort(cssdop);
		cssdop.unpublishPort();
		cssdop.destroyPort();

		physicalResourcesProvider.disconnect(cssdopURI);

		cdsdop.doDisconnection();
		removePort(cdsdop);
		cdsdop.unpublishPort();
		cdsdop.destroyPort();

		physicalResourcesProvider.disconnect(cdsdopURI);

		ccrop.doDisconnection();
		removePort(ccrop);
		ccrop.unpublishPort();
		ccrop.destroyPort();

		physicalResourcesProvider.disconnect(ccropURI);

		for ( ComponentDataNode pdn : cptdn.children ) {
			String 	pmipURI = pdn.getPortLike("pmibp"),
					piipURI = pdn.getPortLike("piibp");
			String	pmopURI = pdn.getPortConnectedTo(pmipURI),
					piopURI = pdn.getPortConnectedTo(piipURI);
			PortI 	pmop = findPortFromURI(pmopURI),
					piop = findPortFromURI(piopURI);

			pmop.doDisconnection();
			removePort(pmop);
			pmop.unpublishPort();
			pmop.destroyPort();

			physicalResourcesProvider.disconnect(pmopURI);

			piop.doDisconnection();
			removePort(piop);
			piop.unpublishPort();
			piop.destroyPort();

			physicalResourcesProvider.disconnect(piopURI);

			cptdn.removeChild(pdn);
		}

		physicalResourcesProvider.findByURI(Branch.COMPUTERS).removeChild(cptdn);
	}

	@Override
	public void connectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppd) throws Exception {
		String 	prpURI = prppd.getUri(),
				prpripURI = prppd.getPhysicalResourcesProviderRequestingInboundPort();
		ComponentDataNode prpdn = new ComponentDataNode(prpURI)
				.addPort(prpripURI);
		String 	prpropURI = generatePhysicalResourcesProviderUri(prpURI, Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

		physicalResourcesProvider.findByURI(Branch.PHYSICAL_RESOURCES_PROVIDERS).addChild(prpdn);

		if ( !requiredInterfaces.contains(PhysicalResourcesProviderRequestingI.class) )
			requiredInterfaces.add(PhysicalResourcesProviderRequestingI.class);
				
		PhysicalResourcesProviderRequestingOutboundPort prprop = 
				new PhysicalResourcesProviderRequestingOutboundPort(
						prpropURI,
						PhysicalResourcesProviderRequestingI.class,
						this);
		addPort(prprop);
		prprop.publishPort();
		prprop.doConnection(prpripURI, PhysicalResourcesProviderRequestingConnector.class.getCanonicalName());		

		physicalResourcesProvider.trustedConnect(prpropURI, prpripURI);		
	}

	@Override
	public void disconnectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppd) throws Exception {
		ComponentDataNode prpdn = physicalResourcesProvider.findByURI(prppd.getUri());
		String 	prpripURI = prppd.getPhysicalResourcesProviderRequestingInboundPort(),
				prpropURI = prpdn.getPortConnectedTo(prpripURI);
		PhysicalResourcesProviderRequestingOutboundPort prprop = 
				(PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
		prprop.doDisconnection();
		removePort(prprop);
		prprop.unpublishPort();
		prprop.destroyPort();

		physicalResourcesProvider.disconnect(prpropURI);

		physicalResourcesProvider.findByURI(Branch.PHYSICAL_RESOURCES_PROVIDERS).removeChild(prpdn);
	}

	/**
	 * Permet de faire varier la fr�quence d'un coeur d'un palier de fr�quence admissible.
	 * 
	 * @param processorURI
	 * @param coreNo
	 * @param variation
	 * @throws Exception
	 */

	protected void coreFrenquencyVariation(String processorURI, Integer coreNo, Variation variation) throws Exception {
		ComponentDataNode 	pdn = physicalResourcesProvider.findByURI(processorURI),
				cptdn = (ComponentDataNode) pdn.parents.toArray()[0];
		String				computerURI = cptdn.uri,
				pmipURI = pdn.getPortLike("pmibp"),
				piipURI = pdn.getPortLike("piibp"),
				pmopURI = pdn.getPortConnectedTo(pmipURI),
				piopURI = pdn.getPortConnectedTo(piipURI);	
		ComputerStaticStateI css = computerStaticStates.get(computerURI);
		ProcessorManagementOutboundPort pmop = (ProcessorManagementOutboundPort) findPortFromURI(pmopURI);
		ProcessorIntrospectionOutboundPort piop = (ProcessorIntrospectionOutboundPort) findPortFromURI(piopURI);
		ProcessorStaticState pss = (ProcessorStaticState) piop.getStaticState();
		ProcessorDynamicState pds = (ProcessorDynamicState) piop.getDynamicState();
		List<Integer> admissibleFrequencies = null;
		Integer frequency = null, index = null, step = null;

		if ( variation == Variation.INCREASED )
			step = 1;
		else if ( variation == Variation.DECREASED )
			step = -1;
		else {
			if ( LOGGING )
				logMessage("The variation (" + variation + ") used isn't acceptable");
			return;
		}

		if ( !css.getProcessorURIs().values().contains(processorURI) )
			throw new Exception("The computer [" + computerURI + "] doesn't contains the processor [" + processorURI + "]");

		if ( !(coreNo < css.getNumberOfCoresPerProcessor()) )
			throw new Exception("CoreNo too high, the processor [" + processorURI + "] have only " + css.getNumberOfCoresPerProcessor() + " cores");

		frequency = pds.getCurrentCoreFrequency(coreNo);

		if ( LOGGING )
			logMessage(	"On computer " + computerURI + 
					", on processor " + processorURI + 
					", the core " + coreNo + 
					", the frequency is " + frequency);

		admissibleFrequencies = new ArrayList<>(pss.getAdmissibleFrequencies());
		Collections.sort(admissibleFrequencies);
		index = admissibleFrequencies.indexOf(frequency);

		if ( index < 0 )
			throw new Exception("The current frenquency ("+ frequency +") doesn't belong to admissible frenquencies ("+ admissibleFrequencies +")");

		index += step;

		if ( index >= admissibleFrequencies.size() ) {
			if ( LOGGING )
				logMessage("On processor ["+ processorURI +"], the core (" + coreNo + ") frequency cannot be more increased");
			return;
		}
		
		if ( index < 0 ) {
			if ( LOGGING )
				logMessage("On processor ["+ processorURI +"], the core (" + coreNo + ") frequency cannot be more decreased");
			return;
		}

		if ( !piop.isCurrentlyPossibleFrequencyForCore(coreNo, admissibleFrequencies.get(index)) ) {

			int targetIndex = index;

			if ( LOGGING )
				logMessage("On computer [" + computerURI + 
						"], on processor [" + processorURI + 
						"], the core (" + coreNo + 
						"), isn't possible to up to (" + admissibleFrequencies.get(index) +
						")");

			for ( int i = 0; i < css.getNumberOfCoresPerProcessor(); i++ ) {

				if ( coreNo != i ) {
					frequency = pds.getCurrentCoreFrequency(i);
					index = admissibleFrequencies.indexOf(frequency);

					if ( index < 0 )
						throw new Exception("The current frenquency ("+ frequency +") doesn't belong to admissible frenquencies ("+ admissibleFrequencies +")");

					index += step;

					if (verifyFrenquencyStepping(computerURI, processorURI, i, admissibleFrequencies, index, variation) == Variation.STAGNATED)
						continue;

					pmop.setCoreFrequency(i, admissibleFrequencies.get(index));
				}

			}

			index = targetIndex;
		}

		verifyFrenquencyStepping(computerURI, processorURI, coreNo, admissibleFrequencies, index, variation);
		pmop.setCoreFrequency(coreNo, admissibleFrequencies.get(index));

	}

	/**
	 * Permet de faire varier la fr�quence d'un processeur d'un palier de fr�quence admissible.
	 * (Moins gourmand en transaction r�seau que pour un seul coeur)
	 * 
	 * @param processorURI
	 * @param variation
	 * @throws Exception
	 */

	protected void processorFrenquencyVariation(String processorURI, Variation variation) throws Exception {

		ComponentDataNode 	pdn = physicalResourcesProvider.findByURI(processorURI),
				cptdn = (ComponentDataNode) pdn.parents.toArray()[0];
		String				computerURI = cptdn.uri,
				pmipURI = pdn.getPortLike("pmibp"),
				piipURI = pdn.getPortLike("piibp"),
				pmopURI = pdn.getPortConnectedTo(pmipURI),
				piopURI = pdn.getPortConnectedTo(piipURI);	
		ComputerStaticStateI css = computerStaticStates.get(computerURI);
		ProcessorManagementOutboundPort pmop = (ProcessorManagementOutboundPort) findPortFromURI(pmopURI);
		ProcessorIntrospectionOutboundPort piop = (ProcessorIntrospectionOutboundPort) findPortFromURI(piopURI);
		ProcessorStaticState pss = (ProcessorStaticState) piop.getStaticState();
		ProcessorDynamicState pds = (ProcessorDynamicState) piop.getDynamicState();
		List<Integer> admissibleFrequencies = null;
		Integer frequency = null, index = null, step = null;

		if ( variation == Variation.INCREASED )
			step = 1;
		else if ( variation == Variation.DECREASED )
			step = -1;
		else {
			if ( LOGGING )
				logMessage("The variation (" + variation + ") used isn't acceptable");
			return;
		}

		if ( !css.getProcessorURIs().values().contains(processorURI) )
			throw new Exception("The computer [" + computerURI + "] doesn't contains the processor [" + processorURI + "]");

		for ( int coreNo = 0; coreNo < pss.getNumberOfCores(); coreNo++ ) {

			frequency = pds.getCurrentCoreFrequency(coreNo);

			if ( LOGGING )
				logMessage(	"On computer " + computerURI + 
						", on processor " + processorURI + 
						", the core " + coreNo + 
						", the frequency is " + frequency);

			admissibleFrequencies = new ArrayList<>(pss.getAdmissibleFrequencies());
			Collections.sort(admissibleFrequencies);
			index = admissibleFrequencies.indexOf(frequency);

			if ( index < 0 )
				throw new Exception("The current frenquency ("+ frequency +") doesn't belong to admissible frenquencies ("+ admissibleFrequencies +")");

			index += step;

			if (verifyFrenquencyStepping(computerURI, processorURI, coreNo, admissibleFrequencies, index, variation) == Variation.STAGNATED)
				continue;

			pmop.setCoreFrequency(coreNo, admissibleFrequencies.get(index));
		}
	}

	/**
	 * Permet de v�rifier que l'index est bien comprit dans l'ensemble des indexes des fr�quences admissibles en fonction de la variation souhait�e.
	 * Retourne l'effectivit� de l'index sur les fr�quences admissibles sous la forme d'une variation.
	 * Si la variation retourn�e et identique � celle pass�e en param�tres alors choisir la fr�quence pr�sente � l'index aura
	 * pour effet de faire varier la fr�quence dans le sens souhait�.
	 * 
	 * @param computerURI
	 * @param processorURI
	 * @param coreNo
	 * @param admissibleFrequencies
	 * @param index
	 * @param variation
	 * @return
	 */

	protected Variation verifyFrenquencyStepping(String computerURI, String processorURI, Integer coreNo, List<Integer> admissibleFrequencies, Integer index, Variation variation) {

		if ( variation == Variation.INCREASED ) {

			if ( (index) >= admissibleFrequencies.size() ) {

				if ( LOGGING )
					logMessage("On processor ["+ processorURI +"], the core (" + coreNo + ") frequency cannot be more increased");

				return Variation.STAGNATED;

			}

			if ( LOGGING )
				logMessage(	"Computer [" + computerURI +
						"], on processor [" + processorURI + 
						"] core (" + coreNo + 
						") frequency up to (" + admissibleFrequencies.get(index) 
						+ ")");


			return Variation.INCREASED;

		} else {

			if ( (index) < 0 ) {

				if ( LOGGING )
					logMessage("On processor ["+ processorURI +"], the core (" + coreNo + ") frequency cannot be more decreased");

				return Variation.STAGNATED;

			}

			if ( LOGGING )
				logMessage(	"Computer [" + computerURI + 
						"], on processor [" + processorURI + 
						"] core (" + coreNo + 
						") frequency down to (" + admissibleFrequencies.get(index) 
						+ ")");

			return Variation.DECREASED;
		}

	}	

	@Override
	public boolean isLocal(Object o) throws Exception {
		return physicalResourcesProvider.findByURI( ((AllocatedCore) o).processorURI) != null;
	}

	@Override
	@Ring
	public void increaseCoreFrenquency(AllocatedCore ac) throws Exception {
		if ( isLocal(ac) )
			coreFrenquencyVariation(ac.processorURI, ac.coreNo, Variation.INCREASED);
		else {
			String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			
			if ( prpropURI == null ) {
				throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
						"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
			}
			
			PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
			prprop.increaseCoreFrenquency(physicalResourcesProvider.uri, ac);
			
		}
	}

	@Override
	@Ring
	public void decreaseCoreFrenquency(AllocatedCore ac) throws Exception {
		if ( isLocal(ac) )
			coreFrenquencyVariation(ac.processorURI, ac.coreNo, Variation.DECREASED);
		else {
			String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

			if ( prpropURI == null ) {
				throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
						"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
			}

			PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
			prprop.decreaseCoreFrenquency(physicalResourcesProvider.uri, ac);
		}
	}

	@Override
	@Ring
	public void increaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		if ( isLocal(ac) )
			processorFrenquencyVariation(ac.processorURI, Variation.INCREASED);
		else {
			String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

			if ( prpropURI == null ) {
				throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
						"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
			}

			PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
			prprop.increaseProcessorFrenquency(physicalResourcesProvider.uri, ac);
		}
	}

	@Override
	@Ring
	public void decreaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		if ( isLocal(ac) )
			processorFrenquencyVariation(ac.processorURI, Variation.DECREASED);
		else {
			String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

			if ( prpropURI == null ) {
				throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
						"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
			}

			PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
			prprop.decreaseProcessorFrenquency(physicalResourcesProvider.uri, ac);
		}
	}

	@Override 
	@Ring
	public void increaseComputerFrenquency(AllocatedCore ac) throws Exception {		
		if ( isLocal(ac) ) {
			ComponentDataNode lpdn = physicalResourcesProvider.findByURI(ac.processorURI);
			ComponentDataNode cptdn = (ComponentDataNode) lpdn.parents.toArray()[0];

			for (ComponentDataNode pdn : cptdn.children)
				processorFrenquencyVariation(pdn.uri, Variation.INCREASED);
		} else {
			String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

			if ( prpropURI == null ) {
				throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
						"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
			}

			PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
			prprop.increaseComputerFrenquency(physicalResourcesProvider.uri, ac);
		}
	}

	@Override
	@Ring
	public void decreaseComputerFrenquency(AllocatedCore ac) throws Exception {
		if ( isLocal(ac) ) {
			ComponentDataNode lpdn = physicalResourcesProvider.findByURI(ac.processorURI);
			ComponentDataNode cptdn = (ComponentDataNode) lpdn.parents.toArray()[0];

			for (ComponentDataNode pdn : cptdn.children)
				processorFrenquencyVariation(pdn.uri, Variation.DECREASED);
		} else {
			String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

			if ( prpropURI == null ) {
				throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
						"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
			}

			PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
			prprop.decreaseComputerFrenquency(physicalResourcesProvider.uri, ac);
		}
	}

	@Override
	public AllocatedCore[] allocateCores(Integer cores) throws Exception {
		String computerURI = findAvailableComputerForCoreAllocation(cores);

		if ( cores <= 0 )
			throw new NoCoreException("No core available");

		if ( computerURI == null ) {

			if ( LOGGING ) {
				logMessage(	"The physicalResourceProvider [" + physicalResourcesProvider.uri + 
						"] havn't (" + cores +
						") cores to allocate on the same computer. It search for (" + (cores - 1) +
						") cores to allocate");
			}

			return allocateCores(cores - 1);		
		}

		ComponentDataNode cptdn = physicalResourcesProvider.findByURI(computerURI);
		String 	csipURI = cptdn.getPortLike(Tag.COMPUTER_SERVICES_INBOUND_PORT),
				csopURI = cptdn.getPortConnectedTo(csipURI);
		ComputerServicesOutboundPort csop = (ComputerServicesOutboundPort) findPortFromURI(csopURI);
		AllocatedCore[] acs = csop.allocateCores(cores);

		if ( LOGGING )
			logMessage(	"On computer [" + computerURI + 
					"], (" + cores + 
					") cores are been allocated");

		return acs;
	}

	@Override
	@Ring
	public AllocatedCore[] allocateCores(AllocatedCore[] acs, Integer cores) throws Exception {
		boolean areLocal = true;
		Boolean areUniform = null;
		
		if ( acs.length == 0 )
			throw new NoCoreException("No cores in allocated table");

		for ( AllocatedCore ac : acs ) {
			boolean isLocal = isLocal( ac );
			areLocal &= isLocal;
			if (areUniform == null)
				areUniform = areLocal;
			if (areUniform != isLocal)
				throw new OriginException("Allocated cores provided aren't from same computer");
		}

		if ( areLocal ) {
			ComponentDataNode pdn = physicalResourcesProvider.findByURI(acs[0].processorURI);
			ComponentDataNode cptdn = (ComponentDataNode) pdn.parents.toArray()[0];
			
			while ( !hasAvailableCoresFromComputer(cptdn.uri, cores) && cores > 0) {
				
				if ( LOGGING ) {
					logMessage(	"The physicalResourceProvider [" + physicalResourcesProvider.uri + 
							"] havn't (" + cores +
							") cores to allocate on the same computer [" + cptdn.uri +
							"]. It search for (" + (cores - 1) +
							") cores to allocate");
				}	
				cores -= 1;
			}
			
			if (cores > 0) {
				String 	csipURI = cptdn.getPortLike(Tag.COMPUTER_SERVICES_INBOUND_PORT),
						csopURI = cptdn.getPortConnectedTo(csipURI);
				ComputerServicesOutboundPort csop = (ComputerServicesOutboundPort) findPortFromURI(csopURI);
				AllocatedCore[] nacs = csop.allocateCores(cores);
				
				if ( LOGGING )
					logMessage(	"On computer [" + cptdn.uri + 
							"], (" + cores + 
							") cores are been allocated");
				
				return nacs;
			} else
				throw new NoCoreException("No available core on the physical resources provider [" + physicalResourcesProvider.uri + "]");
			
		} else {
			String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

			if ( prpropURI == null ) {
				throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
						"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
			}

			PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
			return prprop.allocateCores(physicalResourcesProvider.uri, acs, cores);
		}
	}

	@Override
	@Ring
	public void releaseCores(AllocatedCore[] allocatedCores) throws Exception {
		List<AllocatedCore> notProvidedAllocated = new ArrayList<>();
		List<AllocatedCore> providedAllocated = new ArrayList<>();
		StringBuilder message = new StringBuilder();

		for ( AllocatedCore ac : allocatedCores ) {
			if ( !isLocal(ac) )
				notProvidedAllocated.add(ac);
			else {
				providedAllocated.add(ac);
				message.append("\tOn physical resources provider [" + physicalResourcesProvider.uri + "], core (" + ac.coreNo + ") of processor [" + ac.processorURI + "] is released\n\t");
			}
		}

		if (providedAllocated.size() > 0) {
			
			HashMap<String, List<AllocatedCore>> ccropsToCallMap = new HashMap<>(); // Car on peut avoir diff�rents ordinateurs cibles pour la lib�ration et qu'il faut releaseCore sur les bons ccrops.
			
			for ( AllocatedCore ac : providedAllocated ) {
				ComponentDataNode 	pdn = physicalResourcesProvider.findByURI(ac.processorURI);
				ComponentDataNode 	cptdn = (ComponentDataNode) pdn.parents.toArray()[0];
				String 				ccripURI = cptdn.getPortLike(Tag.COMPUTER_CORE_RELEASING_INBOUND_PORT),
									ccropURI = cptdn.getPortConnectedTo(ccripURI);
								
				if ( !ccropsToCallMap.keySet().contains(ccropURI) ) {
					ccropsToCallMap.put(ccropURI, new ArrayList<>());
				}
				
				ccropsToCallMap.get(ccropURI).add(ac);
			}
			
			for ( String ccropURI : ccropsToCallMap.keySet() ) {
								
				AllocatedCore[] toReleaseLocally = new AllocatedCore[ccropsToCallMap.get(ccropURI).size()];
				
				for (int i = 0; i < ccropsToCallMap.get(ccropURI).size(); i++) {
					toReleaseLocally[i] = ccropsToCallMap.get(ccropURI).get(i);
				}
				
				ComputerCoreReleasingOutboundPort ccrop = (ComputerCoreReleasingOutboundPort) this.findPortFromURI(ccropURI);
				ccrop.releaseCores(toReleaseLocally);
				
			}

			if ( LOGGING )
				logMessage(message.toString());
		}

		if ( notProvidedAllocated.size() > 0 ) {
			
			if ( LOGGING )
				logMessage("Some allocated cores doesn't belong to this physicalResourceProvider [" + physicalResourcesProvider.uri + "]");

			String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

			if ( prpropURI == null ) {
				throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
						"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
			}

			AllocatedCore[] toReleaseElsewhere = new AllocatedCore[notProvidedAllocated.size()];
			
			for (int i = 0; i < notProvidedAllocated.size(); i++) {
				toReleaseElsewhere[i] = notProvidedAllocated.get(i);
			}
			
			PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
			prprop.releaseCores(physicalResourcesProvider.uri, toReleaseElsewhere);

		}

	}

	@Override
	public void increaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
				
		if (requesterUri == physicalResourcesProvider.uri) {
			if ( LOGGING )
				logMessage("All physical resources providers has been requested. Unfortunately, the allocate core seems orphane.");
			throw new OrphaneAllocatedCoreException("Complete physical resources providers ring loops without found the allocated core owner");
		} else {
			if ( isLocal(ac) ) {
				increaseCoreFrenquency(ac);
			} else {
				String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

				if ( prpropURI == null ) {
					throw new Exception(	"No PhysicalResourcesProviderRequestingOutboundPort found for process this not belonged AllocatedCore.\n" +
							"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
				}

				PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
				prprop.increaseCoreFrenquency(requesterUri, ac);
			}
		}

	}

	@Override
	public void decreaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {

		if (requesterUri == physicalResourcesProvider.uri) {
			if ( LOGGING )
				logMessage("All physical resources providers has been requested. Unfortunately, the allocate core seems orphane.");
			throw new OrphaneAllocatedCoreException("Complete physical resources providers ring loops without found the allocated core owner");
		} else {
			if ( isLocal(ac) ) {
				decreaseCoreFrenquency(ac);
			} else {
				String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

				if ( prpropURI == null ) {
					throw new Exception(	"No PhysicalResourcesProviderRequestingOutboundPort found for process this not belonged AllocatedCore.\n" +
							"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
				}

				PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
				prprop.decreaseCoreFrenquency(requesterUri, ac);
			}
		}

	}

	@Override
	public void increaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {

		if (requesterUri == physicalResourcesProvider.uri) {
			if ( LOGGING )
				logMessage("All physical resources providers has been requested. Unfortunately, the allocate core seems orphane.");
			throw new OrphaneAllocatedCoreException("Complete physical resources providers ring loops without found the allocated core owner");
		} else {
			if ( isLocal(ac) ) {
				increaseProcessorFrenquency(ac);
			} else {
				String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

				if ( prpropURI == null ) {
					throw new Exception(	"No PhysicalResourcesProviderRequestingOutboundPort found for process this not belonged AllocatedCore.\n" +
							"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
				}

				PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
				prprop.increaseProcessorFrenquency(requesterUri, ac);
			}
		}

	}

	@Override
	public void decreaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {

		if (requesterUri == physicalResourcesProvider.uri) {
			if ( LOGGING )
				logMessage("All physical resources providers has been requested. Unfortunately, the allocate core seems orphane.");
			throw new OrphaneAllocatedCoreException("Complete physical resources providers ring loops without found the allocated core owner");
		} else {
			if ( isLocal(ac) ) {
				decreaseProcessorFrenquency(ac);
			} else {
				String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

				if ( prpropURI == null ) {
					throw new Exception(	"No PhysicalResourcesProviderRequestingOutboundPort found for process this not belonged AllocatedCore.\n" +
							"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
				}

				PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
				prprop.decreaseProcessorFrenquency(requesterUri, ac);
			}
		}

	}

	@Override
	public void increaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		
		if (requesterUri == physicalResourcesProvider.uri) {
			if ( LOGGING )
				logMessage("All physical resources providers has been requested. Unfortunately, the allocate core seems orphane.");
			throw new OrphaneAllocatedCoreException("Complete physical resources providers ring loops without found the allocated core owner");
		} else {
			if ( isLocal(ac) ) {
				increaseComputerFrenquency(ac);
			} else {
				String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

				if ( prpropURI == null ) {
					throw new Exception(	"No PhysicalResourcesProviderRequestingOutboundPort found for process this not belonged AllocatedCore.\n" +
							"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
				}

				PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
				prprop.increaseComputerFrenquency(requesterUri, ac);
			}
		}

	}

	@Override 
	@Ring
	public void decreaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {

		if (requesterUri == physicalResourcesProvider.uri) {
			if ( LOGGING )
				logMessage("All physical resources providers has been requested. Unfortunately, the allocate core seems orphane.");
			throw new OrphaneAllocatedCoreException("Complete physical resources providers ring loops without found the allocated core owner");
		} else {
			if ( isLocal(ac) ) {
				decreaseComputerFrenquency(ac);
			} else {
				String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

				if ( prpropURI == null ) {
					throw new Exception(	"No PhysicalResourcesProviderRequestingOutboundPort found for process this not belonged AllocatedCore.\n" +
							"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
				}

				PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
				prprop.decreaseComputerFrenquency(requesterUri, ac);
			}
		}

	}

	@Override
	@Ring // TODO
	public AllocatedCore[] allocateCores(String requesterUri, AllocatedCore[] acs, Integer cores) throws Exception {
		if (requesterUri == physicalResourcesProvider.uri) {
			if ( LOGGING )
				logMessage("All physical resources providers has been requested. Unfortunately, the allocate core seems orphane.");
			throw new OrphaneAllocatedCoreException("Complete physical resources providers ring loops without found some allocated core owners");
		} else {			
			boolean areLocal = true;
			Boolean areUniform = null;

			for ( AllocatedCore ac : acs ) {
				boolean isLocal = isLocal( ac );
				areLocal &= isLocal;
				if (areUniform == null)
					areUniform = areLocal;
				if (areUniform != isLocal)
					throw new OriginException("Allocated cores provided aren't from same computer");
			}

			if ( areLocal )
				return allocateCores(acs, cores);
			else {
				String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

				if ( prpropURI == null ) {
					throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
							"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
				}

				PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
				return prprop.allocateCores(requesterUri, acs, cores);
			}

		}
	}

	@Override
	@Ring // TODO
	public void releaseCores(String requesterUri, AllocatedCore[] allocatedCores) throws Exception {
		if (requesterUri == physicalResourcesProvider.uri) {
			if ( LOGGING )
				logMessage("All physical resources providers has been requested. Unfortunately, the allocate core seems orphane.");
			throw new OrphaneAllocatedCoreException("Complete physical resources providers ring loops without found some allocated core owners");
		} else {		
			List<AllocatedCore> notProvidedAllocated = new ArrayList<>();
			List<AllocatedCore> providedAllocated = new ArrayList<>();
			StringBuilder message = new StringBuilder();

			for ( AllocatedCore ac : allocatedCores ) {
				if ( !isLocal(ac) )
					notProvidedAllocated.add(ac);
				else {
					providedAllocated.add(ac);
					message.append("\tOn physical resources provider [" + physicalResourcesProvider.uri + "], core (" + ac.coreNo + ") of processor [" + ac.processorURI + "] is released\n\t");
				}
			}

			if (providedAllocated.size() > 0) {
				ComponentDataNode 	pdn = physicalResourcesProvider.findByURI(providedAllocated.get(0).processorURI);
				ComponentDataNode 	cptdn = (ComponentDataNode) pdn.parents.toArray()[0];
				String 				ccripURI = cptdn.getPortLike(Tag.COMPUTER_CORE_RELEASING_INBOUND_PORT),
									ccropURI = cptdn.getPortConnectedTo(ccripURI);

				AllocatedCore[] toReleaseLocally = new AllocatedCore[providedAllocated.size()];
				
				for (int i = 0; i < providedAllocated.size(); i++) {
					toReleaseLocally[i] = providedAllocated.get(i);
				}
				
				ComputerCoreReleasingOutboundPort ccrop = (ComputerCoreReleasingOutboundPort) this.findPortFromURI(ccropURI);
				ccrop.releaseCores(toReleaseLocally);

				if ( LOGGING )
					logMessage(message.toString());
			}

			if ( notProvidedAllocated.size() > 0 ) {
				
				if ( LOGGING )
					logMessage("Some allocated cores doesn't belong to this physicalResourceProvider [" + physicalResourcesProvider.uri + "]");

				String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

				if ( prpropURI == null ) {
					throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
							"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
				}

				AllocatedCore[] toReleaseElsewhere = new AllocatedCore[notProvidedAllocated.size()];
				
				for (int i = 0; i < notProvidedAllocated.size(); i++) {
					toReleaseElsewhere[i] = notProvidedAllocated.get(i);
				}
				
				PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
				prprop.releaseCores(physicalResourcesProvider.uri, toReleaseElsewhere);

			}
		}
	}

	@Override
	public void acceptComputerStaticData(String computerURI, ComputerStaticStateI staticState) throws Exception {
		computerStaticStates.put(computerURI, staticState);
	}

	@Override
	public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI currentDynamicState) throws Exception {
		computerDynamicStates.put(computerURI, currentDynamicState);
	}	

}
