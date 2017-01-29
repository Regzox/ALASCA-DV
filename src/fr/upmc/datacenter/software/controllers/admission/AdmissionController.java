package fr.upmc.datacenter.software.controllers.admission;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.data.PerformanceControllerPortsData;
import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.data.interfaces.PerformanceControllerPortsDataI;
import fr.upmc.datacenter.providers.resources.logical.connectors.LogicalResourcesProviderManagementConnector;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderManagementI;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderManagementOutboundPort;
import fr.upmc.datacenter.software.controllers.admission.interfaces.AdmissionControllerI;
import fr.upmc.datacenter.software.controllers.performance.PerformanceController;
import fr.upmc.datacenter.software.controllers.performance.connectors.PerformanceControllerManagementConnector;
import fr.upmc.datacenter.software.controllers.performance.connectors.PerformanceControllerServicesConnector;
import fr.upmc.datacenter.software.controllers.performance.interfaces.PerformanceControllerManagementI;
import fr.upmc.datacenter.software.controllers.performance.interfaces.PerformanceControllerServicesI;
import fr.upmc.datacenter.software.controllers.performance.ports.PerformanceControllerManagementOutboundPort;
import fr.upmc.datacenter.software.controllers.performance.ports.PerformanceControllerServicesOutboundPort;
import fr.upmc.datacenter.software.enumerations.Tag;
import fr.upmc.nodes.ComponentDataNode;

/**
 * Contrôleur d'admission.
 * 
 * Le contrôleur d'admission gère les demandes d'hébergement des applications externes.
 * Pour se faire le contrôleur d'admission doit avoir connaissance d'un ensemble de fournisseurs de ressources logiques sur lesquels
 * attacher un nouveau contrôleur de performances.
 * Les fournisseurs de ressources physiques et logiques doivent être tous interconnectés avant l'instanciation du contrôleur d'admission.
 * La limite d'application acceptée est directement influencée par la quantité de fournisseurs de ressources logiques disponibles.
 * Pour un fournisseur de ressources logiques, seul un contrôleur de performances peut être raccordé.
 * 
 * Puisque les fournisseurs de ressources en général sont interconnectés en anneaux, un pour les physiques et un autre pour les logiques,
 * si une application vient à manquer de puissance de calcul dans leur chaine directe, les participants à l'anneau peuvent être mis à contribution.
 * 
 * 
 * @author Daniel RADEAU
 *
 */

