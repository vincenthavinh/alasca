package fr.sorbonne_u.datacenter.hardware.computers.connectors ;


import fr.sorbonne_u.components.connectors.AbstractConnector ;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerIntrospectionI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateI;

/**
 * The class <code>ComputerIntrospectionConnector</code> implements the
 * connector between outbound and inboud ports implementing the interface
 * <code>ComputerIntrospectionI</code>.
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
public class				ComputerIntrospectionConnector
extends		AbstractConnector
implements	ComputerIntrospectionI
{
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerIntrospectionI#isReserved(int, int)
	 */
	@Override
	public boolean isReserved(int processorNo, int coreNo) throws Exception {
		return ((ComputerIntrospectionI)this.offering).isReserved(processorNo, coreNo);
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerIntrospectionI#getStaticState()
	 */
	@Override
	public ComputerStaticStateI getStaticState() throws Exception {
		return ((ComputerIntrospectionI)this.offering).getStaticState() ;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerIntrospectionI#getDynamicState()
	 */
	@Override
	public ComputerDynamicStateI getDynamicState() throws Exception {
		return ((ComputerIntrospectionI)this.offering).getDynamicState() ;
	}
}
