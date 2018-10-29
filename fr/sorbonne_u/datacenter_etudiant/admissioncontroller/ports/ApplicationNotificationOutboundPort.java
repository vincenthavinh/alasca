package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationNotificationI;

public class ApplicationNotificationOutboundPort 
extends AbstractOutboundPort 
implements ApplicationNotificationI {

	private static final long serialVersionUID = 1L;

	public ApplicationNotificationOutboundPort(ComponentI owner) throws Exception {
		super(ApplicationNotificationI.class, owner) ;
	}
	
	public ApplicationNotificationOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationNotificationI.class, owner) ;
		assert	uri != null ;
	}

	@Override
	public void notifyApplicationReady() throws Exception {
		((ApplicationNotificationI)this.connector).notifyApplicationReady() ;		
	}

}
