package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingHandlerI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI;

/**
 * La classe <code>ApplicationHostingInboundPort</code> implémente un inbound
 * port qui offre l'interface <code>ApplicationHostingI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		owner instanceof ApplicationHostingHandlerI
 * </pre>
 * 
 * <p>Created on : February 1, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class ApplicationHostingInboundPort 
extends AbstractInboundPort 
implements ApplicationHostingI {

	private static final long serialVersionUID = 1L;
	
	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------
	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null and owner instanceof ApplicationHostingHandlerI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri			uri of the port.
	 * @param owner			owner component.
	 * @throws Exception		<i>todo.</i>
	 */
	public ApplicationHostingInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationHostingI.class, owner);
		assert	uri != null && owner instanceof ApplicationHostingHandlerI ;
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI#askHosting(String, int, int, int)
	 */
	@Override
	public String askHosting(String requestNotificationInboundPortURI, int nbCores, int seuil_inf, int seuil_sup) throws Exception {
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<String>() {
					@Override
					public String call() throws Exception {
						return ((ApplicationHostingHandlerI)this.getOwner()).
							processAskHosting(requestNotificationInboundPortURI, nbCores, seuil_inf, seuil_sup) ;
					}
				}) ;
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI#askHostToConnect(String)
	 */
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
