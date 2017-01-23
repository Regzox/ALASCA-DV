package fr.upmc.datacenter.software.controllers.performance.interfaces;

import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.software.controllers.performance.AllocatedDispatcher;
import fr.upmc.datacenter.software.controllers.performance.AllocatedRequestGenerator;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

/**
 * Interface des op�rations commune du contr�leur de performances
 * 
 * @author Daniel RADEAU
 *
 */

public interface PerformanceControllerI {
	
	/**
	 * Cr�ation d'un g�n�rateur de r�qu�tes allou�*.
	 * 
	 * @return
	 * @throws Exception
	 */
	
	AllocatedRequestGenerator createAllocatedRequestGenerator() throws Exception;
	
	/**
	 * Cr�ation d'un g�n�rateur de requ�tes.
	 * 
	 * @return
	 * @throws Exception
	 */
	
	RequestGenerator createRequestGenerator(AllocatedRequestGenerator arg) throws Exception;
	
	/**
	 * Cr�ation d'un r�partiteur de r�qu�tes allou�*.
	 * 
	 * @return
	 * @throws Exception
	 */
	
	AllocatedDispatcher createAllocatedDispatcher() throws Exception;
	
	/**
	 * Cr�ation d'un r�partiteur de requ�tes.
	 * 
	 * @return
	 * @throws Exception
	 */
	
	Dispatcher createDispatcher(AllocatedDispatcher adsp) throws Exception;
	
	/**
	 * Loi de contr�le.
	 * 
	 * @throws Exception
	 */
	
	void controlLaw() throws Exception;
	
	/**
	 * R�alise l'op�ration de d'augmentation de la fr�quence de l'AVM bien que distante.
	 * 
	 * @param aavm
	 */
	
	void performIncreaseApplicationVMFrequency(AllocatedApplicationVM aavm)  throws Exception;
	
	/**
	 * R�alise l'op�ration de d'diminution de la fr�quence de l'AVM bien que distante.
	 * 
	 * @param aavm
	 */
	
	void performDecreaseApplicationVMFrequency(AllocatedApplicationVM aavm) throws Exception;
	
	/**
	 * R�alise l'op�ration de d'augmentation du nombre de coeurs de l'AVM bien que distante.
	 * 
	 * @param aavm
	 * @param wantedCores
	 */
	
	void performIncreaseApplicationVMCores(AllocatedApplicationVM aavm, int wantedCores) throws Exception;
	
	/**
	 * R�alise l'op�ration de d'diminution du nombre de coeurs de l'AVM bien que distante.
	 * 
	 * @param aavm
	 * @param wantedCores
	 */
	
	void performDecreaseApplicationVMCores(AllocatedApplicationVM aavm, int wantedCores) throws Exception;
	
	/**
	 * R�alise l'op�ration de d'augmentation du nombre d'AVM bien que distante.
	 * 
	 * @param wantedAavms
	 */
	
	void performAllocateApplicationVM(int wantedAavms) throws Exception;
	
	/**
	 * R�alise l'op�ration de diminution du nombre d'AVM bien que distante.
	 * 
	 * @param aavms
	 */
	
	void performReleaseApplicationVM(Integer wantedCores) throws Exception;
	
}
