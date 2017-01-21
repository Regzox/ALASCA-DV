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
import fr.upmc.datacenter.hardware.processors.Core;
import fr.upmc.datacenter.hardware.processors.Processor;
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

/**
 * Fournisseur de resources physiques. ({@link PhysicalResourcesProvider})<br><br>
 * Les fournisseurs de resources physiques sont en commmunication directe avec les {@link Computer}.<br>
 * Ils propose une interface de services {@link PhysicalResourcesProviderServicesI} permettant de réaliser des oppérations sur les {@link Core}
 * des {@link Processor} contenus dans les {@link Computer} auquels ils sont connectés.<br>
 * De plus les fournisseurs de resources physiques ont la possibilité d'être connectés en anneaux.<br>
 * Pour celà ils disposent d'un {@link PhysicalResourcesProviderRequestingInboundPort} au peut se connecter
 * un autre fournisseur de resources physiques. Cette connexion en anneau permet de la même façon qu'un autre
 * composant serait connecté au {@link PhysicalResourcesProviderServicesInboundPort} de demander une action à 
 * réaliser sur les {@link Core}.<br> 
 * Toute action demandée sur un {@link Core} est toujours réalisée au près du {@link PhysicalResourcesProvider} qui le possède.<br> 
 * L'unité d'échange du {@link PhysicalResourcesProvider} est
 * le {@link AllocatedCore}.<br> 
 * C'est par le biais d'un {@link AllocatedCore} que toute action sur les {@link Core}
 * est autorisée.<br>
 * Les {@link AllocatedCore} peuvent être considérés comme des jetons uniques permettant l'utilisation
 * des services proposés par le {@link PhysicalResourcesProvider}.
 * 
 * @author Daniel RADEAU
 *
 */

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
		
		assert uri != null;
		assert physicalResourceProviderManagementInboundPortURI != null;
		assert physicalResourceProviderRequestingInboundPortURI != null;
		assert physicalResourceProviderServicesInboundPortURI != null;
		
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
		assert computerURI != null; 
		
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
	 * Nombre de coeurs disponibles en fonction d'un état de réservation d'un processeur.
	 * 
	 * @param coreReservations état de réservation des coeurs d'un processeur.
	 * @return le nombre de coeurs disponibles pour ce processeur.
	 * @throws Exception
	 */

	protected int processorAvailableCores(boolean[] coreReservations) throws Exception {
		assert coreReservations != null;
		
		int processorAvailableCores = 0;

		for ( int ci = 0; ci < coreReservations.length; ci++ )
			if (!coreReservations[ci])
				processorAvailableCores++;

		return processorAvailableCores;
	}

	/**
	 * Retourne vrai si l'ordinateur cible possède le montant de coeurs libre voulu
	 * 
	 * @param computerURI
	 * @param wanted
	 * @return
	 * @throws Exception
	 */

	protected boolean hasAvailableCoresFromComputer(String computerURI, int wanted) throws Exception {
		assert computerURI != null;
		assert wanted >= 0;
		
		return ( wanted <= computerAvailableCores(computerURI) );
	}

	/**
	 * Trouve l'uri d'un {@link Computer} possédant le montant de coeurs disponibles souhaités
	 * sinon null
	 * 
	 * @param cores
	 * @return
	 * @throws Exception
	 */

	protected String findAvailableComputerForCoreAllocation(int cores) throws Exception {
		assert cores >= 0;
		
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
		assert cpd != null;
		
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
	 * Permet de faire varier la fréquence d'un coeur d'un palier de fréquence admissible.
	 * 
	 * @param processorURI
	 * @param coreNo
	 * @param variation
	 * @throws Exception
	 */

	protected Integer coreFrenquencyVariation(String processorURI, Integer coreNo, Variation variation) throws Exception {
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
			return null;
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
			return frequency;
		}
		
		if ( index < 0 ) {
			if ( LOGGING )
				logMessage("On processor ["+ processorURI +"], the core (" + coreNo + ") frequency cannot be more decreased");
			return frequency;
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
		return admissibleFrequencies.get(index);
	}

	/**
	 * Permet de faire varier la fréquence d'un processeur d'un palier de fréquence admissible.
	 * (Moins gourmand en transaction réseau que pour un seul coeur)
	 * 
	 * @param processorURI
	 * @param variation
	 * @throws Exception
	 */

	protected Integer[] processorFrenquencyVariation(String processorURI, Variation variation) throws Exception {

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
			return null;
		}

		if ( !css.getProcessorURIs().values().contains(processorURI) )
			throw new Exception("The computer [" + computerURI + "] doesn't contains the processor [" + processorURI + "]");

		Integer[] processorFrequencies = new Integer[pss.getNumberOfCores()];
		
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

			processorFrequencies[coreNo] = admissibleFrequencies.get(index);
			
			index += step;

			if (verifyFrenquencyStepping(computerURI, processorURI, coreNo, admissibleFrequencies, index, variation) == Variation.STAGNATED)
				continue;

			processorFrequencies[coreNo] = admissibleFrequencies.get(index);
			
			pmop.setCoreFrequency(coreNo, admissibleFrequencies.get(index));
		}
		
		return processorFrequencies;
	}

	/**
	 * Permet de vérifier que l'index est bien comprit dans l'ensemble des indexes des fréquences admissibles en fonction de la variation souhaitée.
	 * Retourne l'effectivité de l'index sur les fréquences admissibles sous la forme d'une variation.
	 * Si la variation retournée et identique à celle passée en paramètres alors choisir la fréquence présente à l'index aura
	 * pour effet de faire varier la fréquence dans le sens souhaité.
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
		assert o != null;
		return physicalResourcesProvider.findByURI( ((AllocatedCore) o).processorURI) != null;
	}

	@Override
	@Ring
	public Integer increaseCoreFrenquency(AllocatedCore ac) throws Exception {
		if ( isLocal(ac) )
			return coreFrenquencyVariation(ac.processorURI, ac.coreNo, Variation.INCREASED);
		else {
			String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);
			
			if ( prpropURI == null ) {
				throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
						"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
			}
			
			PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
			return prprop.increaseCoreFrenquency(physicalResourcesProvider.uri, ac);
			
		}
	}

	@Override
	@Ring
	public Integer decreaseCoreFrenquency(AllocatedCore ac) throws Exception {
		if ( isLocal(ac) )
			return coreFrenquencyVariation(ac.processorURI, ac.coreNo, Variation.DECREASED);
		else {
			String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

			if ( prpropURI == null ) {
				throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
						"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
			}

			PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
			return prprop.decreaseCoreFrenquency(physicalResourcesProvider.uri, ac);
		}
	}

	@Override
	@Ring
	public Integer[] increaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		if ( isLocal(ac) )
			return processorFrenquencyVariation(ac.processorURI, Variation.INCREASED);
		else {
			String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

			if ( prpropURI == null ) {
				throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
						"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
			}

			PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
			return prprop.increaseProcessorFrenquency(physicalResourcesProvider.uri, ac);
		}
	}

	@Override
	@Ring
	public Integer[] decreaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		if ( isLocal(ac) )
			return processorFrenquencyVariation(ac.processorURI, Variation.DECREASED);
		else {
			String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

			if ( prpropURI == null ) {
				throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
						"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
			}

			PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
			return prprop.decreaseProcessorFrenquency(physicalResourcesProvider.uri, ac);
		}
	}

	@Override 
	@Ring
	public Integer[][] increaseComputerFrenquency(AllocatedCore ac) throws Exception {
		
		if ( isLocal(ac) ) {
			ComponentDataNode lpdn = physicalResourcesProvider.findByURI(ac.processorURI);
			ComponentDataNode cptdn = (ComponentDataNode) lpdn.parents.toArray()[0];
			
			Integer[][] computerFrequencies = null;
			
			int processorNo = 0;
			for (ComponentDataNode pdn : cptdn.children) {
				Integer[] processorFrequencies = processorFrenquencyVariation(pdn.uri, Variation.INCREASED);
				if ( computerFrequencies == null )
					computerFrequencies = new Integer[cptdn.children.size()][processorFrequencies.length];
				computerFrequencies[processorNo++] = processorFrequencies;
			}
			
			return computerFrequencies;
			
		} else {
			String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

			if ( prpropURI == null ) {
				throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
						"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
			}

			PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
			return prprop.increaseComputerFrenquency(physicalResourcesProvider.uri, ac);
		}
	}

	@Override
	@Ring
	public Integer[][] decreaseComputerFrenquency(AllocatedCore ac) throws Exception {
		if ( isLocal(ac) ) {
			ComponentDataNode lpdn = physicalResourcesProvider.findByURI(ac.processorURI);
			ComponentDataNode cptdn = (ComponentDataNode) lpdn.parents.toArray()[0];

			Integer[][] computerFrequencies = null;
			
			int processorNo = 0;
			for (ComponentDataNode pdn : cptdn.children) {
				Integer[] processorFrequencies = processorFrenquencyVariation(pdn.uri, Variation.DECREASED);
				if ( computerFrequencies == null )
					computerFrequencies = new Integer[cptdn.children.size()][processorFrequencies.length];
				computerFrequencies[processorNo++] = processorFrequencies;
			}
			
			return computerFrequencies;
			
		} else {
			String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

			if ( prpropURI == null ) {
				throw new RingException("No PhysicalResourcesProviderServicesOutboundPort found for process this not belonged AllocatedCore.\n" +
						"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
			}

			PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
			return prprop.decreaseComputerFrenquency(physicalResourcesProvider.uri, ac);
		}
	}

	@Override
	public AllocatedCore[] allocateCores(Integer cores) throws Exception {
		assert cores >= 0;
		
		if ( cores == 0 )
			throw new NoCoreException("No core available");
		
		String computerURI = findAvailableComputerForCoreAllocation(cores);
		
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
					"], (" + acs.length + 
					") core(s) are been allocated");

		assert acs != null;
		assert acs.length > 0;
		assert acs.length == cores;
		
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
	public AllocatedCore[] releaseCores(AllocatedCore[] allocatedCores) throws Exception {
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
		
		List<AllocatedCore> released = new ArrayList<>();

		if (providedAllocated.size() > 0) {
			
			HashMap<String, List<AllocatedCore>> ccropsToCallMap = new HashMap<>(); // Car on peut avoir différents ordinateurs cibles pour la libération et qu'il faut releaseCore sur les bons ccrops.
			
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
				
				for (AllocatedCore ac : toReleaseLocally)
					released.add(ac);
				
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
			AllocatedCore[] releasedElsewhere = prprop.releaseCores(physicalResourcesProvider.uri, toReleaseElsewhere);
			
			for (AllocatedCore ac : releasedElsewhere)
				released.add(ac);

		}
		
		AllocatedCore[] trulyReleasedCore = new AllocatedCore[released.size()];
		for ( int i = 0; i < released.size(); i++ ) {
			trulyReleasedCore[i] = released.get(i);
		}
		
		return trulyReleasedCore;

	}

	@Override
	public Integer increaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
				
		if (requesterUri == physicalResourcesProvider.uri) {
			if ( LOGGING )
				logMessage("All physical resources providers has been requested. Unfortunately, the allocate core seems orphane.");
			throw new OrphaneAllocatedCoreException("Complete physical resources providers ring loops without found the allocated core owner");
		} else {
			if ( isLocal(ac) ) {
				return increaseCoreFrenquency(ac);
			} else {
				String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

				if ( prpropURI == null ) {
					throw new Exception(	"No PhysicalResourcesProviderRequestingOutboundPort found for process this not belonged AllocatedCore.\n" +
							"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
				}

				PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
				return prprop.increaseCoreFrenquency(requesterUri, ac);
			}
		}

	}

	@Override
	public Integer decreaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {

		if (requesterUri == physicalResourcesProvider.uri) {
			if ( LOGGING )
				logMessage("All physical resources providers has been requested. Unfortunately, the allocate core seems orphane.");
			throw new OrphaneAllocatedCoreException("Complete physical resources providers ring loops without found the allocated core owner");
		} else {
			if ( isLocal(ac) ) {
				return decreaseCoreFrenquency(ac);
			} else {
				String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

				if ( prpropURI == null ) {
					throw new Exception(	"No PhysicalResourcesProviderRequestingOutboundPort found for process this not belonged AllocatedCore.\n" +
							"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
				}

				PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
				return prprop.decreaseCoreFrenquency(requesterUri, ac);
			}
		}

	}

	@Override
	public Integer[] increaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {

		if (requesterUri == physicalResourcesProvider.uri) {
			if ( LOGGING )
				logMessage("All physical resources providers has been requested. Unfortunately, the allocate core seems orphane.");
			throw new OrphaneAllocatedCoreException("Complete physical resources providers ring loops without found the allocated core owner");
		} else {
			if ( isLocal(ac) ) {
				return increaseProcessorFrenquency(ac);
			} else {
				String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

				if ( prpropURI == null ) {
					throw new Exception(	"No PhysicalResourcesProviderRequestingOutboundPort found for process this not belonged AllocatedCore.\n" +
							"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
				}

				PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
				return prprop.increaseProcessorFrenquency(requesterUri, ac);
			}
		}

	}

	@Override
	public Integer[] decreaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {

		if (requesterUri == physicalResourcesProvider.uri) {
			if ( LOGGING )
				logMessage("All physical resources providers has been requested. Unfortunately, the allocate core seems orphane.");
			throw new OrphaneAllocatedCoreException("Complete physical resources providers ring loops without found the allocated core owner");
		} else {
			if ( isLocal(ac) ) {
				return decreaseProcessorFrenquency(ac);
			} else {
				String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

				if ( prpropURI == null ) {
					throw new Exception(	"No PhysicalResourcesProviderRequestingOutboundPort found for process this not belonged AllocatedCore.\n" +
							"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
				}

				PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
				return prprop.decreaseProcessorFrenquency(requesterUri, ac);
			}
		}

	}

	@Override
	public Integer[][] increaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		
		if (requesterUri == physicalResourcesProvider.uri) {
			if ( LOGGING )
				logMessage("All physical resources providers has been requested. Unfortunately, the allocate core seems orphane.");
			throw new OrphaneAllocatedCoreException("Complete physical resources providers ring loops without found the allocated core owner");
		} else {
			if ( isLocal(ac) ) {
				return increaseComputerFrenquency(ac);
			} else {
				String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

				if ( prpropURI == null ) {
					throw new Exception(	"No PhysicalResourcesProviderRequestingOutboundPort found for process this not belonged AllocatedCore.\n" +
							"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
				}

				PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
				return prprop.increaseComputerFrenquency(requesterUri, ac);
			}
		}

	}

	@Override 
	@Ring
	public Integer[][] decreaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {

		if (requesterUri == physicalResourcesProvider.uri) {
			if ( LOGGING )
				logMessage("All physical resources providers has been requested. Unfortunately, the allocate core seems orphane.");
			throw new OrphaneAllocatedCoreException("Complete physical resources providers ring loops without found the allocated core owner");
		} else {
			if ( isLocal(ac) ) {
				return decreaseComputerFrenquency(ac);
			} else {
				String	prpropURI = physicalResourcesProvider.getPortLike(Tag.PHYSICAL_RESOURCES_PROVIDER_REQUESTING_OUTBOUND_PORT);

				if ( prpropURI == null ) {
					throw new Exception(	"No PhysicalResourcesProviderRequestingOutboundPort found for process this not belonged AllocatedCore.\n" +
							"LOG GRAPH : \n" + physicalResourcesProvider.graphToString());
				}

				PhysicalResourcesProviderRequestingOutboundPort prprop = (PhysicalResourcesProviderRequestingOutboundPort) findPortFromURI(prpropURI);
				return prprop.decreaseComputerFrenquency(requesterUri, ac);
			}
		}

	}

	@Override
	@Ring
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
	@Ring
	public AllocatedCore[] releaseCores(String requesterUri, AllocatedCore[] allocatedCores) throws Exception {
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

			List<AllocatedCore> released = new ArrayList<>();
			
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

				for (AllocatedCore ac : toReleaseLocally)
					released.add(ac);
				
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
				AllocatedCore[] releasedElsewhere = prprop.releaseCores(physicalResourcesProvider.uri, toReleaseElsewhere);
				
				for (AllocatedCore ac : releasedElsewhere)
					released.add(ac);

			}
			
			AllocatedCore[] trulyReleasedCore = new AllocatedCore[released.size()];
			for ( int i = 0; i < released.size(); i++ ) {
				trulyReleasedCore[i] = released.get(i);
			}
			
			return trulyReleasedCore;
			
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
