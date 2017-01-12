package fr.upmc.datacenter.software.controllers.performance;

import java.io.Serializable;

public class AllocatedRequestGenerator implements Serializable {

	private static final long serialVersionUID = -6370750753354249999L;

	public double meanInterArrivalTime;
	public long meanNumberOfInstructions;
	public String 	rgURI,
					rgmipURI,
					rgrsopURI,
					rgrnipURI;
	
	public AllocatedRequestGenerator(
			String rgURI,
			double meanInterArrivalTime,
			long meanNumberOfInstructions,
			String rgmipURI,
			String rgrsopURI,
			String rgrnipURI) 
	{
		this.rgURI = rgURI;
		this.meanInterArrivalTime = meanInterArrivalTime;
		this.meanNumberOfInstructions = meanNumberOfInstructions;
		this.rgmipURI = rgmipURI;
		this.rgrsopURI = rgrsopURI;
 		this.rgrnipURI = rgrnipURI;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof AllocatedRequestGenerator) )
			return false;
		AllocatedRequestGenerator arg = (AllocatedRequestGenerator) obj;
		return	rgURI.equals(arg.rgURI) &&
				(meanInterArrivalTime == (arg.meanInterArrivalTime)) &&
				(meanNumberOfInstructions == (arg.meanNumberOfInstructions)) &&
				rgmipURI.equals(arg.rgmipURI) &&
				rgrsopURI.equals(arg.rgrsopURI) &&
				rgrnipURI.equals(arg.rgrnipURI);
	}

}
