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
	void submitApplication(Class<?> inter) throws Exception;
	
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
}
