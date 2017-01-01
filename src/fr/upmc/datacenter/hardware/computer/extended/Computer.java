package fr.upmc.datacenter.hardware.computer.extended;

import java.util.Map;
import java.util.Set;

import fr.upmc.datacenter.software.applicationvm.extended.interfaces.CoreReleasingI;

public class Computer 
	extends 
		fr.upmc.datacenter.hardware.computers.Computer
	implements
		CoreReleasingI
{

	protected String	cripURI;
//						crnopURI;
	
	public Computer(	String computerURI, 
						Set<Integer> possibleFrequencies, 
						Map<Integer, Integer> processingPower,
						int defaultFrequency, 
						int maxFrequencyGap, 
						int numberOfProcessors, 
						int numberOfCores,
						String computerServicesInboundPortURI, 
						String computerStaticStateDataInboundPortURI,
						String computerDynamicStateDataInboundPortURI,
						String computerCoreReleasingInboundPortURI
//						String computerCoreReleasingNotificationOutboundPortURI
						) throws Exception 
	{
		super(	computerURI, 
				possibleFrequencies, 
				processingPower, 
				defaultFrequency, 
				maxFrequencyGap, 
				numberOfProcessors,
				numberOfCores, 
				computerServicesInboundPortURI, 
				computerStaticStateDataInboundPortURI,
				computerDynamicStateDataInboundPortURI);
		
		cripURI = computerCoreReleasingInboundPortURI;
//		crnopURI = computerCoreReleasingNotificationOutboundPortURI;
	}

	@Override
	public void releaseCore() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void releaseCores(int cores) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void releaseMaximumCores() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
