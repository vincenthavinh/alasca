package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ApplicationHostingI 
extends OfferedI, 
		RequiredI{

	/**
	 * Demande l'hebergement d'une application.
	 * @param requestNotificationInboundPortURI
	 * @return l'URI du RequestSubmissionInboundPort auquel se connecter.
	 * @throws Exception
	 */
	public String askHosting(String requestNotificationInboundPortURI) throws Exception;

	/**
	 * Demande a l'Host de l'application de connecter ses OutboundPorts.
	 * @param requestNotificationInboundPortURI TODO
	 * @return true si les ports de l'Host sont bien connectes, false sinon.
	 * @throws Exception
	 */
	public Boolean askHostToConnect(String requestNotificationInboundPortURI) throws Exception ;
}
