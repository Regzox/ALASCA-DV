package fr.upmc.datacenter.software.controllers.performance.tests.local;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;

public class PerformanceControllerUnitTest {


	public static PerformanceControllerCVM pcCVM;
	public static List<AllocatedApplicationVM> aavms;
	
	@Rule
	public TestRule watcher = new TestWatcher() {
	   protected void starting(Description description) {
	      System.out.println("\n\t>>>>>> Starting test: " + description.getMethodName() + " <<<<<<\n");
	   }
	};
	
	@BeforeClass
	public static void instanciate() throws Exception {
		 pcCVM = new PerformanceControllerCVM();
		 aavms = new ArrayList<>();
		 PhysicalResourcesProvider.LOGGING = true;
		 pcCVM.deploy();
		 pcCVM.start();
//		 Dispatcher.DYNAMIC_STATE_DATA_DISPLAY = true;
	}
		
	@Test
	public void nothing () {
		
	}
	
	@Test
	public void acceptApplication() throws Exception {
		pcCVM.pcsop_A.acceptApplication();
		Thread.sleep(30 * 60 * 1000);
	}
	
	//@Test(expected = Exception.class)
	public void unauthorizedAcceptApplication() throws Exception {
		pcCVM.pcsop_A.acceptApplication();
		pcCVM.pcsop_A.acceptApplication();
	}
	
	
}
