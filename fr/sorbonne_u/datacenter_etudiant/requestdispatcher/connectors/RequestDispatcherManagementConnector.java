package fr.sorbonne_u.datacenter_etudiant.requestdispatcher.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI;

/**
 * La classe <code>RequestDispatcherManagementConnector</code> implémente un
 * connecteur pour l'échange des ports à travers l'interface
 * <code>ApplicationVMManagementI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : February 1, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class RequestDispatcherManagementConnector 
extends		AbstractConnector
implements	RequestDispatcherManagementI {

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#connectOutboundPorts()
	 */
	@Override
	public void connectOutboundPorts() throws Exception {
		((RequestDispatcherManagementI)this.offering).connectOutboundPorts();
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#toggleTracingLogging()
	 */
	@Override
	public void toggleTracingLogging() throws Exception {
		((RequestDispatcherManagementI)this.offering).toggleTracingLogging();
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#addAVM(String)
	 */
	@Override
	public void addAVM(String reqSubURI) throws Exception {
		((RequestDispatcherManagementI)this.offering).addAVM(reqSubURI);
	}

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#removeAVM(String)
	 */
	@Override
	public void removeAVM(String avm_rsipURI) throws Exception {
		((RequestDispatcherManagementI)this.offering).removeAVM(avm_rsipURI);
	}

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#getAverageReqDuration()
	 */
	@Override
	public long getAverageReqDuration() throws Exception {
		return ((RequestDispatcherManagementI)this.offering).getAverageReqDuration();
	}
}
