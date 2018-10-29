package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationSubmissionHandlerI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationSubmissionI;

public class ApplicationSubmissionInboundPort 
extends AbstractInboundPort 
implements ApplicationSubmissionI {

	private static final long serialVersionUID = 1L;

	public ApplicationSubmissionInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationSubmissionI.class, owner);
		assert	uri != null && owner instanceof ApplicationSubmissionHandlerI ;
	}
	
	@Override
	public void submitApplicationAndNotify() throws Exception {
		this.getOwner().handleRequestAsync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((ApplicationSubmissionHandlerI)this.getOwner()).
								acceptApplicationSubmissionAndNotify() ;
							return null ;
						}
					}) ;
	}

}
