package fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.RequestDispatcherPerf;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherPerfManagementI;

public class RequestDispatcherPerfManagementInboundPort 
extends		AbstractInboundPort
implements	RequestDispatcherPerfManagementI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RequestDispatcherPerfManagementInboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherPerfManagementI.class, owner);
	}
	
	public RequestDispatcherPerfManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestDispatcherPerfManagementI.class, owner);
	}

	
	@Override
	public void connectOutboundPorts() throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((RequestDispatcherPerf)this.getOwner()).
								connectOutboundPorts();
						return null;
					}
				}) ;
	}

	@Override
	public void toggleTracingLogging() throws Exception {
		this.getOwner().handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((RequestDispatcherPerf)this.getOwner()).
								toggleTracingLogging();
						return null;
					}
				}) ;
	}
}
