package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI;

public class ApplicationHostingConnector 
extends AbstractConnector 
implements ApplicationHostingI {

	@Override
	public String askHosting(String requestNotificationInboundPortURI, int nbCores, int seuil_inf, int seuil_sup) throws Exception {
		return ((ApplicationHostingI)this.offering).askHosting(requestNotificationInboundPortURI, nbCores, seuil_inf, seuil_sup) ;
	}

	@Override
	public Boolean askHostToConnect(String requestNotificationInboundPortURI) throws Exception {
		return ((ApplicationHostingI)this.offering).askHostToConnect(requestNotificationInboundPortURI) ;		
	}

}
