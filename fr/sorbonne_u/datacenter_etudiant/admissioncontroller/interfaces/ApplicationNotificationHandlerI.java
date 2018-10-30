package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces;

public interface ApplicationNotificationHandlerI {
	
	public void	acceptApplicationReadyNotification() throws Exception ;
	
	public void acceptApplicationRejectedNotification() throws Exception ;
}
