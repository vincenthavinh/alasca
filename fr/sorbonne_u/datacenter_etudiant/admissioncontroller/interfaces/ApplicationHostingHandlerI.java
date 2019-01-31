package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces;

public interface ApplicationHostingHandlerI {

	//retourne la requestSubmissionInboundPortURI du request dispatcher, ou null si pas possible.
	public String processAskHosting(String requestNotificationInboundPortURI, int nbCores, int seuil_inf, int seuil_sup) throws Exception;
	
	//retourne true si la connection a bien ete faite, false sinon.
	public Boolean processAskHostToConnect(String requestNotificationInboundPortURI);
}
