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
//import fr.sorbonne_u.datacenter_etudiant.performanceController.connectors.PerformanceControllerManagementConnector;
import fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI;
import fr.sorbonne_u.datacenter_etudiant.performanceController.ports.PerformanceControllerManagementOutboundPort;
//import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.connectors.RequestDispatcherPerfManagementConnector;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherPerfManagementI;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports.RequestDispatcherPerfManagementOutboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class				IntegratorPerformanceController
extends		AbstractComponent
{
	protected String									rgmipURI ;
	protected String									rdmipURI ;
	protected String									csipURI ;
	protected String									avm0ipURI ;
	protected String									avm1ipURI ;
	protected String									pcmipURI ;
	
	/** Port connected to the request generator component to manage its
	 *  execution (starting and stopping the request generation).			*/
	protected RequestGeneratorManagementOutboundPort	rgmop ;
	/** Port connected to the request dispatcher component to manage its
	 *	connections 														*/
	protected RequestDispatcherPerfManagementOutboundPort rdmop ;
	/** Port connected to the computer component to access its services.	*/
	protected ComputerServicesOutboundPort			csop ;
	/** Port connected to the AVM component to allocate it cores.			*/
	protected ApplicationVMManagementOutboundPort	avm0op ;
	protected ApplicationVMManagementOutboundPort	avm1op ;
	/** Port connected to the Performance controller to manage its connections */
	protected PerformanceControllerManagementOutboundPort pcmop ;
	
	public				IntegratorPerformanceController(
		String csipURI,
		String avm0ipURI,
		String avm1ipURI,
		String rgmipURI,
		String rdmipURI,
		String pcmipURI
		) throws Exception
	{
		super(1, 0) ;

		assert	csipURI != null 
				&& avm0ipURI != null 
				&& avm1ipURI != null 
				&& rgmipURI != null 
				&& rdmipURI != null
				&& pcmipURI != null;

		this.rgmipURI = rgmipURI ;
		this.rdmipURI = rdmipURI ;
		this.avm0ipURI = avm0ipURI ;
		this.avm1ipURI = avm1ipURI ;
		this.csipURI = csipURI ;
		this.pcmipURI = pcmipURI ;

		this.addRequiredInterface(ComputerServicesI.class) ;
		this.addRequiredInterface(RequestGeneratorManagementI.class) ;
		this.addRequiredInterface(RequestDispatcherPerfManagementI.class) ;
		this.addRequiredInterface(ApplicationVMManagementI.class) ;
		this.addRequiredInterface(PerformanceControllerManagementI.class);

		this.csop = new ComputerServicesOutboundPort(this) ;
		this.addPort(this.csop) ;
		this.csop.publishPort() ;

		this.rgmop = new RequestGeneratorManagementOutboundPort(this) ;
		this.addPort(rgmop) ;
		this.rgmop.publishPort() ;
		
		this.rdmop = new RequestDispatcherPerfManagementOutboundPort(this) ;
		this.addPort(rdmop) ;
		this.rdmop.publishPort() ;

		this.avm0op = new ApplicationVMManagementOutboundPort(this) ;
		this.addPort(this.avm0op) ;
		this.avm0op.publishPort() ;
		
		this.avm1op = new ApplicationVMManagementOutboundPort(this) ;
		this.addPort(this.avm1op) ;
		this.avm1op.publishPort() ;
		
		this.pcmop = new PerformanceControllerManagementOutboundPort(this);
		this.addPort(this.pcmop) ;
		this.pcmop.publishPort() ;
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
				this.rgmop.getPortURI(),
				rgmipURI,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;
			this.doPortConnection(
				this.rdmop.getPortURI(), 
				rdmipURI, 
				RequestDispatcherPerfManagementConnector.class.getCanonicalName()) ;
			this.doPortConnection(
				this.avm0op.getPortURI(),
				avm0ipURI,
				ApplicationVMManagementConnector.class.getCanonicalName()) ;
			this.doPortConnection(
				this.avm1op.getPortURI(),
				avm1ipURI,
				ApplicationVMManagementConnector.class.getCanonicalName()) ;
//			this.doPortConnection(
//				this.pcmop.getPortURI(), 
//				pcmipURI, 
//				PerformanceControllerManagementConnector.class.getCanonicalName());
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
		this.rdmop.connectOutboundPorts();
		this.avm0op.connectOutboundPorts();
		this.avm1op.connectOutboundPorts();
//		this.pcmop.connectOutboundPorts();

		AllocatedCore[] ac0 = this.csop.allocateCores(2) ;
		this.avm0op.allocateCores(ac0) ;
		AllocatedCore[] ac1 = this.csop.allocateCores(2) ;
		this.avm1op.allocateCores(ac1) ;
		this.rgmop.startGeneration() ;
		// wait 20 seconds
		Thread.sleep(50000L) ;
		// then stop the generation.
		this.rgmop.stopGeneration() ;
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
		this.doPortDisconnection(this.rgmop.getPortURI()) ;
		this.doPortDisconnection(this.rdmop.getPortURI()) ;
//		this.doPortDisconnection(this.pcmop.getPortURI());
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
			this.rgmop.unpublishPort() ;
			this.rdmop.unpublishPort() ;
			this.pcmop.unpublishPort() ;
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
			this.rgmop.unpublishPort() ;
			this.rdmop.unpublishPort() ;
			this.pcmop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdownNow();
	}
}