public class AdmissionController 
extends 	AbstractComponent
implements	AdmissionControllerI
{
	enum Branch {PERFORMANCE_CONTROLLERS, LOGICAL_RESSOURCES_PROVIDERS}
	
	ComponentDataNode admissionController;
	List<LogicalResourcesProviderPortsDataI> lrppdis;
	List<LogicalResourcesProviderPortsDataI> connectedLrppdis;
	List<AllocatedPerformanceController> apcs;
	
	public AdmissionController(String uri, List<LogicalResourcesProviderPortsDataI> lrppdis) {
		admissionController = new ComponentDataNode(uri);
		this.lrppdis = new ArrayList<>(lrppdis);
		connectedLrppdis = new ArrayList<>();
		apcs = new ArrayList<>();
		
		admissionController.addChild(new ComponentDataNode(Branch.PERFORMANCE_CONTROLLERS));
		admissionController.addChild(new ComponentDataNode(Branch.LOGICAL_RESSOURCES_PROVIDERS));
	}

	@Override
	public boolean submitApplication() throws Exception {
		if ( !isHostable() )
			return false;
		else {
			
			createPerformanceController();
			
			return true;
		}
	}
	
	@Override
	public void createPerformanceController() throws Exception {
		
		String 	pcURI = generatePerformanceControllerUri(admissionController.uri, Tag.PERFORMANCE_CONTROLLER),
				pcmipURI = generatePerformanceControllerUri(admissionController.uri, Tag.PERFORMANCE_CONTROLLER_MANAGEMENT_INBOUND_PORT),
				pcsipURI = generatePerformanceControllerUri(admissionController.uri, Tag.PERFORMANCE_CONTROLLER_SERVICES_INBOUND_PORT),
				pccrnipURI = generatePerformanceControllerUri(admissionController.uri, Tag.APPLICATION_VM_CORE_RELEASING_INBOUND_PORT),
				pcmopURI = generatePerformanceControllerUri(admissionController.uri, Tag.PERFORMANCE_CONTROLLER_MANAGEMENT_OUTBOUND_PORT),
				pcsopURI = generatePerformanceControllerUri(admissionController.uri, Tag.PERFORMANCE_CONTROLLER_SERVICES_OUTBOUND_PORT);
		
		ComponentDataNode pcdn = new ComponentDataNode(pcURI)
				.addPort(pcmipURI)
				.addPort(pcsipURI)
				.addPort(pccrnipURI);
		
		admissionController.findByURI(Branch.PERFORMANCE_CONTROLLERS).addChild(pcdn);
		
		PerformanceController pc = new PerformanceController(pcURI, pcmipURI, pcsipURI, pccrnipURI);
		pc.toggleLogging();
		pc.toggleTracing();
		
		AbstractCVM.theCVM.addDeployedComponent(pc);
		pc.start();
		
		PerformanceControllerManagementOutboundPort pcmop = new PerformanceControllerManagementOutboundPort(
				pcmopURI, 
				PerformanceControllerManagementI.class, 
				this);
		pcmop.publishPort();
		pcmop.doConnection(pcmipURI, PerformanceControllerManagementConnector.class.getCanonicalName());
		
		admissionController.trustedConnect(pcmopURI, pcmipURI);
	
		PerformanceControllerServicesOutboundPort pcsop = new PerformanceControllerServicesOutboundPort(
				pcsopURI, 
				PerformanceControllerServicesI.class, 
				this);
		pcsop.publishPort();
		pcsop.doConnection(pcsipURI, PerformanceControllerServicesConnector.class.getCanonicalName());
		
		admissionController.trustedConnect(pcsopURI, pcsipURI);
		
		LogicalResourcesProviderPortsDataI lrppdi = lrppdis.remove(0);
		pcmop.connectLogicalResourcesProvider(lrppdi);
		connectedLrppdis.add(lrppdi);
		
		PerformanceControllerPortsDataI pcpdi = new PerformanceControllerPortsData(
				pcURI, 
				pcmipURI, 
				pcsipURI, 
				pccrnipURI);
		
		ComponentDataNode lrpdn = new ComponentDataNode(lrppdi.getUri())
				.addPort(lrppdi.getLogicalResourcesProviderCoreReleasingNotifyBackInboundPort())
				.addPort(lrppdi.getLogicalResourcesProviderManagementInboundPort())
				.addPort(lrppdi.getLogicalResourcesProviderRequestingInboundPort())
				.addPort(lrppdi.getLogicalResourcesProviderServicesInboundPort());
		
		admissionController.findByURI(Branch.LOGICAL_RESSOURCES_PROVIDERS).addChild(lrpdn);
		
		String lrpmopURI = generateLogicalResourcesProviderUri(admissionController.uri, Tag.LOGICAL_RESOURCES_PROVIDER_MANAGAMENT_OUTBOUND_PORT);
		LogicalResourcesProviderManagementOutboundPort lrpmop = 
				new LogicalResourcesProviderManagementOutboundPort(lrpmopURI, LogicalResourcesProviderManagementI.class, this);
		lrpmop.publishPort();
		lrpmop.doConnection(lrppdi.getLogicalResourcesProviderManagementInboundPort(), LogicalResourcesProviderManagementConnector.class.getCanonicalName());
		
		admissionController.trustedConnect(lrpmopURI, lrppdi.getLogicalResourcesProviderManagementInboundPort());
		
		lrpmop.connectPerformanceController(pcpdi);
		
		pcsop.acceptApplication(); // Lancement d'une nouvelle application simulé dans le contrôleur de performance
		
	}

	@Override
	public String generatePerformanceControllerUri(String ownerUri, Object tag) throws Exception {
		return ownerUri + " : " + tag.toString() + "_" + (admissionController.findByURI(Branch.PERFORMANCE_CONTROLLERS).children.size() + 1);
	}
	
	@Override
	public String generateLogicalResourcesProviderUri(String ownerUri, Object tag) throws Exception {
		return ownerUri + " : " + tag.toString() + "_" + (admissionController.findByURI(Branch.LOGICAL_RESSOURCES_PROVIDERS).children.size() + 1);
	}

	@Override
	public boolean isHostable() throws Exception {
		return !lrppdis.isEmpty();
	}

}
