package fr.sorbonne_u.datacenter.hardware.computers.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerIntrospectionI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateI;

/**
 * The class <code>ComputerIntrospectionInboundPort</code> defines
 * an inbound port associated with the interface
 * <code>ComputerIntrospectionI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : February 2, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class				ComputerIntrospectionInboundPort
extends		AbstractInboundPort
implements	ComputerIntrospectionI
{
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public				ComputerIntrospectionInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ComputerIntrospectionI.class, owner) ;
	}

	public				ComputerIntrospectionInboundPort(
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
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ((Computer)this.getOwner()).
									isReserved(processorNo, coreNo) ;
					}
				}) ;
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerIntrospectionI#getStaticState()
	 */
	@Override
	public ComputerStaticStateI	getStaticState() throws Exception
	{
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<ComputerStaticStateI>() {
					@Override
					public ComputerStaticStateI call() throws Exception {
						return ((Computer)this.getOwner()).
									getStaticState() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerIntrospectionI#getDynamicState()
	 */
	@Override
	public ComputerDynamicStateI getDynamicState() throws Exception
	{
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<ComputerDynamicStateI>() {
					@Override
					public ComputerDynamicStateI call() throws Exception {
						return ((Computer)this.getOwner()).
													getDynamicState() ;
					}
				}) ;
	}
}
