package fr.sorbonne_u.datacenter.software.applicationvm;

import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;

public class ApplicationVMDynamicState 
extends		AbstractTimeStampedData
implements ApplicationVMDynamicStateI{
	// ------------------------------------------------------------------------
	// Instance variables and constants
	// ------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	/** uri de l'applicationVM */
	protected final String applicationVMURI;
	/** l'état de l'applicationVM */
	protected final boolean	idleStatus ;
	
	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create a snapshot of the dynamic state of a applicationVM component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	applicationVMURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param applicationVMURI		execution status of the processor cores.
	 * @param idleStatus	current frequencies of the processor cores.
	 */
	public ApplicationVMDynamicState(String applicationVMURI, boolean idleStatus) {
		super() ;

		assert	applicationVMURI != null;
		
		this.applicationVMURI = applicationVMURI;
		this.idleStatus = idleStatus;
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI#getApplicationVMURI()
	 */
	@Override
	public String getApplicationVMURI() {
		return applicationVMURI;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI#isIdle()
	 */
	@Override
	public boolean isIdle() {
		return idleStatus;
	}

}
