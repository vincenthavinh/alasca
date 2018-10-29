package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationNotificationI;

public class ApplicationNotificationConnector 
extends AbstractConnector 
implements ApplicationNotificationI {

	@Override
	public void notifyApplicationReady() throws Exception {
		((ApplicationNotificationI)this.offering).notifyApplicationReady() ;		
	}

}
