package fr.upmc.datacenter.hardware.processor.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.processors.Processor;

/**
 * Enumeration de modèle de processeurs factices pour la génération d'ordinateurs {@link Computer}
 * se basant dessus.
 * 
 * @author Daniel RADEAU
 *
 */

public enum Model {
	
	I7_6700K (
			"i7-6700k", 
			Arrays.asList(
					4000,
					4050,
					4100,
					4150,
					4200),
			Arrays.asList(
					4000, 4000000,
					4050, 4050000,
					4100, 4100000,
					4150, 4150000,
					4200, 4200000),
			4000,
			100,
			8),
	
	I5_480M ("i5-480m",
			Arrays.asList(
					2600,
					2650,
					2700,
					2750,
					2800,
					2850,
					2900),
			Arrays.asList(
					2600, 2600000,
					2650, 2650000,
					2700, 2700000,
					2750, 2750000,
					2800, 2800000,
					2850, 2850000,
					2900, 2900000),
			2600, 
			100, 
			4);

	private Processor processor = null;
	private Set<Integer> admissibleFrequencies = null;
	private Map<Integer, Integer> processingPower = null;	
	
	private Model(
			String processorURI,
			List<Integer> admissibleFrequencies,
			List<Integer> processingPower,
			int defaultFrequency,
			int maxFrequencyGap,
			int numberOfCores
			) {
		Random rd = new Random();

		this.admissibleFrequencies = new HashSet<>(admissibleFrequencies);
		this.processingPower = new HashMap<>();

		for (int i = 0; i <= processingPower.size()-1; i += 2)
			this.processingPower.put(processingPower.get(i), processingPower.get(i + 1));
		
		try {
			processor = new Processor(
					processorURI + rd.nextInt(), 
					this.admissibleFrequencies, 
					this.processingPower, 
					defaultFrequency, 
					maxFrequencyGap, 
					numberOfCores, 
					"prototype" + rd.nextInt(), 
					"prototype" + rd.nextInt(), 
					"prototype" + rd.nextInt(), 
					"prototype" + rd.nextInt(), 
					"prototype" + rd.nextInt()
					);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retourne le {@link Processor} factice associé au modèle.
	 * 
	 * @return
	 */
	
	public Processor getProcessor() {
		return processor;
	}
	
	/**
	 * Retourne les fréquences admissibles pour ce modèle de {@link Processor}.
	 * 
	 * @return
	 */
	
	public Set<Integer> getAdmissibleFrequencies() {
		return admissibleFrequencies;
	}
	
	/**
	 * Retourne la table des puissances de calcul en fonction des fréquences pour ce modèle de {@link Processor}.
	 * 
	 * @return
	 */
	
	public Map<Integer, Integer> getProcessingPower() {
		return processingPower;
	}
}
