package fr.upmc.datacenter.software.controllers.performance.interfaces;

import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.software.controllers.performance.AllocatedDispatcher;
import fr.upmc.datacenter.software.controllers.performance.AllocatedRequestGenerator;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

public interface PerformanceControllerI {
	
	/**
	 * Création d'un générateur de réquêtes alloué*.
	 * 
	 * @return
	 * @throws Exception
	 */
	
	AllocatedRequestGenerator createAllocatedRequestGenerator() throws Exception;
	
	/**
	 * Création d'un générateur de requêtes.
	 * 
	 * @return
	 * @throws Exception
	 */
	
	RequestGenerator createRequestGenerator(AllocatedRequestGenerator arg) throws Exception;
	
	/**
	 * Création d'un répartiteur de réquêtes alloué*.
	 * 
	 * @return
	 * @throws Exception
	 */
	
	AllocatedDispatcher createAllocatedDispatcher() throws Exception;
	
	/**
	 * Création d'un répartiteur de requêtes.
	 * 
	 * @return
	 * @throws Exception
	 */
	
	Dispatcher createDispatcher(AllocatedDispatcher adsp) throws Exception;
	
	/**
	 * Loi de contrôle.
	 * 
	 * @throws Exception
	 */
	
	void controlLaw() throws Exception;
	
	/**
	 * Réalise l'opération de d'augmentation de la fréquence de l'AVM bien que distante.
	 * 
	 * @param aavm
	 */
	
	void performIncreaseApplicationVMFrequency(AllocatedApplicationVM aavm)  throws Exception;
	
	/**
	 * Réalise l'opération de d'diminution de la fréquence de l'AVM bien que distante.
	 * 
	 * @param aavm
	 */
	
	void performDecreaseApplicationVMFrequency(AllocatedApplicationVM aavm) throws Exception;
	
	/**
	 * Réalise l'opération de d'augmentation du nombre de coeurs de l'AVM bien que distante.
	 * 
	 * @param aavm
	 * @param wantedCores
	 */
	
	void performIncreaseApplicationVMCores(AllocatedApplicationVM aavm, int wantedCores) throws Exception;
	
	/**
	 * Réalise l'opération de d'diminution du nombre de coeurs de l'AVM bien que distante.
	 * 
	 * @param aavm
	 * @param wantedCores
	 */
	
	void performDecreaseApplicationVMCores(AllocatedApplicationVM aavm, int wantedCores) throws Exception;
	
	/**
	 * Réalise l'opération de d'augmentation du nombre d'AVM bien que distante.
	 * 
	 * @param wantedAavms
	 */
	
	void performAllocateApplicationVM(int wantedAavms) throws Exception;
	
	/**
	 * Réalise l'opération de diminution du nombre d'AVM bien que distante.
	 * 
	 * @param aavms
	 */
	
	void performReleaseApplicationVM(Integer wantedCores) throws Exception;
	
}
