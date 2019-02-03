package fr.sorbonne_u.datacenter.hardware.computers.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * The interface <code>ComputerIntrospectionI</code> gives access to the
 * state information of computer through offered ports of computer.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : February 2, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public interface			ComputerIntrospectionI
extends		OfferedI,
			RequiredI
{
	/**
	 * return true if a core is reserved.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param processorNo	processor number to be tested.
	 * @param coreNo		core number to be tested.
	 * @return				true if a core is reserved.
	 * @throws Exception	<i>todo.</i>
	 */
	public boolean		isReserved(int processorNo, int coreNo) throws Exception;

	/**
	 * return the static state of the computer as an instance of
	 * <code>ComputerStaticStateI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return	the static state of the computer.
	 * @throws Exception	<i>todo.</i>
	 */
	public ComputerStaticStateI	getStaticState() throws Exception ;

	/**
	 * return the dynamic state of the computer as an instance of
	 * <code>ComputerDynamicStateI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return	the dynamic state of the computer.
	 * @throws Exception	<i>todo.</i>
	 */
	public ComputerDynamicStateI	getDynamicState() throws Exception ;
}
