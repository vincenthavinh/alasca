package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI;

/**
 * La classe <code>ApplicationHostingOutboundPort</code> implément un outbound
 * port qui requiert l'interface <code>ApplicationHostingI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : February 1, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class ApplicationHostingOutboundPort 
extends AbstractOutboundPort 
implements ApplicationHostingI {

	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public	ApplicationHostingOutboundPort(ComponentI owner) throws Exception{
		super(ApplicationHostingI.class, owner) ;
		assert owner != null;
	}
	
	public	ApplicationHostingOutboundPort(String uri,ComponentI owner) throws Exception{
		super(uri, ApplicationHostingI.class, owner) ;
		assert	uri != null ;
	}
	
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI#askHosting(String, int, int, int)
	 */
	@Override
	public String askHosting(String requestNotificationInboundPortURI, int nbCores, int seuil_inf, int seuil_sup) throws Exception {
		return ((ApplicationHostingI)this.connector).askHosting(requestNotificationInboundPortURI, nbCores, seuil_inf, seuil_sup) ;		
	}

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI#askHostToConnect(String)
	 */
	@Override
	public Boolean askHostToConnect(String requestNotificationInboundPortURI) throws Exception {
		return ((ApplicationHostingI)this.connector).askHostToConnect(requestNotificationInboundPortURI);
	}

}
