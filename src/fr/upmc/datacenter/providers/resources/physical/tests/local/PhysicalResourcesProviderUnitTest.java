package fr.upmc.datacenter.providers.resources.physical.tests.local;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;

/**
 * Batterie de tests basés sur la simulation {@link PhysicalResourcesProviderCVM}
 * Quelques tests sont réalisés pour savoir si tout va bien dans un fonctionnement nominal.
 * 
 * 
 * @author Daniel RADEAU
 *
 */

public class PhysicalResourcesProviderUnitTest {
	
	public static PhysicalResourcesProviderCVM prpcvm;
	public static List<AllocatedCore> acs = new ArrayList<>();
	
	@BeforeClass
	public static void instanciate() throws Exception {
		PhysicalResourcesProvider.LOGGING = true;
		prpcvm = new PhysicalResourcesProviderCVM();
		prpcvm.deploy();
		prpcvm.start();
	}
	
	public void addToAllocatedCores(AllocatedCore[] acs) {
		for (AllocatedCore ac: acs) {
			PhysicalResourcesProviderUnitTest.acs.add(ac);
		}
	}
	
	@After
	public void releaseAllocatedCores() throws Exception {
		AllocatedCore[] acs = new AllocatedCore[PhysicalResourcesProviderUnitTest.acs.size()];
		for (int i = 0; i < PhysicalResourcesProviderUnitTest.acs.size(); i++)
			acs[i] = PhysicalResourcesProviderUnitTest.acs.get(i);
		prpcvm.prpsopA.releaseCores(acs);
		PhysicalResourcesProviderUnitTest.acs.clear();
	}
	
	@Test
	public void allocateCoresNormal() throws Exception {
		addToAllocatedCores(prpcvm.prpsopA.allocateCores(1));
	}
	
	@Test
	public void allocateCoresMaximum() throws Exception {
		addToAllocatedCores(prpcvm.prpsopA.allocateCores(32));
	}
	
	@Test(expected = Exception.class)
	public void allocateCoresAnormalLow() throws Exception {
		addToAllocatedCores(prpcvm.prpsopA.allocateCores(-1));
	}
	
	@Test
	public void allocateCoresAnormalHigh() throws Exception {
		addToAllocatedCores(prpcvm.prpsopA.allocateCores(33));
	}
	
	@Test
	public void allocateCoresNormalOver() throws Exception {
		addToAllocatedCores(prpcvm.prpsopA.allocateCores(32));
		addToAllocatedCores(prpcvm.prpsopA.allocateCores(1));
	}
	
	@Test(expected = Exception.class)
	public void allocateCoresHasNoCore() throws Exception {
		addToAllocatedCores(prpcvm.prpsopA.allocateCores(32));
		addToAllocatedCores(prpcvm.prpsopA.allocateCores(32));
		addToAllocatedCores(prpcvm.prpsopA.allocateCores(1));
	}
	
	@Test
	public void releaseCoresByRing() throws Exception {
		addToAllocatedCores(prpcvm.prpsopA.allocateCores(10));
			
	}
	
	@Test
	public void variationCoreFrenquency() throws Exception {
		addToAllocatedCores(prpcvm.prpsopA.allocateCores(32));
		
		for (AllocatedCore ac : PhysicalResourcesProviderUnitTest.acs) { 
			prpcvm.prpsopA.increaseCoreFrenquency(ac);
			prpcvm.prpsopA.decreaseCoreFrenquency(ac);
		}	
	}
	
	@Test
	public void variationProcessorFrenquency() throws Exception {
		addToAllocatedCores(prpcvm.prpsopA.allocateCores(32));
		
		for (AllocatedCore ac : PhysicalResourcesProviderUnitTest.acs) { 
			prpcvm.prpsopA.increaseProcessorFrenquency(ac);
			prpcvm.prpsopA.decreaseProcessorFrenquency(ac);
		}
		
	}
	
	@Test
	public void variationComputerFrenquency() throws Exception {
		addToAllocatedCores(prpcvm.prpsopA.allocateCores(32));
		
		for (AllocatedCore ac : PhysicalResourcesProviderUnitTest.acs) { 
			prpcvm.prpsopA.increaseComputerFrenquency(ac);
			prpcvm.prpsopA.decreaseComputerFrenquency(ac);
		}
		
	}
}
