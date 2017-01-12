package fr.upmc.datacenter.software.controllers.performance.interfaces;

import fr.upmc.datacenter.software.controllers.performance.AllocatedDispatcher;
import fr.upmc.datacenter.software.controllers.performance.AllocatedRequestGenerator;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

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
	
}
