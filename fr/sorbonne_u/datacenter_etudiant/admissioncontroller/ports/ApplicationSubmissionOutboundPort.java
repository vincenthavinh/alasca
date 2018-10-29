package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationSubmissionI;

public class ApplicationSubmissionOutboundPort 
extends AbstractOutboundPort 
implements ApplicationSubmissionI {

	private static final long serialVersionUID = 1L;

	public	ApplicationSubmissionOutboundPort(ComponentI owner) throws Exception{
		super(ApplicationSubmissionI.class, owner) ;
	}
	
	public	ApplicationSubmissionOutboundPort(String uri,ComponentI owner) throws Exception{
		super(uri, ApplicationSubmissionI.class, owner) ;
		assert	uri != null ;
	}
	
	@Override
	public void submitApplicationAndNotify() throws Exception {
		((ApplicationSubmissionI)this.connector).submitApplicationAndNotify() ;		
	}

}
