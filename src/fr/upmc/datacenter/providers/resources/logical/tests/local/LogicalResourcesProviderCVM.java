package fr.upmc.datacenter.providers.resources.logical.tests.local;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.data.ComputerPortsData;
import fr.upmc.datacenter.data.LogicalResourcesProviderPortsData;
import fr.upmc.datacenter.data.PhysicalResourcesProviderPortsData;
import fr.upmc.datacenter.data.interfaces.ComputerPortsDataI;
import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.data.interfaces.PhysicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.hardware.computer.stock.Stock;
import fr.upmc.datacenter.hardware.processor.model.Model;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;
import fr.upmc.datacenter.providers.resources.logical.connectors.LogicalResourcesProviderManagementConnector;
import fr.upmc.datacenter.providers.resources.logical.connectors.LogicalResourcesProviderServicesConnector;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderManagementI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderRequestingI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderServicesI;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderManagementOutboundPort;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderRequestingOutboundPort;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderServicesOutboundPort;
import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;
import fr.upmc.datacenter.providers.resources.physical.connectors.PhysicalResourcesProviderManagementConnector;
import fr.upmc.datacenter.providers.resources.physical.connectors.PhysicalResourcesProviderServicesConnector;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderManagementI;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderRequestingI;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderServicesI;
import fr.upmc.datacenter.providers.resources.physical.ports.PhysicalResourcesProviderManagementOutboundPort;
import fr.upmc.datacenter.providers.resources.physical.ports.PhysicalResourcesProviderRequestingOutboundPort;
import fr.upmc.datacenter.providers.resources.physical.ports.PhysicalResourcesProviderServicesOutboundPort;

public class LogicalResourcesProviderCVM extends AbstractCVM {

	public final String
	lrpURI_A = "lrp_A",
	lrpmipURI_A = "lrpmip_A",
	lrpmopURI_A = "lrpmop_A",
	lrpripURI_A = "lrprip_A",
	lrpropURI_A = "lrprop_A",
	lrpsipURI_A = "lrpsip_A",
	lrpsopURI_A = "lrpsop_A",
	lrpcrnbipURI_A = "lrpcrnbip_A";
	
	public final String
	lrpURI_B = "lrp_B",
	lrpmipURI_B = "lrpmip_B",
	lrpmopURI_B = "lrpmop_B",
	lrpripURI_B = "lrprip_B",
	lrpropURI_B = "lrprop_B",
	lrpsipURI_B = "lrpsip_B",
	lrpsopURI_B = "lrpsop_B",
	lrpcrnbipURI_B = "lrpcrnbip_B";
	
	public final String 	
	prpURI_A = "prp_A",
	prpmipURI_A = "prpmip_A",
	prpripURI_A = "prprip_A",
	prpsipURI_A = "prpsip_A",
	prpmopURI_A = "prpmop_A",
	prpropURI_A = "prprop_A",
	prpsopURI_A = "prpsop_A";
	
	public final String 	
	prpURI_B = "prp_B",
	prpmipURI_B = "prpmip_B",
	prpripURI_B = "prprip_B",
	prpsipURI_B = "prpsip_B",
	prpmopURI_B = "prpmop_B",
	prpropURI_B = "prprop_B",
	prpsopURI_B = "prpsop_B";
	
	public LogicalResourceProvider lrp_A, lrp_B;
	
	public Stock stockA;
	public PhysicalResourcesProvider prpA;
	
	public Stock stockB;
	public PhysicalResourcesProvider prpB;

	public LogicalResourcesProviderManagementOutboundPort lrpmop_A;
	public LogicalResourcesProviderRequestingOutboundPort lrprop_A;
	public LogicalResourcesProviderServicesOutboundPort lrpsop_A;
	
	public LogicalResourcesProviderManagementOutboundPort lrpmop_B;
	public LogicalResourcesProviderRequestingOutboundPort lrprop_B;
	public LogicalResourcesProviderServicesOutboundPort lrpsop_B;
	
	public PhysicalResourcesProviderManagementOutboundPort prpmopA;
	public PhysicalResourcesProviderRequestingOutboundPort prpropA;
	public PhysicalResourcesProviderServicesOutboundPort prpsopA;
	
	public PhysicalResourcesProviderManagementOutboundPort prpmopB;
	public PhysicalResourcesProviderRequestingOutboundPort prpropB;
	public PhysicalResourcesProviderServicesOutboundPort prpsopB;

