package fr.sorbonne_u.datacenter.hardware.computers.ports;


import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerIntrospectionI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateI;

/**
 * The class <code>ComputerIntrospectionOutboundPort</code> defines
 * an outbound port associated with the interface
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
 * <p>Created on : January 29, 2015</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class				ComputerIntrospectionOutboundPort
extends		AbstractOutboundPort
implements	ComputerIntrospectionI
{
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public				ComputerIntrospectionOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ComputerIntrospectionI.class, owner);
	}

	public				ComputerIntrospectionOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ComputerIntrospectionI.class, owner);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerIntrospectionI#isReserved(int, int)
	 */
	@Override
	public boolean isReserved(int processorNo, int coreNo) throws Exception {
		return ((ComputerIntrospectionI)this.connector).isReserved(processorNo, coreNo);
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerIntrospectionI#getStaticState()
	 */
	@Override
	public ComputerStaticStateI getStaticState() throws Exception
	{
		return ((ComputerIntrospectionI)this.connector).getStaticState() ;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerIntrospectionI#getDynamicState()
	 */
	@Override
	public ComputerDynamicStateI getDynamicState() throws Exception
	{
		return ((ComputerIntrospectionI)this.connector).getDynamicState() ;
	}
}
