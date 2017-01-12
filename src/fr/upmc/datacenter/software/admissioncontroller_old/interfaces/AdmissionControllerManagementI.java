package fr.upmc.datacenter.software.admissioncontroller_old.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.external.software.applications.AbstractApplication;

/**
 * Interface de gestion du contr�leur d'admissions.
 * Synth�tise les m�thodes pouvant avoir une action sur le fonctionnement du contr�leur d'admissions.
 * 
 * @author Daniel RADEAU
 *
 */

public interface AdmissionControllerManagementI
	extends		
		OfferedI,
		RequiredI
{


	/**
	 * 
	 * <h4>Connect the admission controller to a computer</h4>
	 * <p>
	 * URIS passed by parameters are inbounds port where the admission controller have to plug his out bound ports
	 * </p>
	 * 
	 * <p>
	 * List of connections established : <br/><br/>
	 * <b>[AdmissionController]</b>-<em>(ComputerServicesOutboundPort)</em>-> ... >-<em>(ComputerServicesInboundPort)</em>-<b>[Computer]</b><br/>
	 * <b>[AdmissionController]</b>-<em>(ComputerStaticStateOutboundPort)</em>-> ... >-<em>(ComputerStaticStateInboundPort)</em>-<b>[Computer]</b><br/>
	 * <b>[AdmissionController]</b>-<em>(ComputerDynamicStateOutboundPort)</em>-> ... >-<em>(ComputerDynamicStateInboundPort)</em>-<b>[Computer]</b><br/>
	 * </p>
	 * 
	 * @param computerURI : Computer URI
	 * @param csipURI : ComputerServicesInboundPort URI
	 * @param cssdipURI : ComputerStaticStateDataInboundPort URI
	 * @param cdsdipURI : ComputerDynamicStateDataInboundPort URI
	 * @throws Exception
	 */
	
	void connectToComputer(
			final String computerURI, 
			final String csipURI,
			final String cssdipURI,
			final String cdsdipURI,
			final String ccripURI) throws Exception;
	
	/**
	 * Soumission d'une application simul�e par une chaine {@link RequestGenerator}, {@link Dispatcher} et {@link ApplicationVM}
	 * On y alloue un processeur par pour une {@link ApplicationVM} depuis le premier {@link Computer} disponible.
	 */
	
	String submitApplication() throws Exception;
	
	
	/**
	 * Soumission d'une application par le biais de son interface de soumission
	 * de requ�tes.
	 * 
	 * @param inter
	 */
	void submitApplication(
			AbstractApplication application, 
			Class<?> submissionInterface) throws Exception;
	
	/**
	 * Oblige tous les r�partiteurs de requ�tes � ajouter une nouvelle AVM � leur boucle de traitement.
	 * Les AVM cr��es sont allou�es dynamiquement par le contr�leur d'admission.
	 * Si jamais le contr�leur d'admission ne poss�de pas les ressources n�c�ssaires � l'incr�mentation
	 * tous les r�partiteurs de requ�te une exception est lev�e mais les AVM d�j� affect�es par 
	 * l'incr�mentation ne sont pas d�sallou�es.
	 * 
	 * Version actuelle : Un processeur est allou� � une AVM
	 * 
	 * @throws Exception
	 */
	void forceApplicationVMIncrementation() throws Exception;
	
	/**
	 * Stop l'emission de donn�es dynamiques depuis les fournisseurs
	 */
	
	void stopDynamicStateDataPushing() throws Exception;
	
	/**
	 * Programme une �mission de donn�e dynamiques p�riodique en millisecondes.
	 * Stop le pushing existant pour red�finir l'intervalle d'�mission par la suite.
	 * @param milliseconds
	 */
	
	void startDynamicStateDataPushing(int milliseconds) throws Exception;

	/**
	 * Augmente la fr�quence du coeur allou� pass� en param�tre 
	 * 
	 * @param allocatedCore
	 * @return
	 */
	
	void increaseCoreFrequency(String computerURI, String processorURI, Integer coreNo) throws Exception;
	
	/**
	 * Diminue la fr�quence du coeur allou� pass� en param�tre
	 * 
	 * @param allocatedCore
	 * @return
	 */
	
	void decreaseCoreFrequency(String computerURI, String processorURI, Integer coreNo) throws Exception;
	
	/**
	 * Augmente la fr�quence d'un processeur 
	 * 
	 * @param computerURI
	 * @param processorURI
	 * @return
	 * @throws Exception
	 */
	
	void increaseProcessorFrenquency(String computerURI, String processorURI) throws Exception;
	
	void decreaseProcessorFrenquency(String computerURI, String processorURI) throws Exception;
	
	void increaseProcessorsFrenquencies(String computerURI) throws Exception;
	
	void decreaseProcessorsFrenquencies(String computerURI) throws Exception;
	
	void allocateCores(String computerURI, String avmURI, int cores) throws Exception;
	
	void releaseCores(String computerURI, String avmURI, int cores) throws Exception;
	
	void increaseAVMs(String dispatcherURI) throws Exception;
	
	void decreaseAVMs(String dispatcherURI) throws Exception;
	
	
}
