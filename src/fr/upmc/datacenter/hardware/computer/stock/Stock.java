package fr.upmc.datacenter.hardware.computer.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.computer.extended.Computer;
import fr.upmc.datacenter.hardware.processor.model.Model;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.software.enumerations.Tag;

/**
 * Parc informatique, composé de <em>n</em> ordinateurs ayant les mêmes caractéristiques
 * 
 * @author Daniel RADEAU
 *
 */

public class Stock {

	String uri;
	Model processorModel;
	List<Computer> computers;
	List<String> computersURI;
	Map<String, String> computerServicesInboundPortURIMap;
	Map<String, String> computerStaticStateDataInboundPortURIMap;
	Map<String, String> computerDynamicStateDataInboundPortURIMap;
	Map<String, String> computerCoreReleasingInboundPortURIMap;
	
	/**
	 * Création d'un parc informatique nommé par une URI,
	 * composé du nombre <code>computerUnits</code> de {@link Computer},
	 * eux-mêmes composés de <code>processorUnits</code> de {@link Processor} 
	 * de <code>model</code> {@link Model}.
	 * 
	 * @param uri
	 * @param computerUnits
	 * @param processorUnits
	 * @param model
	 * @throws Exception
	 */
	
	public Stock(String uri, Integer computerUnits, Integer processorUnits, Model model) throws Exception {
		computers = new ArrayList<>();
		computersURI = new ArrayList<>();
		computerServicesInboundPortURIMap = new HashMap<>();
		computerStaticStateDataInboundPortURIMap = new HashMap<>();
		computerDynamicStateDataInboundPortURIMap = new HashMap<>();
		computerCoreReleasingInboundPortURIMap = new HashMap<>();
		
		this.uri = uri;
		this.processorModel = model;
		
		for (int i = computerUnits; i > 0; i--) {
			String computerURI = generateURI(Tag.COMPUTER);
			String computerServicesInboundPortURI = generateURI(Tag.COMPUTER_SERVICES_INBOUND_PORT);
			String computerStaticStateDataInboundPort = generateURI(Tag.COMPUTER_STATIC_STATE_DATA_INBOUND_PORT);
			String computerDynamicStateDataInboundPort = generateURI(Tag.COMPUTER_DYNAMIC_STATE_DATA_INBOUND_PORT);
			String computerCoreReleasingInboundPort = generateURI(Tag.COMPUTER_CORE_RELEASING_INBOUND_PORT);
			
			Computer computer = new Computer(
					computerURI, 
					model.getAdmissibleFrequencies(), 
					model.getProcessingPower(), 
					model.getProcessor().getDefaultFrequency(), 
					model.getProcessor().getMaxFrequencyGap(), 
					processorUnits, 
					model.getProcessor().getNumberOfCores(), 
					computerServicesInboundPortURI, 
					computerStaticStateDataInboundPort, 
					computerDynamicStateDataInboundPort,
					computerCoreReleasingInboundPort);
			AbstractCVM.theCVM.addDeployedComponent(computer);
			
			computers.add(computer);
			computersURI.add(computerURI);
			computerServicesInboundPortURIMap.put(computerURI, computerServicesInboundPortURI);
			computerStaticStateDataInboundPortURIMap.put(computerURI, computerStaticStateDataInboundPort);
			computerDynamicStateDataInboundPortURIMap.put(computerURI, computerDynamicStateDataInboundPort);
			computerCoreReleasingInboundPortURIMap.put(computerURI, computerCoreReleasingInboundPort);
		}
	}
	
	/**
	 * Génére un nouveau {@link Computer} composé du nombre <code>processorUnits</code> de {@link Processor} de modèle {@link Model}
	 * 
	 * @param processorUnits
	 * @param model
	 * @return
	 * @throws Exception
	 */
	
	public static Computer makeComputer(Integer processorUnits, Model model) throws Exception {
		Random rd = new Random();
		Computer computer = new Computer(
				Tag.COMPUTER.name() + rd.nextInt(), 
				model.getAdmissibleFrequencies(), 
				model.getProcessingPower(), 
				model.getProcessor().getDefaultFrequency(), 
				model.getProcessor().getMaxFrequencyGap(), 
				processorUnits, 
				model.getProcessor().getNumberOfCores(), 
				Tag.COMPUTER_SERVICES_INBOUND_PORT.name() + rd.nextInt(), 
				Tag.COMPUTER_STATIC_STATE_DATA_INBOUND_PORT.name() + rd.nextInt(), 
				Tag.COMPUTER_DYNAMIC_STATE_DATA_INBOUND_PORT.name() + rd.nextInt(),
				Tag.COMPUTER_CORE_RELEASING_INBOUND_PORT.name() + rd.nextInt());
		return computer;
	}
	
	public String generateURI(Object tag) {
		System.out.println(uri + '_' + tag + '_' + (computers.size() + 1));
		return uri + '_' + tag + '_' + (computers.size() + 1);
	}

	public String getUri() {
		return uri;
	}

	public Model getProcessorModel() {
		return processorModel;
	}

	public List<Computer> getComputers() {
		return computers;
	}

	public List<String> getComputersURI() {
		return computersURI;
	}

	public Map<String, String> getComputerServicesInboundPortURIMap() {
		return computerServicesInboundPortURIMap;
	}

	public Map<String, String> getComputerStaticStateDataInboundPortURIMap() {
		return computerStaticStateDataInboundPortURIMap;
	}

	public Map<String, String> getComputerDynamicStateDataInboundPortURIMap() {
		return computerDynamicStateDataInboundPortURIMap;
	}
	
	public Map<String, String> getComputerCoreReleasingInboundPortURIMap() {
		return computerCoreReleasingInboundPortURIMap;
	}
	
}
