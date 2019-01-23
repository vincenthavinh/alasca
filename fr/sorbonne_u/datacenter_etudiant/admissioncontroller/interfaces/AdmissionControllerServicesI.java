package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces;

import java.util.Map;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
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
 * <p>Created on : 16 January, 2019</p>
 * 
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
	 * @return		uri of the allocated AVM
	 * @throws Exception		<i>todo.</i>
	 */
	public Map<ApplicationVMPortTypes, String>	allocateFreeAVM() throws Exception ;
	
	/**
	 * recycle a free AVM previously allocated to the performance controller.
	 *
	 * @param uri of the AVM
	 * @throws Exception	<i>todo.</i>
	 */
	public void		recycleFreeAVM(String AVMuri) throws Exception;
	
	/**
	 * find an idle core 
	 * 
	 * @param nbCore
	 * @return a core allocated from a computer
	 * @throws Exception
	 */
	public AllocatedCore[] findComputerAndAllocateCores(int nbCore) throws Exception;
}