	public LogicalResourcesProviderCVM() throws Exception {
		super();

		lrp_A = new LogicalResourceProvider(lrpURI_A, lrpmipURI_A, lrpripURI_A, lrpsipURI_A, lrpcrnbipURI_A);
		lrp_A.toggleLogging();
		lrp_A.toggleTracing();
		
		lrp_B = new LogicalResourceProvider(lrpURI_B, lrpmipURI_B, lrpripURI_B, lrpsipURI_B, lrpcrnbipURI_B);
		lrp_B.toggleLogging();
		lrp_B.toggleTracing();
		
		stockA = new Stock("stockA", 2, 4, Model.I7_6700K);
		prpA = new PhysicalResourcesProvider(prpURI_A, prpmipURI_A, prpripURI_A, prpsipURI_A);
		prpA.toggleLogging();
		prpA.toggleTracing();
		
		stockB = new Stock("stockB", 2, 4, Model.I7_6700K);
		prpB = new PhysicalResourcesProvider(prpURI_B, prpmipURI_B, prpripURI_B, prpsipURI_B);
		prpB.toggleLogging();
		prpB.toggleTracing();
	}

	@Override
	public void deploy() throws Exception {

		addDeployedComponent(lrp_A);
		
		lrpmop_A = new LogicalResourcesProviderManagementOutboundPort(
				lrpmopURI_A, 
				LogicalResourcesProviderManagementI.class, 
				new AbstractComponent() {});
		lrpmop_A.publishPort();
		
		lrprop_A = new LogicalResourcesProviderRequestingOutboundPort(
				lrpropURI_A, 
				LogicalResourcesProviderRequestingI.class, 
				new AbstractComponent() {});
		lrprop_A.publishPort();
		
		lrpsop_A = new LogicalResourcesProviderServicesOutboundPort(
				lrpsopURI_A, 
				LogicalResourcesProviderServicesI.class, 
				new AbstractComponent() {});
		lrpsop_A.publishPort();
		
		/////////////////////////////////////////////////////////////
		
		addDeployedComponent(lrp_B);
		
		lrpmop_B = new LogicalResourcesProviderManagementOutboundPort(
				lrpmopURI_B, 
				LogicalResourcesProviderManagementI.class, 
				new AbstractComponent() {});
		lrpmop_B.publishPort();
		
		lrprop_B = new LogicalResourcesProviderRequestingOutboundPort(
				lrpropURI_B, 
				LogicalResourcesProviderRequestingI.class, 
				new AbstractComponent() {});
		lrprop_B.publishPort();
		
		lrpsop_B = new LogicalResourcesProviderServicesOutboundPort(
				lrpsopURI_B, 
				LogicalResourcesProviderServicesI.class, 
				new AbstractComponent() {});
		lrpsop_B.publishPort();
		
		/////////////////////////////////////////////////////////////
		
		addDeployedComponent(prpA);

		prpmopA = new PhysicalResourcesProviderManagementOutboundPort(
				prpmopURI_A, 
				PhysicalResourcesProviderManagementI.class,
				new AbstractComponent() {});
		prpmopA.publishPort();

		prpropA = new PhysicalResourcesProviderRequestingOutboundPort(
				prpropURI_A, 
				PhysicalResourcesProviderRequestingI.class,
				new AbstractComponent() {});
		prpropA.publishPort();

		prpsopA = new PhysicalResourcesProviderServicesOutboundPort(
				prpsopURI_A, 
				PhysicalResourcesProviderServicesI.class,
				new AbstractComponent() {});
		prpsopA.publishPort();
		
		//////////////////////////////////////////////////////////////
		
		addDeployedComponent(prpB);

		prpmopB = new PhysicalResourcesProviderManagementOutboundPort(
				prpmopURI_B, 
				PhysicalResourcesProviderManagementI.class,
				new AbstractComponent() {});
		prpmopB.publishPort();

		prpropB = new PhysicalResourcesProviderRequestingOutboundPort(
				prpropURI_B, 
				PhysicalResourcesProviderRequestingI.class,
				new AbstractComponent() {});
		prpropB.publishPort();

		prpsopB = new PhysicalResourcesProviderServicesOutboundPort(
				prpsopURI_B, 
				PhysicalResourcesProviderServicesI.class,
				new AbstractComponent() {});
		prpsopB.publishPort();
				
		super.deploy();
	}
	
