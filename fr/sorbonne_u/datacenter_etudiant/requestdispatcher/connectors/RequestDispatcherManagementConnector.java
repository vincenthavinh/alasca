package fr.sorbonne_u.datacenter_etudiant.requestdispatcher.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementConnector 
extends		AbstractConnector
implements	RequestDispatcherManagementI {

	@Override
	public void connectOutboundPorts() throws Exception {
		((RequestDispatcherManagementI)this.offering).connectOutboundPorts();
	}

	@Override
	public void toggleTracingLogging() throws Exception {
		((RequestDispatcherManagementI)this.offering).toggleTracingLogging();
	}

}
