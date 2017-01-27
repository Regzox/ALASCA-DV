package fr.upmc.datacenter.software.controllers.admission.tests.local;

import java.util.ArrayList;
import java.util.List;

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
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderManagementI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderRequestingI;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderServicesI;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderManagementOutboundPort;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderRequestingOutboundPort;
import fr.upmc.datacenter.providers.resources.logical.ports.LogicalResourcesProviderServicesOutboundPort;
import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;
import fr.upmc.datacenter.providers.resources.physical.connectors.PhysicalResourcesProviderManagementConnector;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderManagementI;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderRequestingI;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderServicesI;
import fr.upmc.datacenter.providers.resources.physical.ports.PhysicalResourcesProviderManagementOutboundPort;
import fr.upmc.datacenter.providers.resources.physical.ports.PhysicalResourcesProviderRequestingOutboundPort;
import fr.upmc.datacenter.providers.resources.physical.ports.PhysicalResourcesProviderServicesOutboundPort;
import fr.upmc.datacenter.software.controllers.admission.AdmissionController;

class PRP {
	
	static int i = 0;
	
	public static PRP generate() throws Exception {
		PRP.i++;
		PRP prp = new PRP();
		prp.prp = new PhysicalResourcesProvider(prp.prpURI, prp.prpmipURI, prp.prpripURI, prp.prpsipURI);
		return prp;
	}
	
	public final String
	prpURI = "prp_" + i,
	prpmipURI = "prpmip_" + i,
	prpripURI = "prprip_" + i,
	prpsipURI = "prpsip_" + i,
	prpmopURI = "prpmop_" + i,
	prpropURI = "prprop_" + i,
	prpsopURI = "prpsop_" + i;
	
	public PhysicalResourcesProvider prp;
	
	public PhysicalResourcesProviderManagementOutboundPort prpmop;
	public PhysicalResourcesProviderRequestingOutboundPort prprop;
	public PhysicalResourcesProviderServicesOutboundPort prpsop;
	
}

class LRP {
	
	static int i = 0;
	
	public static LRP generate() throws Exception {
		LRP.i++;
		LRP lrp = new LRP();
		lrp.lrp = new LogicalResourceProvider(lrp.lrpURI, lrp.lrpmipURI, lrp.lrpripURI, lrp.lrpsipURI, lrp.lrpcrnbipURI);
		return lrp;
	}
	
	public final String
	lrpURI = "lrp_" + i,
	lrpmipURI = "lrpmip_" + i,
	lrpmopURI = "lrpmop_" + i,
	lrpripURI = "lrprip_" + i,
	lrpropURI = "lrprop_" + i,
	lrpsipURI = "lrpsip_" + i,
	lrpsopURI = "lrpsop_" + i,
	lrpcrnbipURI = "lrpcrnbip_" + i;
	
	public LogicalResourceProvider lrp;
	
	public LogicalResourcesProviderManagementOutboundPort lrpmop;
	public LogicalResourcesProviderRequestingOutboundPort lrprop;
	public LogicalResourcesProviderServicesOutboundPort lrpsop;
	
}

public class AdmissionControllerCVMExtremScaleUp extends AbstractCVM {

	AdmissionController ac;
	public static int STOCKS = 0;
	public static int BRANCHES = 1;
	protected static int INDEX = 0;
	protected List<PRP> prps;
	protected List<LRP> lrps;
	protected List<Stock> stocks;
	
	public void generatePRPs() throws Exception {
		List<PRP> list = new ArrayList<>();
		for (int i = 0; i < BRANCHES; i++)
			list.add(PRP.generate());
		prps = list;
	}
	
	public void generateLRPs() throws Exception {
		List<LRP> list = new ArrayList<>();
		for (int i = 0; i < BRANCHES; i++)
			list.add(LRP.generate());
		lrps = list;
	}
	
	public void generateStocks() throws Exception {
		List<Stock> list = new ArrayList<>();
		for (int i = 0; i < BRANCHES; i++) {
			STOCKS++;
			list.add(new Stock("stock_" + STOCKS, 2, 1, Model.I7_6700K));
		}
		stocks = list;
	}

	public AdmissionControllerCVMExtremScaleUp() throws Exception {
		super();
		
		generateStocks();
		generatePRPs();
		generateLRPs();
		
	}

