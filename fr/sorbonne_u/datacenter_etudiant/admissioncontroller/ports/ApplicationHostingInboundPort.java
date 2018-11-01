package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingHandlerI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI;

public class ApplicationHostingInboundPort 
extends AbstractInboundPort 
implements ApplicationHostingI {

	private static final long serialVersionUID = 1L;

	public ApplicationHostingInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationHostingI.class, owner);
		assert	uri != null && owner instanceof ApplicationHostingHandlerI ;
	}
	
	@Override
	public String askHosting(String requestNotificationInboundPortURI) throws Exception {
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<String>() {
					@Override
					public String call() throws Exception {
						return ((ApplicationHostingHandlerI)this.getOwner()).
							processAskHosting(requestNotificationInboundPortURI) ;
					}
				}) ;
	}

	@Override
	public Boolean askHostToConnect(String requestNotificationInboundPortURI) throws Exception {
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ((ApplicationHostingHandlerI)this.getOwner()).
								processAskHostToConnect(requestNotificationInboundPortURI) ;
					}
				}) ;
	}

}
