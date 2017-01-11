package fr.upmc.datacenter.providers.resources.physical.tests.local;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.data.ComputerPortsData;
import fr.upmc.datacenter.data.PhysicalResourcesProviderPortsData;
import fr.upmc.datacenter.data.interfaces.ComputerPortsDataI;
import fr.upmc.datacenter.data.interfaces.PhysicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.hardware.computer.extended.Computer;
import fr.upmc.datacenter.hardware.computer.stock.Stock;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.processor.model.Model;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;
import fr.upmc.datacenter.providers.resources.physical.connectors.PhysicalResourcesProviderManagementConnector;
import fr.upmc.datacenter.providers.resources.physical.connectors.PhysicalResourcesProviderServicesConnector;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderManagementI;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderRequestingI;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderServicesI;
import fr.upmc.datacenter.providers.resources.physical.ports.PhysicalResourcesProviderManagementOutboundPort;
import fr.upmc.datacenter.providers.resources.physical.ports.PhysicalResourcesProviderRequestingOutboundPort;
import fr.upmc.datacenter.providers.resources.physical.ports.PhysicalResourcesProviderServicesOutboundPort;

/**
 * CVM de test du composant {@link PhysicalResourcesProvider}.
 * Contexte : 2 {@link Computer}, avec 4 {@link Processor} basés sur le {@link Model} I7-6700K.
 * 
 * @author Daniel RADEAU
 *
 */

public class PhysicalResourcesProviderCVM extends AbstractCVM {

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
	
	public Stock stockA;
	public PhysicalResourcesProvider prpA;
	
	public Stock stockB;
	public PhysicalResourcesProvider prpB;

	public PhysicalResourcesProviderManagementOutboundPort prpmopA;
	public PhysicalResourcesProviderRequestingOutboundPort prpropA;
	public PhysicalResourcesProviderServicesOutboundPort prpsopA;
	
	public PhysicalResourcesProviderManagementOutboundPort prpmopB;
	public PhysicalResourcesProviderRequestingOutboundPort prpropB;
	public PhysicalResourcesProviderServicesOutboundPort prpsopB;

	public PhysicalResourcesProviderCVM() throws Exception {
		super();

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
	
	public static void main(String[] args) {
		PhysicalResourcesProviderCVM prpcvm = null;
		try {
			prpcvm = new PhysicalResourcesProviderCVM();
			prpcvm.deploy();
			prpcvm.start();
			
			/** TEST DES METHODES RING **/
			
				PhysicalResourcesProvider.LOGGING = true;
				prpcvm.prpA.toggleLogging();
				prpcvm.prpA.toggleTracing();
				prpcvm.prpB.toggleLogging();
				prpcvm.prpB.toggleTracing();
								
				AllocatedCore[] acs1 = prpcvm.prpsopA.allocateCores(16);		
				AllocatedCore[] acs2 = prpcvm.prpsopB.allocateCores(16);
				
				AllocatedCore[] a = {acs1[0], acs1[1], acs1[2], acs1[3]};
				AllocatedCore[] b = {acs1[4], acs1[5], acs1[6], acs1[7]};
				AllocatedCore[] c = {acs1[8], acs1[9], acs1[10], acs1[11]};
				AllocatedCore[] d = {acs1[12], acs1[13], acs1[14], acs1[15]};
				
				prpcvm.prpsopB.increaseCoreFrenquency(a[0]);
				prpcvm.prpsopB.decreaseCoreFrenquency(a[0]);
				
				prpcvm.prpsopB.increaseProcessorFrenquency(b[0]);
				prpcvm.prpsopB.decreaseProcessorFrenquency(b[0]);
				
				prpcvm.prpsopB.increaseComputerFrenquency(c[0]);
				prpcvm.prpsopB.decreaseComputerFrenquency(c[0]);
				
				prpcvm.prpsopB.increaseCoreFrenquency(d[0]);
				prpcvm.prpsopB.decreaseCoreFrenquency(d[0]);
				
				prpcvm.prpsopA.releaseCores(acs2);
				prpcvm.prpsopB.releaseCores(acs1);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				prpcvm.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
	}
}