package fr.upmc.datacenter.software.controllers.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourcesProviderDynamicState;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;

/**
 * Etat dynamique du {@link PerformanceController}.
 * 
 * De la même manière que {@link LogicalResourcesProviderDynamicState} on assure les opérations
 * par la mise en place de synchrnisations et de contrats.
 * 
 * @author Daniel RADEAU
 *
 */

public class PerformanceControllerDynamicState {

	protected Map<AllocatedDispatcher, AllocatedRequestGenerator> argMap;
	protected Map<AllocatedDispatcher, List<AllocatedApplicationVM>> aavmsMap;
	
	protected Map<AllocatedApplicationVM, Integer> aavmsCoreCountMap;
	
	protected List<AllocatedApplicationVMStateChanging> stateChanging;
	
	/**
	 * Matérialise le changement d'état d'un {@link AllocatedApplicationVM} au moment de la variation de son nombre {@link AllocatedCore}
	 * Tant que des la variation de coeurs n'est pas nulle alors on réalise une étape de variation.
	 * Le but étant de pouvoir synchroniser l'état du {@link PerformanceController} et celui du {@link LogicalResourceProvider}
	 * lors de opérations du genre variation du nombre de coeurs d'un {@link AllocatedApplicationVM}.
	 * {@link AllocatedApplicationVMStateChanging} trouve plutôt son utilité dans les opérations de réduction de coeurs,
	 * pouvant prendre un délai relativement important d'une demande à l'autre.
	 * Le principe serait n'autoriser les opérations de variation de coeurs que lorsque aucun 
	 * {@link AllocatedApplicationVMStateChanging} correspondant à une {@link AllocatedApplicationVM} 
	 * n'est présent dans la liste du {@link PerformanceControllerDynamicState} 
	 * 
	 * @author Daniel RADEAU
	 *
	 */
	
	class AllocatedApplicationVMStateChanging {
		protected AllocatedApplicationVM aavm;
		protected Integer coreVariation;
		
		public AllocatedApplicationVMStateChanging(AllocatedApplicationVM aavm, Integer coreVariation) throws Exception {
			assert aavm != null;
			assert coreVariation != null;
			
			this.aavm = aavm;
			this.coreVariation = coreVariation;
		}
		
		/**
		 * Si l'état de {@link AllocatedApplicationVM} est considéré comme changé
		 * 
		 * @return <b>vrai</b> si nous avons terminé la variation, sinon <b>faux</b>
		 */
		
		public boolean isChanged() throws Exception {
			System.out.println(1);
			assert coreVariation != null;
			System.out.println(2);
			return coreVariation == 0;
		}
		
		public AllocatedApplicationVM getAllocatedApplicationVM() throws Exception {
			System.out.println(3);
			assert aavm != null;
			System.out.println(4);
			return aavm;
		}
		
		public Integer getCoreVariation() throws Exception {
			assert coreVariation != null;
			return coreVariation;
		}
		
		/**
		 * Effectue une étape de décrementation ou d'incrémentation, le but étant de d'atteindre 0
		 * 
		 * @return <b>vrai</b> si à l'issue de l'étape nous avons terminé la variation, sinon <b>faux</b>
		 */
		
		public synchronized boolean step() throws Exception {
			System.out.println(5);
			assert aavm != null;
			assert coreVariation != null;
			
			if (coreVariation < 0)
				coreVariation++;
			else if (coreVariation > 0)
				coreVariation--;
			System.out.println(6);
			return isChanged();
		}
		