	@Override
	public void deploy() throws Exception {
		
		for (int i = 0; i < BRANCHES; i++) {
			
			PRP prp = prps.get(i);
			LRP lrp = lrps.get(i);
		
			/**
			 * PRP déclarations des outbound ports 
			 */
			
			addDeployedComponent(prp.prp);
			
			prp.prpmop = new PhysicalResourcesProviderManagementOutboundPort(
					prp.prpmopURI, 
					PhysicalResourcesProviderManagementI.class,
					new AbstractComponent() {});
			prp.prpmop.publishPort();

			prp.prprop = new PhysicalResourcesProviderRequestingOutboundPort(
					prp.prpropURI, 
					PhysicalResourcesProviderRequestingI.class,
					new AbstractComponent() {});
			prp.prprop.publishPort();

			prp.prpsop = new PhysicalResourcesProviderServicesOutboundPort(
					prp.prpsopURI, 
					PhysicalResourcesProviderServicesI.class,
					new AbstractComponent() {});
			prp.prpsop.publishPort();
			
			/**
			 * LRP déclarations des outbound ports
			 */
			
			addDeployedComponent(lrp.lrp);
			
			lrp.lrpmop = new LogicalResourcesProviderManagementOutboundPort(
					lrp.lrpmopURI, 
					LogicalResourcesProviderManagementI.class, 
					new AbstractComponent() {});
			lrp.lrpmop.publishPort();
			
			lrp.lrprop = new LogicalResourcesProviderRequestingOutboundPort(
					lrp.lrpropURI, 
					LogicalResourcesProviderRequestingI.class, 
					new AbstractComponent() {});
			lrp.lrprop.publishPort();
			
			lrp.lrpsop = new LogicalResourcesProviderServicesOutboundPort(
					lrp.lrpsopURI, 
					LogicalResourcesProviderServicesI.class, 
					new AbstractComponent() {});
			lrp.lrpsop.publishPort();
			
		}
				
		super.deploy();
	}
	
	public void connect() throws Exception {
		
		List<LogicalResourcesProviderPortsDataI> lrppdis = new ArrayList<>();
		
		for (int i = 0; i < BRANCHES; i++) {
						
			Stock stock = stocks.get(i);
			PRP prp = prps.get(i);
			LRP lrp = lrps.get(i);
			
			prp.prpmop.doConnection(prp.prpmipURI, PhysicalResourcesProviderManagementConnector.class.getCanonicalName());
			
			PhysicalResourcesProviderPortsDataI prppdi =
					new PhysicalResourcesProviderPortsData(
							prp.prpURI, 
							prp.prpmipURI, 
							prp.prpripURI, 
							prp.prpsipURI);
			
			for (String cptURI : stock.getComputersURI()) {
				
				String 	csipURI = stock.getComputerServicesInboundPortURIMap().get(cptURI),
						cssdipURI = stock.getComputerStaticStateDataInboundPortURIMap().get(cptURI),
						cdsdipURI = stock.getComputerDynamicStateDataInboundPortURIMap().get(cptURI),
						ccripURI = stock.getComputerCoreReleasingInboundPortURIMap().get(cptURI);
						
				ComputerPortsDataI cdpi = 
						new ComputerPortsData(
								cptURI, 
								csipURI, 
								cssdipURI, 
								cdsdipURI, 
								ccripURI);
							
				prp.prpmop.connectComputer(cdpi);
				
			}
			
			lrp.lrpmop.doConnection(lrp.lrpmipURI, LogicalResourcesProviderManagementConnector.class.getCanonicalName());
						
			lrp.lrpmop.connectPhysicalResourcesProvider(prppdi);
			
			LogicalResourcesProviderPortsDataI lrppdi = 
					new LogicalResourcesProviderPortsData(
							lrp.lrpURI, 
							lrp.lrpmipURI, 
							lrp.lrpripURI, 
							lrp.lrpsipURI,
							lrp.lrpcrnbipURI);
			
			lrppdis.add(lrppdi);
			
			if ( i > 0 ) {
				
				PRP prpb = prps.get(i-1);
				LRP lrpb = lrps.get(i-1);
			
				LogicalResourcesProviderPortsDataI lrpbpdi = 
						new LogicalResourcesProviderPortsData(
								lrpb.lrpURI, 
								lrpb.lrpmipURI, 
								lrpb.lrpripURI, 
								lrpb.lrpsipURI,
								lrpb.lrpcrnbipURI);

				prpb.prpmop.connectPhysicalResourcesProvider(prppdi);
				lrpb.lrpmop.connectLogicalResourcesProvider(lrppdi);
				lrp.lrpmop.connectLogicalResourcesProviderNotifyBack(lrpbpdi);
				
				if ( i == (BRANCHES - 1) ) {
					prpb = prp;
					prp = prps.get(0);
					lrpb = lrp;
					lrp = lrps.get(0);
					
					prppdi = new PhysicalResourcesProviderPortsData(
							prp.prpURI, 
							prp.prpmipURI, 
							prp.prpripURI, 
							prp.prpsipURI);
					
					lrppdi = new LogicalResourcesProviderPortsData(
							lrp.lrpURI, 
							lrp.lrpmipURI, 
							lrp.lrpripURI, 
							lrp.lrpsipURI,
							lrp.lrpcrnbipURI);
					
					lrpbpdi = new LogicalResourcesProviderPortsData(
							lrpb.lrpURI, 
							lrpb.lrpmipURI, 
							lrpb.lrpripURI, 
							lrpb.lrpsipURI,
							lrpb.lrpcrnbipURI);
					
					prpb.prpmop.connectPhysicalResourcesProvider(prppdi);
					lrpb.lrpmop.connectLogicalResourcesProvider(lrppdi);
					lrp.lrpmop.connectLogicalResourcesProviderNotifyBack(lrpbpdi);
					
				}
				
			}
			
		}		
		
		ac = new AdmissionController("ac", lrppdis);
		addDeployedComponent(ac);
		ac.start();
		
	}
	
	@Override
	public void start() throws Exception {
		super.start();
		
		connect();
	}
	
}
