package fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementInboundPort 
extends		AbstractInboundPort
implements	RequestDispatcherManagementI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RequestDispatcherManagementInboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherManagementI.class, owner);
	}
	
	public RequestDispatcherManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestDispatcherManagementI.class, owner);
	}

	
	@Override
	public void connectOutboundPorts() throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((RequestDispatcher)this.getOwner()).
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
						((RequestDispatcher)this.getOwner()).
								toggleTracingLogging();
						return null;
					}
				}) ;
	}

	@Override
	public long getAverageReqDuration() throws Exception {
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Long>() {
					@Override
					public Long call() throws Exception {
						return ((RequestDispatcher)this.getOwner()).getAverageReqDuration();
					}
				}) ;
	}
	
	@Override
	public void addAVM(String reqSubURI) throws Exception {
		this.getOwner().handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((RequestDispatcher)this.getOwner()).
								addAVM(reqSubURI);
						return null;
					}
				}) ;
	}

	@Override
	public void removeAVM(String avm_rsipURI) throws Exception {
		this.getOwner().handleRequestSync(
			new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() throws Exception {
					((RequestDispatcher)this.getOwner()).
									removeAVM(avm_rsipURI) ;
					return null;
				}
			}) ;
	}
}
