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
	}
	
	public	ApplicationHostingOutboundPort(String uri,ComponentI owner) throws Exception{
		super(uri, ApplicationHostingI.class, owner) ;
		assert	uri != null ;
	}
	
	@Override
	public String askHosting(String requestNotificationInboundPortURI) throws Exception {
		return ((ApplicationHostingI)this.connector).askHosting(requestNotificationInboundPortURI) ;		
	}

	@Override
	public Boolean askHostToConnect() throws Exception {
		return ((ApplicationHostingI)this.connector).askHostToConnect();
	}

}
