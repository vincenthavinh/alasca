package fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementOutboundPort 
extends		AbstractOutboundPort
implements	RequestDispatcherManagementI{

	private static final long serialVersionUID = 1L;

	public RequestDispatcherManagementOutboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherManagementI.class, owner);
		assert	owner instanceof RequestDispatcherManagementI ;
	}
	
	public RequestDispatcherManagementOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestDispatcherManagementI.class, owner);
		assert	uri != null && owner instanceof RequestDispatcherManagementI ;
	}

	@Override
	public void connectOutboundPorts() throws Exception {
		((RequestDispatcherManagementI)this.connector).connectOutboundPorts();
	}

	@Override
	public void toggleTracingLogging() throws Exception {
		((RequestDispatcherManagementI)this.connector).toggleTracingLogging();
	}

	public void addAVM(String reqSubURI) throws Exception {
		((RequestDispatcherManagementI)this.connector).addAVM(reqSubURI);
	}

	@Override
	public void removeAVM(String avm_rsipURI) throws Exception {
		((RequestDispatcherManagementI)this.connector).removeAVM(avm_rsipURI);
	}
	
	@Override
	public long getAverageReqDuration() throws Exception {
		return ((RequestDispatcherManagementI)this.connector).getAverageReqDuration();
	}
}
