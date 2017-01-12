package fr.upmc.datacenter.software.controllers.performance.interfaces;

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
	
}
