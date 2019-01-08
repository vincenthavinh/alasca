package fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherPerfManagementI;

public class RequestDispatcherPerfManagementOutboundPort 
extends		AbstractOutboundPort
implements	RequestDispatcherManagementI{

	private static final long serialVersionUID = 1L;

	public RequestDispatcherPerfManagementOutboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherPerfManagementI.class, owner);
		assert	owner instanceof RequestDispatcherPerfManagementI ;
	}
	
	public RequestDispatcherPerfManagementOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestDispatcherPerfManagementI.class, owner);
		assert	uri != null && owner instanceof RequestDispatcherPerfManagementI ;
	}

	@Override
	public void connectOutboundPorts() throws Exception {
		((RequestDispatcherPerfManagementI)this.connector).connectOutboundPorts();
	}

	@Override
	public void toggleTracingLogging() throws Exception {
		((RequestDispatcherPerfManagementI)this.connector).toggleTracingLogging();
	}
}
