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

	@Override
	public void addAVM(String reqSubURI) throws Exception {
		((RequestDispatcherManagementI)this.offering).addAVM(reqSubURI);
	}

	@Override
	public void removeAVM(String avm_rsipURI) throws Exception {
		((RequestDispatcherManagementI)this.offering).removeAVM(avm_rsipURI);
	}

	@Override
	public long getAverageReqDuration() throws Exception {
		return ((RequestDispatcherManagementI)this.offering).getAverageReqDuration();
	}
}
