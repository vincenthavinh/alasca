package fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI;

/**
 * La classe <code>RequestDispatcherManagementOutboundPort</code> implémente le
 * inbound port requis par l'interface <code>ApplicationVMManagementI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : February 1, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class RequestDispatcherManagementOutboundPort 
extends		AbstractOutboundPort
implements	RequestDispatcherManagementI{

	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------
	public RequestDispatcherManagementOutboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherManagementI.class, owner);
		assert	owner instanceof RequestDispatcherManagementI ;
	}
	
	public RequestDispatcherManagementOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestDispatcherManagementI.class, owner);
		assert	uri != null && owner instanceof RequestDispatcherManagementI ;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#connectOutboundPorts()
	 */
	@Override
	public void connectOutboundPorts() throws Exception {
		((RequestDispatcherManagementI)this.connector).connectOutboundPorts();
	}

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#toggleTracingLogging()
	 */
	@Override
	public void toggleTracingLogging() throws Exception {
		((RequestDispatcherManagementI)this.connector).toggleTracingLogging();
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#addAVM(String)
	 */
	@Override
	public void addAVM(String reqSubURI) throws Exception {
		((RequestDispatcherManagementI)this.connector).addAVM(reqSubURI);
	}

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#removeAVM(String)
	 */
	@Override
	public void removeAVM(String avm_rsipURI) throws Exception {
		((RequestDispatcherManagementI)this.connector).removeAVM(avm_rsipURI);
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#getAverageReqDuration()
	 */
	@Override
	public long getAverageReqDuration() throws Exception {
		return ((RequestDispatcherManagementI)this.connector).getAverageReqDuration();
	}
}
