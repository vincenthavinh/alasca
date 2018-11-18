package fr.sorbonne_u.datacenter_etudiant.tests.integrators;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class				IntegratorRequestDispatcher_multi_generator
extends		AbstractComponent
{
	protected String									rg0mipURI ;
	protected String									rg1mipURI ;
	protected String									rd0mipURI ;
	protected String									rd1mipURI ;
	protected String									csipURI ;
	protected String									avm0ipURI ;
	protected String									avm1ipURI ;
	protected String									avm2ipURI ;
	protected String									avm3ipURI ;
	/** Port connected to the request generator component to manage its
	 *  execution (starting and stopping the request generation).			*/
	protected RequestGeneratorManagementOutboundPort	rg0mop ;
	protected RequestGeneratorManagementOutboundPort	rg1mop ;
	/** Port connected to the request dispatcher component to manage its
	 *	connections 														*/
	protected RequestDispatcherManagementOutboundPort rd0mop ;
	protected RequestDispatcherManagementOutboundPort rd1mop ;
	/** Port connected to the computer component to access its services.	*/
	protected ComputerServicesOutboundPort			csop ;
	/** Port connected to the AVM component to allocate it cores.			*/
	protected ApplicationVMManagementOutboundPort	avm0op ;
	protected ApplicationVMManagementOutboundPort	avm1op ;
	protected ApplicationVMManagementOutboundPort	avm2op ;
	protected ApplicationVMManagementOutboundPort	avm3op ;
	
	public				IntegratorRequestDispatcher_multi_generator(
		String csipURI,
		String avm0ipURI,
		String avm1ipURI,
		String avm2ipURI,
		String avm3ipURI,
		String rg0mipURI,
		String rg1mipURI,
		String rd0mipURI,
		String rd1mipURI
		) throws Exception
	{
		super(1, 0) ;

		assert	csipURI != null 
				&& avm0ipURI != null 
				&& avm1ipURI != null 
				&& avm2ipURI != null 
				&& avm3ipURI != null 
				&& rg0mipURI != null 
				&& rg1mipURI != null 
				&& rd0mipURI != null
				&& rd1mipURI != null;

		this.rg0mipURI = rg0mipURI ;
		this.rg1mipURI = rg1mipURI ;
		this.rd0mipURI = rd0mipURI ;
		this.rd1mipURI = rd1mipURI ;
		this.avm0ipURI = avm0ipURI ;
		this.avm1ipURI = avm1ipURI ;
		this.avm2ipURI = avm2ipURI ;
		this.avm3ipURI = avm3ipURI ;
		this.csipURI = csipURI ;

		this.addRequiredInterface(ComputerServicesI.class) ;
		this.addRequiredInterface(RequestGeneratorManagementI.class) ;
		this.addRequiredInterface(RequestDispatcherManagementI.class) ;
		this.addRequiredInterface(ApplicationVMManagementI.class) ;

		this.csop = new ComputerServicesOutboundPort(this) ;
		this.addPort(this.csop) ;
		this.csop.publishPort() ;

		this.rg0mop = new RequestGeneratorManagementOutboundPort(this) ;
		this.addPort(rg0mop) ;
		this.rg0mop.publishPort() ;
		
		this.rg1mop = new RequestGeneratorManagementOutboundPort(this) ;
		this.addPort(rg1mop) ;
		this.rg1mop.publishPort() ;
		
		this.rd0mop = new RequestDispatcherManagementOutboundPort(this) ;
		this.addPort(rd0mop) ;
		this.rd0mop.publishPort() ;
		
		this.rd1mop = new RequestDispatcherManagementOutboundPort(this) ;
		this.addPort(rd1mop) ;
		this.rd1mop.publishPort() ;

		this.avm0op = new ApplicationVMManagementOutboundPort(this) ;
		this.addPort(this.avm0op) ;
		this.avm0op.publishPort() ;
		
		this.avm1op = new ApplicationVMManagementOutboundPort(this) ;
		this.addPort(this.avm1op) ;
		this.avm1op.publishPort() ;
		
		this.avm2op = new ApplicationVMManagementOutboundPort(this) ;
		this.addPort(this.avm2op) ;
		this.avm2op.publishPort() ;
		
		this.avm3op = new ApplicationVMManagementOutboundPort(this) ;
		this.addPort(this.avm3op) ;
		this.avm3op.publishPort() ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;

		try {
			this.doPortConnection(
				this.csop.getPortURI(),
				this.csipURI,
				ComputerServicesConnector.class.getCanonicalName()) ;
			this.doPortConnection(
				this.rg0mop.getPortURI(),
				rg0mipURI,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;
			this.doPortConnection(
				this.rg1mop.getPortURI(),
				rg1mipURI,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;
			this.doPortConnection(
				this.rd0mop.getPortURI(), 
				rd0mipURI, 
				RequestDispatcherManagementConnector.class.getCanonicalName()) ;
			this.doPortConnection(
				this.rd1mop.getPortURI(), 
				rd1mipURI, 
				RequestDispatcherManagementConnector.class.getCanonicalName()) ;
			this.doPortConnection(
				this.avm0op.getPortURI(),
				avm0ipURI,
				ApplicationVMManagementConnector.class.getCanonicalName()) ;
			this.doPortConnection(
				this.avm1op.getPortURI(),
				avm1ipURI,
				ApplicationVMManagementConnector.class.getCanonicalName()) ;
			this.doPortConnection(
				this.avm2op.getPortURI(),
				avm2ipURI,
				ApplicationVMManagementConnector.class.getCanonicalName()) ;
			this.doPortConnection(
				this.avm3op.getPortURI(),
				avm3ipURI,
				ApplicationVMManagementConnector.class.getCanonicalName()) ;
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;

		this.rd0mop.connectOutboundPorts();
		this.rd1mop.connectOutboundPorts();
		this.avm0op.connectOutboundPorts();
		this.avm1op.connectOutboundPorts();
		this.avm2op.connectOutboundPorts();
		this.avm3op.connectOutboundPorts();

		AllocatedCore[] ac0 = this.csop.allocateCores(1) ;
		this.avm0op.allocateCores(ac0) ;
		AllocatedCore[] ac1 = this.csop.allocateCores(1) ;
		this.avm1op.allocateCores(ac1) ;
		AllocatedCore[] ac2 = this.csop.allocateCores(1) ;
		this.avm2op.allocateCores(ac2) ;
		AllocatedCore[] ac3 = this.csop.allocateCores(1) ;
		this.avm3op.allocateCores(ac3) ;
		this.rg0mop.startGeneration() ;
		this.rg1mop.startGeneration() ;
		// wait 20 seconds
		Thread.sleep(2000L) ;
		// then stop the generation.
		this.rg0mop.stopGeneration() ;
		this.rg1mop.stopGeneration() ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.doPortDisconnection(this.csop.getPortURI()) ;
		this.doPortDisconnection(this.avm0op.getPortURI()) ;
		this.doPortDisconnection(this.avm1op.getPortURI()) ;
		this.doPortDisconnection(this.rg0mop.getPortURI()) ;
		this.doPortDisconnection(this.rd0mop.getPortURI()) ;
		this.doPortDisconnection(this.avm2op.getPortURI()) ;
		this.doPortDisconnection(this.avm3op.getPortURI()) ;
		this.doPortDisconnection(this.rg1mop.getPortURI()) ;
		this.doPortDisconnection(this.rd1mop.getPortURI()) ;
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.csop.unpublishPort() ;
			this.avm0op.unpublishPort() ;
			this.avm1op.unpublishPort() ;
			this.rg0mop.unpublishPort() ;
			this.rd0mop.unpublishPort() ;
			this.avm2op.unpublishPort() ;
			this.avm3op.unpublishPort() ;
			this.rg1mop.unpublishPort() ;
			this.rd1mop.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdownNow()
	 */
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{
		try {
			this.csop.unpublishPort() ;
			this.avm0op.unpublishPort() ;
			this.avm1op.unpublishPort() ;
			this.rg0mop.unpublishPort() ;
			this.rd0mop.unpublishPort() ;
			this.avm2op.unpublishPort() ;
			this.avm3op.unpublishPort() ;
			this.rg1mop.unpublishPort() ;
			this.rd1mop.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdownNow();
	}
}
