package fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;

public class RequestDispatcherManagementInboundPort 
extends		AbstractInboundPort
implements	RequestDispatcherManagementI {

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
}