package fr.upmc.datacenter.hardware.computer.extended;

import java.util.Map;
import java.util.Set;

import fr.upmc.datacenter.hardware.computer.extended.interfaces.ComputerCoreReleasingI;
import fr.upmc.datacenter.hardware.computer.extended.ports.ComputerCoreReleasingInboundPort;

public class Computer 
	extends 
		fr.upmc.datacenter.hardware.computers.Computer
	implements
		ComputerCoreReleasingI
{

	protected String	ccripURI;
	protected String 	cpdsdipURI;
	
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
		
		ccripURI = computerCoreReleasingInboundPortURI;
		
		if (!offeredInterfaces.contains(ComputerCoreReleasingI.class))
			offeredInterfaces.add(ComputerCoreReleasingI.class);
		
		ComputerCoreReleasingInboundPort ccrip = new ComputerCoreReleasingInboundPort(computerCoreReleasingInboundPortURI, ComputerCoreReleasingI.class, this);
		addPort(ccrip);
		ccrip.publishPort();
		
	}
		
}
