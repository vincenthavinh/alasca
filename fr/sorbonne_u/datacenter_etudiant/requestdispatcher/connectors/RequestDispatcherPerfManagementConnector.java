package fr.sorbonne_u.datacenter_etudiant.requestdispatcher.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherPerfManagementI;

public class RequestDispatcherPerfManagementConnector 
extends		AbstractConnector
implements	RequestDispatcherPerfManagementI {

	@Override
	public void connectOutboundPorts() throws Exception {
		((RequestDispatcherPerfManagementI)this.offering).connectOutboundPorts();
	}

	@Override
	public void toggleTracingLogging() throws Exception {
		((RequestDispatcherPerfManagementI)this.offering).toggleTracingLogging();
	}

	@Override
	public void addAVM(String reqSubURI) throws Exception {
		((RequestDispatcherPerfManagementI)this.offering).addAVM(reqSubURI);
	}

	@Override
	public void removeAVM(String avm_rsipURI) throws Exception {
		((RequestDispatcherPerfManagementI)this.offering).removeAVM(avm_rsipURI);
	}

	@Override
	public long getAverageReqDuration() throws Exception {
		return ((RequestDispatcherPerfManagementI)this.offering).getAverageReqDuration();
	}
}
