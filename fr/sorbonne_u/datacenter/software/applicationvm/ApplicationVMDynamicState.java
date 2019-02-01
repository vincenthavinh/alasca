package fr.sorbonne_u.datacenter.software.applicationvm;

import java.util.Set;

import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
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
	/** uri de l'ordinateur sur lequel l'AVM est hébergé */
	protected final String computerURI;
	/** l'état de l'applicationVM */
	protected final Set<AllocatedCore>	coresStatus ;
	
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
	 * @param applicationVMURI		uri of avm.
	 * @param idleStatus	avm idle or not
	 */
	public ApplicationVMDynamicState(String applicationVMURI, String computerURI, Set<AllocatedCore> coresStatus) {
		super() ;

		assert	applicationVMURI != null;
		this.computerURI = computerURI;
		this.applicationVMURI = applicationVMURI;
		this.coresStatus = coresStatus;
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI#getApplicationVMURI()
	 */
	@Override
	public String getApplicationVMURI() {
		return applicationVMURI;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI#getCoresStatus()
	 */
	@Override
	public Set<AllocatedCore> getCoresStatus() {
		return coresStatus;
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI#getComputerURI()
	 */
	@Override
	public String getComputerURI() {
		return computerURI;
	}

}
