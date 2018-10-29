package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationNotificationHandlerI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationNotificationI;

public class ApplicationNotificationInboundPort 
extends AbstractInboundPort 
implements ApplicationNotificationI {

	private static final long serialVersionUID = 1L;

	public ApplicationNotificationInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ApplicationNotificationI.class , owner);
		assert	uri != null && owner instanceof ApplicationNotificationHandlerI ;
	}

	
	@Override
	public void	notifyApplicationReady() throws Exception {
		this.getOwner().handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((ApplicationNotificationHandlerI)this.getOwner()).
							acceptApplicationReadyNotification() ;
						return null;
					}
				}) ;
	}

}
