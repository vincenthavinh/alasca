package fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI;

/**
 * La classe <code>RequestDispatcherManagementInboundPort</code> implémente le
 * inbound port offert par l'interface <code>ApplicationVMManagementI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		owner instanceof RequestDispatcherManagementI
 * </pre>
 * 
 * <p>Created on : February 1, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class RequestDispatcherManagementInboundPort 
extends		AbstractInboundPort
implements	RequestDispatcherManagementI {

	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner instanceof RequestDispatcherManagementI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param owner			owner component.
	 * @throws Exception		<i>todo.</i>
	 */
	public RequestDispatcherManagementInboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherManagementI.class, owner);
	}
	
	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null and owner instanceof RequestDispatcherManagementI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri			uri of the port.
	 * @param owner			owner component.
	 * @throws Exception		<i>todo.</i>
	 */
	public RequestDispatcherManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestDispatcherManagementI.class, owner);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#connectOutboundPorts()
	 */
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

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#toggleTracingLogging()
	 */
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

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#addAVM(String)
	 */
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
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#removeAVM(String)
	 */
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
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#getAverageReqDuration()
	 */
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
}
