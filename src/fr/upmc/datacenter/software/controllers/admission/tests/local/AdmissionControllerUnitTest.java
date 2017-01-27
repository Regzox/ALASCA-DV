package fr.upmc.datacenter.software.controllers.admission.tests.local;

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

public class AdmissionControllerUnitTest {


	public static AdmissionControllerCVM acCVM;
	public static List<AllocatedApplicationVM> aavms;
	
	@Rule
	public TestRule watcher = new TestWatcher() {
	   protected void starting(Description description) {
	      System.out.println("\n\t>>>>>> Starting test: " + description.getMethodName() + " <<<<<<\n");
	   }
	};
	
	@BeforeClass
	public static void instanciate() throws Exception {
		 acCVM = new AdmissionControllerCVM();
		 aavms = new ArrayList<>();
		 PhysicalResourcesProvider.LOGGING = true;
		 acCVM.deploy();
		 acCVM.start();
	}
		
	@Test
	public void nothing () {
		
	}
	
	@Test
	public void submitApplication() throws Exception {
		acCVM.ac.submitApplication();
		acCVM.ac.submitApplication();
		Thread.sleep(30 * 60 * 1000);
	}
	
}
