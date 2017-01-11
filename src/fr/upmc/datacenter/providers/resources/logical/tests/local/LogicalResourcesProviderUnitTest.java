package fr.upmc.datacenter.providers.resources.logical.tests.local;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.upmc.datacenter.providers.resources.annotations.Ring;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;

public class LogicalResourcesProviderUnitTest {
	
	public static LogicalResourcesProviderCVM lrpCVM;
	public static List<AllocatedApplicationVM> aavms;
	
	@BeforeClass
	public static void instanciate() throws Exception {
		 lrpCVM = new LogicalResourcesProviderCVM();
		 aavms = new ArrayList<>();
		 PhysicalResourcesProvider.LOGGING = true;
		 lrpCVM.deploy();
		 lrpCVM.start();
	}
	
	public void addToAllocatedApplicationVM(AllocatedApplicationVM[] aavms) {
		for (AllocatedApplicationVM avm: aavms) {
			LogicalResourcesProviderUnitTest.aavms.add(avm);
		}
	}
	
	@Before
	public void separator() {
		System.out.println("\n/////////////////////////////////////////////////////////////\n");
	}
	
	@After
	public void releaseAllocatedApplicationVM() throws Exception {
		AllocatedApplicationVM[] aavms = new AllocatedApplicationVM[LogicalResourcesProviderUnitTest.aavms.size()];
		for (int i = 0; i < LogicalResourcesProviderUnitTest.aavms.size(); i++)
			aavms[i] = LogicalResourcesProviderUnitTest.aavms.get(i);
		lrpCVM.lrpsop_A.releaseApplicationVMs(aavms);
		LogicalResourcesProviderUnitTest.aavms.clear();
	}
	
	@Test
	public void nothing() {
		
	}

	@Test
	public void allocateApplicationVM() throws Exception {
		assert lrpCVM.lrpsop_A != null;
		
		addToAllocatedApplicationVM(lrpCVM.lrpsop_A.allocateApplicationVMs(32));
	}
	
	@Test
	public void allocateApplicationAgainVM() throws Exception {
		assert lrpCVM.lrpsop_A != null;
		
		addToAllocatedApplicationVM(lrpCVM.lrpsop_A.allocateApplicationVMs(32));
	}
	
	//@Test
	public void allocateApplicationVMExplode() throws Exception {
		assert lrpCVM.lrpsop_A != null;
		
		addToAllocatedApplicationVM(lrpCVM.lrpsop_A.allocateApplicationVMs(65));
	}
	
	@Test
	public void increaseApplicationVMCores() throws Exception {
		AllocatedApplicationVM[] aavms = lrpCVM.lrpsop_A.allocateApplicationVMs(1);
		addToAllocatedApplicationVM(aavms);
		lrpCVM.lrpsop_A.increaseApplicationVMCores(aavms[0], 3);
	}
	
	/**
	 * Erreur dans le cas d'allocation de tous les coeurs aux AVM en attente et plus aucun coeru disponible sur une même machine pour augmenter
	 * le nombre de coeurs de l'AVM en question. (Biaisé par les tests exécutés avant) à tester en premier ou seul.
	 * 
	 * @throws Exception
	 */
	
	@Test
	public void increaseApplicationVMCoresMaxAndOver() throws Exception {
		AllocatedApplicationVM[] aavms = lrpCVM.lrpsop_A.allocateApplicationVMs(1);
		addToAllocatedApplicationVM(aavms);
		lrpCVM.lrpsop_A.increaseApplicationVMCores(aavms[0], 33);
	}
	
	@Test
	public void increaseApplicationVMFrenquency() throws Exception {
		AllocatedApplicationVM[] aavms = lrpCVM.lrpsop_A.allocateApplicationVMs(1);
		addToAllocatedApplicationVM(aavms);
		lrpCVM.lrpsop_A.increaseApplicationVMFrequency(aavms[0]);
	}
	
	@Test
	public void decreaseApplicationVMFrenquency() throws Exception {
		AllocatedApplicationVM[] aavms = lrpCVM.lrpsop_A.allocateApplicationVMs(1);
		addToAllocatedApplicationVM(aavms);
		lrpCVM.lrpsop_A.decreaseApplicationVMFrequency(aavms[0]);
	}
	
	/// RINGS TESTS
		
	@Test @Ring
	public void allocateApplicationVMExplodeRing() throws Exception {
		assert lrpCVM.lrpsop_A != null;
		
		addToAllocatedApplicationVM(lrpCVM.lrpsop_A.allocateApplicationVMs(65));
		System.out.println("IN TEST, AAVMS : " + aavms.size());
	}
	
	@Test @Ring
	public void increaseApplicationVMCoresRing() throws Exception {
		AllocatedApplicationVM[] aavms = lrpCVM.lrpsop_A.allocateApplicationVMs(1);
		addToAllocatedApplicationVM(aavms);
		lrpCVM.lrpsop_B.increaseApplicationVMCores(aavms[0], 3);
	}
	
	/**
	 * Erreur dans le cas d'allocation de tous les coeurs aux AVM en attente et plus aucun coeru disponible sur une même machine pour augmenter
	 * le nombre de coeurs de l'AVM en question. (Biaisé par les tests exécutés avant) à tester en premier ou seul.
	 * 
	 * @throws Exception
	 */
	
	@Test @Ring
	public void increaseApplicationVMCoresMaxAndOverRing() throws Exception {
		AllocatedApplicationVM[] aavms = lrpCVM.lrpsop_A.allocateApplicationVMs(1);
		addToAllocatedApplicationVM(aavms);
		lrpCVM.lrpsop_B.increaseApplicationVMCores(aavms[0], 33);
	}
	
	@Test @Ring
	public void increaseApplicationVMFrenquencyRing() throws Exception {
		AllocatedApplicationVM[] aavms = lrpCVM.lrpsop_A.allocateApplicationVMs(1);
		addToAllocatedApplicationVM(aavms);
		lrpCVM.lrpsop_B.increaseApplicationVMFrequency(aavms[0]);
	}
	
	@Test @Ring
	public void decreaseApplicationVMFrenquencyRing() throws Exception {
		AllocatedApplicationVM[] aavms = lrpCVM.lrpsop_A.allocateApplicationVMs(1);
		addToAllocatedApplicationVM(aavms);
		lrpCVM.lrpsop_B.decreaseApplicationVMFrequency(aavms[0]);
	}
	
}
