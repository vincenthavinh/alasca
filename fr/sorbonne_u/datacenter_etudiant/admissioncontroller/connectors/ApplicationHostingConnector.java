package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI;

/**
 * La classe <code>ApplicationHostingConnector</code> implémente un connecteur pour
 * l'échange des ports à travers l'interface <code>ApplicationHostingI</code>.
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
public class ApplicationHostingConnector 
extends AbstractConnector 
implements ApplicationHostingI {

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI#askHosting(String, int, int, int)
	 */
	@Override
	public String askHosting(String requestNotificationInboundPortURI, int nbCores, int seuil_inf, int seuil_sup) throws Exception {
		return ((ApplicationHostingI)this.offering).askHosting(requestNotificationInboundPortURI, nbCores, seuil_inf, seuil_sup) ;
	}

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI#askHostToConnect(String)
	 */
	@Override
	public Boolean askHostToConnect(String requestNotificationInboundPortURI) throws Exception {
		return ((ApplicationHostingI)this.offering).askHostToConnect(requestNotificationInboundPortURI) ;		
	}

}
