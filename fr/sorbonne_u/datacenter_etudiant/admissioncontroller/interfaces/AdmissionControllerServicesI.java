package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces;

import java.util.Map;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM.ApplicationVMPortTypes;

/**
 * The interface <code>AdmissionControllerServiceI</code> defines the services offered by
 * <code>Admission Controller</code> components (allocating and releasing free avms).
 *
 * <p><strong>Description</strong></p>
 * 
 * Allocate or recycle free AVM from performance controller
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : January 16, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public interface			AdmissionControllerServicesI
extends		OfferedI,
			RequiredI
{
	/**
	 * allocate one free AVM to the performance controller and return the inbound port
	 * uri of the AVM allocated; return null if no free AVM is available.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return		uri of ports of the allocated AVM
	 * @throws Exception		<i>todo.</i>
	 */
	public Map<ApplicationVMPortTypes, String>	allocateFreeAVM() throws Exception ;
	
	/**
	 * recycle a free AVM previously allocated to a performance controller.
	 *
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	AVMuri != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param AVMuri		uri of the free AVM 
	 * @throws Exception	<i>todo.</i>
	 */
	public void		recycleFreeAVM(String AVMuri) throws Exception;
	
}

