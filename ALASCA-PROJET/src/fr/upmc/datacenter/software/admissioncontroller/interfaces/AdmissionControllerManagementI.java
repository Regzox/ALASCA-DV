package fr.upmc.datacenter.software.admissioncontroller.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

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
			final String cdsdipURI) throws Exception;
	
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
	void submitApplication(Class<?> inter) throws Exception;
	
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
}
