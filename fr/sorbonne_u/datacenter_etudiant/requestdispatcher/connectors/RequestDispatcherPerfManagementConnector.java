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
	public String removeAVM() throws Exception {
		return ((RequestDispatcherPerfManagementI)this.offering).removeAVM();
	}

	@Override
	public long getAverageReqDuration() throws Exception {
		return ((RequestDispatcherPerfManagementI)this.offering).getAverageReqDuration();
	}
}