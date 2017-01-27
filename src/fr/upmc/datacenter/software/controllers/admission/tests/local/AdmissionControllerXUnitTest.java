package fr.upmc.datacenter.software.controllers.admission.tests.local;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class AdmissionControllerXUnitTest {

	public static AdmissionControllerCVMExtremScaleUp acCVMX;
	
	@Rule
	public TestRule watcher = new TestWatcher() {
	   protected void starting(Description description) {
	      System.out.println("\n\t>>>>>> Starting test: " + description.getMethodName() + " <<<<<<\n");
	   }
	};
	
	@BeforeClass
	public static void instanciate() throws Exception {
		AdmissionControllerCVMExtremScaleUp.BRANCHES = 500;
		acCVMX = new AdmissionControllerCVMExtremScaleUp();
		acCVMX.deploy();
		acCVMX.start();
	}
		
	@Test
	public void nothing () {
		
	}
	
	/**
	 * Creation de 500 branches qui vont tourner en simultané
	 * 
	 * @throws Exception
	 */
	
	@Test
	public void submitApplication() throws Exception {
		
		for (int i = 0; i < AdmissionControllerCVMExtremScaleUp.BRANCHES; i++) {
			System.out.println("Application " + (i + 1) + " deployed");
			acCVMX.ac.submitApplication();
		}
		
		Thread.sleep(30 * 60 * 1000);
	}
	
}
