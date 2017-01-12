package fr.upmc.datacenter.software.admissioncontroller_old.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.external.software.applications.AbstractApplication;

/**
 * Interface de gestion du contrôleur d'admissions.
 * Synthétise les méthodes pouvant avoir une action sur le fonctionnement du contrôleur d'admissions.
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
	 * Soumission d'une application simulée par une chaine {@link RequestGenerator}, {@link Dispatcher} et {@link ApplicationVM}
	 * On y alloue un processeur par pour une {@link ApplicationVM} depuis le premier {@link Computer} disponible.
	 */
	
	String submitApplication() throws Exception;
	
	
	/**
	 * Soumission d'une application par le biais de son interface de soumission
	 * de requêtes.
	 * 
	 * @param inter
	 */
	void submitApplication(
			AbstractApplication application, 
			Class<?> submissionInterface) throws Exception;
	
	/**
	 * Oblige tous les répartiteurs de requêtes à ajouter une nouvelle AVM à leur boucle de traitement.
	 * Les AVM créées sont allouées dynamiquement par le contrôleur d'admission.
	 * Si jamais le contrôleur d'admission ne possède pas les ressources nécéssaires à l'incrémentation
	 * tous les répartiteurs de requête une exception est levée mais les AVM déjà affectées par 
	 * l'incrémentation ne sont pas désallouées.
	 * 
	 * Version actuelle : Un processeur est alloué à une AVM
	 * 
	 * @throws Exception
	 */
	void forceApplicationVMIncrementation() throws Exception;
	
	/**
	 * Stop l'emission de données dynamiques depuis les fournisseurs
	 */
	
	void stopDynamicStateDataPushing() throws Exception;
	
	/**
	 * Programme une émission de donnée dynamiques périodique en millisecondes.
	 * Stop le pushing existant pour redéfinir l'intervalle d'émission par la suite.
	 * @param milliseconds
	 */
	
	void startDynamicStateDataPushing(int milliseconds) throws Exception;

	/**
	 * Augmente la fréquence du coeur alloué passé en paramètre 
	 * 
	 * @param allocatedCore
	 * @return
	 */
	
	void increaseCoreFrequency(String computerURI, String processorURI, Integer coreNo) throws Exception;
	
	/**
	 * Diminue la fréquence du coeur alloué passé en paramètre
	 * 
	 * @param allocatedCore
	 * @return
	 */
	
	void decreaseCoreFrequency(String computerURI, String processorURI, Integer coreNo) throws Exception;
	
	/**
	 * Augmente la fréquence d'un processeur 
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