	public void connect() throws Exception {
		
		lrpmop_A.doConnection(lrpmipURI_A, LogicalResourcesProviderManagementConnector.class.getCanonicalName());
		
		PhysicalResourcesProviderPortsDataI prppdi_A =
				new PhysicalResourcesProviderPortsData(
						prpURI_A, 
						prpmipURI_A, 
						prpripURI_A, 
						prpsipURI_A);
		
		lrpmop_A.connectPhysicalResourcesProvider(prppdi_A);

		LogicalResourcesProviderPortsDataI lrppdi_A = 
				new LogicalResourcesProviderPortsData(
						lrpURI_A, 
						lrpmipURI_A, 
						lrpripURI_A, 
						lrpsipURI_A,
						lrpcrnbipURI_A);
		
		
		lrpsop_A.doConnection(lrpsipURI_A, LogicalResourcesProviderServicesConnector.class.getCanonicalName());
		
		//////////////////////////////////////////////////////////////////////////////////////////////////
		
		lrpmop_B.doConnection(lrpmipURI_B, LogicalResourcesProviderManagementConnector.class.getCanonicalName());
		
		PhysicalResourcesProviderPortsDataI prppdi_B =
				new PhysicalResourcesProviderPortsData(
						prpURI_B, 
						prpmipURI_B, 
						prpripURI_B, 
						prpsipURI_B);
		
		lrpmop_B.connectPhysicalResourcesProvider(prppdi_B);
		
		LogicalResourcesProviderPortsDataI lrppdi_B = 
				new LogicalResourcesProviderPortsData(
						lrpURI_B, 
						lrpmipURI_B, 
						lrpripURI_B, 
						lrpsipURI_B,
						lrpcrnbipURI_B);
		
		
		lrpsop_B.doConnection(lrpsipURI_B, LogicalResourcesProviderServicesConnector.class.getCanonicalName());

		//////////////////////////////////////// RING CONNECTION /////////////////////////////////////////////
		
		lrpmop_A.connectLogicalResourcesProvider(lrppdi_B);
		lrpmop_B.connectLogicalResourcesProvider(lrppdi_A);
		
		lrpmop_A.connectLogicalResourcesProviderNotifyBack(lrppdi_B);
		lrpmop_B.connectLogicalResourcesProviderNotifyBack(lrppdi_A);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////
		
		prpmopA.doConnection(prpmipURI_A, PhysicalResourcesProviderManagementConnector.class.getCanonicalName());
		
		for (String cptURI : stockA.getComputersURI()) {
			String 	csipURI = stockA.getComputerServicesInboundPortURIMap().get(cptURI),
					cssdipURI = stockA.getComputerStaticStateDataInboundPortURIMap().get(cptURI),
					cdsdipURI = stockA.getComputerDynamicStateDataInboundPortURIMap().get(cptURI),
					ccripURI = stockA.getComputerCoreReleasingInboundPortURIMap().get(cptURI);
					
			ComputerPortsDataI cdpi = 
					new ComputerPortsData(
							cptURI, 
							csipURI, 
							cssdipURI, 
							cdsdipURI, 
							ccripURI);
						
			prpmopA.connectComputer(cdpi);
		}
		
		PhysicalResourcesProviderPortsDataI prppdiA =
				new PhysicalResourcesProviderPortsData(
						prpURI_A, 
						prpmipURI_A, 
						prpripURI_A, 
						prpsipURI_A);
		
		prpsopA.doConnection(prpsipURI_A, PhysicalResourcesProviderServicesConnector.class.getCanonicalName());
		
		///////////////////////////////////////////////////////////////////////////////////////////////////
		
		prpmopB.doConnection(prpmipURI_B, PhysicalResourcesProviderManagementConnector.class.getCanonicalName());
		
		for (String cptURI : stockB.getComputersURI()) {
			String 	csipURI = stockB.getComputerServicesInboundPortURIMap().get(cptURI),
					cssdipURI = stockB.getComputerStaticStateDataInboundPortURIMap().get(cptURI),
					cdsdipURI = stockB.getComputerDynamicStateDataInboundPortURIMap().get(cptURI),
					ccripURI = stockB.getComputerCoreReleasingInboundPortURIMap().get(cptURI);
					
			ComputerPortsDataI cdpi = 
					new ComputerPortsData(
							cptURI, 
							csipURI, 
							cssdipURI, 
							cdsdipURI, 
							ccripURI);
						
			prpmopB.connectComputer(cdpi);
		}
		
		PhysicalResourcesProviderPortsDataI prppdiB =
				new PhysicalResourcesProviderPortsData(
						prpURI_B, 
						prpmipURI_B, 
						prpripURI_B, 
						prpsipURI_B);
		
		
		prpsopB.doConnection(prpsipURI_B, PhysicalResourcesProviderServicesConnector.class.getCanonicalName());
		
		/////////////////////////////////////// RING CONNECTION ////////////////////////////////////////////////
		
		prpmopA.connectPhysicalResourcesProvider(prppdiB);
		prpmopB.connectPhysicalResourcesProvider(prppdiA);
		
	}
	
	@Override
	public void start() throws Exception {
		super.start();
		
		connect();
	}
	
}