		/**
		 * Sont considérés les mêmes si leurs {@link AllocatedApplicationVM} sont identiques
		 */
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AllocatedApplicationVMStateChanging))
				return false;
			return this.aavm.equals(((AllocatedApplicationVMStateChanging) obj).aavm);
		}
		
	}
	
	public PerformanceControllerDynamicState() {
		argMap = new HashMap<>();
		aavmsMap = new HashMap<>();
		aavmsCoreCountMap = new HashMap<>();
		stateChanging = new ArrayList<>();
	}
	
	/**
	 * Ajout d'un {@link Dispatcher} avec ses conditions initiales
	 * 
	 * @param adsp {@link AllocatedDispatcher} à ajouter
	 * @param arg {@link AllocatedRequestGenerator} lié au {@link AllocatedDispatcher}
	 * @param aavms tableau des {@link AllocatedApplicationVM} liés au {@link AllocatedDispatcher}
	 * @param coreCounts tableau des nombres de {@link AllocatedCore} associés aux {@link AllocatedApplicationVM}
	 * @throws Exception
	 */
	
	public synchronized void addAllocatedDispatcher(
			AllocatedDispatcher adsp,
			AllocatedRequestGenerator arg,
			AllocatedApplicationVM[] aavms,
			Integer[] coreCounts
			) throws Exception 
	{
		System.out.println(7);
		assert adsp != null;
		assert arg != null;
		assert aavms != null;
		assert coreCounts != null;
		assert aavms.length > 0;
		assert argMap.get(adsp) == null;
		assert aavmsMap.get(adsp) == null;
		for (int i = 0; i < aavms.length; i++) {
			assert aavms[i] != null;
			assert aavmsCoreCountMap.get(aavms[i]) == null;
		}
		
		ArrayList<AllocatedApplicationVM> list = new ArrayList<>();

		for (int i = 0; i < aavms.length; i++) {
			list.add(aavms[i]);
			aavmsCoreCountMap.put(aavms[i], coreCounts[i]);
		}
		
		argMap.put(adsp, arg);
		aavmsMap.put(adsp, list);
		
		assert argMap.get(adsp) != null;
		assert aavmsMap.get(adsp) != null; 
		assert aavmsMap.get(adsp).size() == aavms.length;
		for (int i = 0; i < aavms.length; i++) {
			assert aavmsCoreCountMap.get(aavms[i]) != null;
		}
		System.out.println(8);
	}
	
	/**
	 * Supression d'un {@link AllocatedDispatcher} présent dans le {@link PerformanceControllerDynamicState}
	 * 
	 * @param adsp {@link AllocatedDispatcher} cible
	 * @throws Exception
	 */
	
	public synchronized void removeAllocatedDispatcher(AllocatedDispatcher adsp) throws Exception {
		System.out.println(9);
		assert adsp != null;
		assert argMap.get(adsp) != null;
		assert aavmsMap.get(adsp) != null;
		
		argMap.remove(adsp);
		aavmsMap.remove(adsp);
		
		assert argMap.get(adsp) == null;
		assert aavmsMap.get(adsp) == null;
		System.out.println(10);
	}
	
	/**
	 * Ajoute un {@link AllocatedApplicationVM} non présent dans le {@link PerformanceControllerDynamicState}
	 * 
	 * @param adsp adsp {@link AllocatedDispatcher} cible
	 * @param aavm {@link AllocatedApplicationVM} à ajouter
	 * @throws Exception
	 */
	
	public synchronized void addAllocatedApplicationVM(AllocatedDispatcher adsp, AllocatedApplicationVM aavm, Integer coreCount) throws Exception {
		System.out.println(11);
		assert adsp != null;
		assert aavm != null;
		assert aavmsMap.get(adsp) != null;
		assert !aavmsMap.get(adsp).contains(aavm);
		assert aavmsCoreCountMap != null;
		assert aavmsCoreCountMap.get(aavm) == null;
		
		int size = aavmsMap.get(adsp).size();
		aavmsMap.get(adsp).add(aavm);
		aavmsCoreCountMap.put(aavm, coreCount);
		
		assert aavmsMap.get(adsp).size() == (size + 1);
		System.out.println(12);
	}
	
	/**
	 * Supprime un {@link AllocatedApplicationVM} présent dans le {@link PerformanceControllerDynamicState}
	 * 
	 * @param adsp {@link AllocatedDispatcher} cible
	 * @param aavm {@link AllocatedApplicationVM} à supprimer
	 * @throws Exception
	 */
	
	public synchronized void removeAllocatedApplicationVM(AllocatedDispatcher adsp, AllocatedApplicationVM aavm) throws Exception {
		System.out.println(13);
		assert aavm != null;
		assert aavmsMap.get(adsp) != null;
		assert aavmsMap.get(adsp).contains(aavm);
		assert aavmsCoreCountMap.get(aavm) != null;
		
		int size = aavmsMap.get(adsp).size();
		aavmsMap.get(adsp).remove(aavm);
		aavmsCoreCountMap.remove(aavm);
		
		AllocatedApplicationVMStateChanging result = null;
		for (AllocatedApplicationVMStateChanging aavmsc : stateChanging)
			if ( aavmsc.getAllocatedApplicationVM().equals(aavm) )
				result = aavmsc;
		if ( result != null )
			stateChanging.remove(result);
		
		assert aavmsMap.get(adsp).size() == (size - 1);
		assert !aavmsMap.get(adsp).contains(aavm);
		assert aavmsCoreCountMap.get(aavm) == null;
		assert !stateChanging.contains(result);
		System.out.println(14);
	}
	
	/**
	 * Augmente le nombre de coeurs recensés pour la {@link AllocatedApplicationVM} en question
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param coreNumber nombre de coeurs à ajouter
	 * @throws Exception
	 */
	
	public synchronized void increaseAllocatedCoreCount(AllocatedApplicationVM aavm, Integer coreNumber) throws Exception {
		System.out.println(15);
		assert aavm != null;
		assert coreNumber != null;
		assert coreNumber > 0;
		assert aavmsCoreCountMap.get(aavm) != null;
		
		Integer coreCount = aavmsCoreCountMap.get(aavm);
		coreCount += coreNumber;
		aavmsCoreCountMap.put(aavm, coreCount);
		System.out.println(16);
	}
	
	/**
	 * Réduit le nombre de coeurs recensés pour la {@link AllocatedApplicationVM} en question
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param coreNumber nombre de coeurs à retirer
	 * @throws Exception
	 */
	
	public synchronized void decreaseAllocatedCoreCount(AllocatedApplicationVM aavm, Integer coreNumber) throws Exception {
		System.out.println(17);
		assert aavm != null;
		assert coreNumber != null;
		assert coreNumber > 0;
		assert aavmsCoreCountMap.get(aavm) != null;
		assert (aavmsCoreCountMap.get(aavm) - coreNumber) >= 0;
		
		Integer coreCount = aavmsCoreCountMap.get(aavm);
		coreCount -= coreNumber;
		aavmsCoreCountMap.put(aavm, coreCount);
		System.out.println(18);
	}
	
	/**
	 * Ajoute un {@link AllocatedApplicationVMStateChanging} dans la liste des états changeant
	 * 
	 * @param aavm {@link AllocatedApplicationVM} concernée
	 * @param coreVariation Variation à opérer
	 * @throws Exception
	 */
	
	public synchronized void pushAllocatedApplicationVMChangingState(AllocatedApplicationVM aavm, Integer coreVariation) throws Exception {
		System.out.println(19);
		assert aavm != null;
		assert coreVariation != null;
		assert stateChanging != null;

		System.out.println("OK");
		
		int size = stateChanging.size();
		AllocatedApplicationVMStateChanging aavmsc = new AllocatedApplicationVMStateChanging(aavm, coreVariation);
		
		
		assert !stateChanging.contains(aavmsc);
		
		stateChanging.add(aavmsc);
		
		assert stateChanging.size() == (size + 1); 
		System.out.println(20);
	}
	
	/**
	 * Effectue un pas changement d'état pour la {@link AllocatedApplicationVM} cible.
	 * Un pas met à jour toute la structure de {@link PerformanceControllerDynamicState}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @return vrai si la cible à fini sa transition (allocation ou libération complète de coeurs)
	 * @throws Exception
	 */
	
	public synchronized boolean step(AllocatedApplicationVM aavm) throws Exception {
		System.out.println(21);
		assert aavm != null;
		assert stateChanging != null;
		assert !stateChanging.isEmpty();
		
		Boolean finished = null;
		AllocatedApplicationVMStateChanging result = null;
		
		for ( AllocatedApplicationVMStateChanging aavmsc : stateChanging ) {
			System.out.println("IN STEPPING : " + aavmsc.getAllocatedApplicationVM().avmURI + " " + aavmsc.getCoreVariation());
			if ( aavmsc.getAllocatedApplicationVM().equals(aavm) ) {
				int cv = aavmsc.getCoreVariation();
				finished = aavmsc.step();
				if ( finished ) {
					result = aavmsc;
				}
				if (aavmsc.getCoreVariation() < cv) {
					increaseAllocatedCoreCount(aavm, 1);
				} else if (aavmsc.getCoreVariation() > cv) {
					decreaseAllocatedCoreCount(aavm, 1);
				}
			}
		}
		
		if ( finished == null )
			throw new Exception("step failure : AllocatedApplicationVM not found");
		if ( result != null )
			assert stateChanging.remove(result);
		
		System.out.println(22);
		return finished;
	}
	
	/**
	 * Retourne l'état courant de changement de la {@link AllocatedApplicationVM}
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @return vrai si la cible à fini sa transition (allocation ou libération complète de coeurs)
	 * @throws Exception
	 */
	
	public synchronized boolean isChanged(AllocatedApplicationVM aavm) throws Exception {
		System.out.println(23);
		assert aavm != null;
		assert stateChanging != null;
		assert !stateChanging.isEmpty();
		
		Boolean result = null;
		
		for ( AllocatedApplicationVMStateChanging aavmsc : stateChanging ) {
			if ( aavmsc.getAllocatedApplicationVM().equals(aavm) )
				result = aavmsc.isChanged();
		}
		
		assert result != null;
		System.out.println(24);
		return result; 
	}
	
	public synchronized boolean isChanging(AllocatedApplicationVM aavm) throws Exception {
		System.out.println(25);
		boolean result = false;
		for ( AllocatedApplicationVMStateChanging aavmsc : stateChanging ) {
			System.out.println("\tstateChanging\t" +aavmsc.getAllocatedApplicationVM().avmURI);
			System.out.println("\tstateChanging\t" +aavmsc.getCoreVariation());
			if ( aavmsc.getAllocatedApplicationVM().equals(aavm) )
				result = true;
		}
		System.out.println(26);
		return result;
	}
	
	public synchronized List<AllocatedApplicationVM> getChangingAllocatedApplicationVM() throws Exception {
		System.out.println(27);
		ArrayList<AllocatedApplicationVM> list = new ArrayList<>();
		
		for ( AllocatedApplicationVMStateChanging aavmsc : stateChanging ) {
			list.add(aavmsc.getAllocatedApplicationVM());
		}
		System.out.println(28);
		return list;
	}
	
	public synchronized List<AllocatedApplicationVM> getAllocatedApplicationVMs(AllocatedDispatcher adsp) {
		System.out.println(29);
		assert adsp != null;
		assert aavmsMap != null;
		System.out.println(30);
		return aavmsMap.get(adsp);
	}
	
	public synchronized List<AllocatedApplicationVM> getAllocatedApplicationVMs() {
		System.out.println(31);
		assert aavmsMap != null;
		assert aavmsMap.size() == 1;
		System.out.println(32);
		return aavmsMap.get(new ArrayList<>(aavmsMap.keySet()).get(0));
	}
	
	public synchronized Integer getAllocatedApplicationVMCoreCount(AllocatedApplicationVM aavm) {
		System.out.println(33);
		assert aavm != null;
		assert aavmsCoreCountMap.containsKey(aavm);
		System.out.println(34);
		return aavmsCoreCountMap.get(aavm);
	}
	
	public synchronized AllocatedDispatcher getSingleAllocatedDispatcher() {
		System.out.println(35);
		assert aavmsMap != null;
		assert aavmsMap.size() == 1;
		System.out.println(36);
		return new ArrayList<>(aavmsMap.keySet()).get(0);
	}
	
	public synchronized void removeAllocatedApplicationVM(AllocatedApplicationVM aavm) throws Exception {
		System.out.println(37);
		removeAllocatedApplicationVM(getSingleAllocatedDispatcher(), aavm);
		System.out.println(38);
	}

}
