package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI;

public class ApplicationHostingOutboundPort 
extends AbstractOutboundPort 
implements ApplicationHostingI {

	private static final long serialVersionUID = 1L;

	public	ApplicationHostingOutboundPort(ComponentI owner) throws Exception{
		super(ApplicationHostingI.class, owner) ;
		assert owner != null;
	}
	
	public	ApplicationHostingOutboundPort(String uri,ComponentI owner) throws Exception{
		super(uri, ApplicationHostingI.class, owner) ;
		assert	uri != null ;
	}
	
	@Override
	public String askHosting(String requestNotificationInboundPortURI, int nbCores, int seuil_inf, int seuil_sup) throws Exception {
		return ((ApplicationHostingI)this.connector).askHosting(requestNotificationInboundPortURI, nbCores, seuil_inf, seuil_sup) ;		
	}

	@Override
	public Boolean askHostToConnect(String requestNotificationInboundPortURI) throws Exception {
		return ((ApplicationHostingI)this.connector).askHostToConnect(requestNotificationInboundPortURI);
	}

}
