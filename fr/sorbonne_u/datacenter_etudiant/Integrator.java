package fr.sorbonne_u.datacenter_etudiant;

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
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class				Integrator
extends		AbstractComponent
{
	protected String									rmipURI ;
	protected String									csipURI ;
	protected String									avmipURI ;
	/** Port connected to the request generator component to manage its
	 *  execution (starting and stopping the request generation).			*/
	protected RequestGeneratorManagementOutboundPort	rmop ;
	/** Port connected to the computer component to access its services.	*/
	protected ComputerServicesOutboundPort			csop ;
	/** Port connected to the AVM component to allocate it cores.			*/
	protected ApplicationVMManagementOutboundPort	avmop ;
	
	public				Integrator(
		String csipURI,
		String avmipURI,
		String rmipURI
		) throws Exception
	{
		super(1, 0) ;

		assert	csipURI != null && avmipURI != null && rmipURI != null ;

		this.rmipURI = rmipURI ;
		this.avmipURI = avmipURI ;
		this.csipURI = csipURI ;

		this.addRequiredInterface(ComputerServicesI.class) ;
		this.addRequiredInterface(RequestGeneratorManagementI.class) ;
		this.addRequiredInterface(ApplicationVMManagementI.class) ;

		this.csop = new ComputerServicesOutboundPort(this) ;
		this.addPort(this.csop) ;
		this.csop.publishPort() ;

		this.rmop = new RequestGeneratorManagementOutboundPort(this) ;
		this.addPort(rmop) ;
		this.rmop.publishPort() ;

		this.avmop = new ApplicationVMManagementOutboundPort(this) ;
		this.addPort(this.avmop) ;
		this.avmop.publishPort() ;
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
				this.rmop.getPortURI(),
				rmipURI,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;
			this.doPortConnection(
				this.avmop.getPortURI(),
				avmipURI,
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

		AllocatedCore[] ac = this.csop.allocateCores(4) ;
		this.avmop.allocateCores(ac) ;
		this.rmop.startGeneration() ;
		// wait 20 seconds
		Thread.sleep(2000L) ;
		// then stop the generation.
		this.rmop.stopGeneration() ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.doPortDisconnection(this.csop.getPortURI()) ;
		this.doPortDisconnection(this.avmop.getPortURI()) ;
		this.doPortDisconnection(this.rmop.getPortURI()) ;
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
			this.avmop.unpublishPort() ;
			this.rmop.unpublishPort() ;
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
			this.avmop.unpublishPort() ;
			this.rmop.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdownNow();
	}
}
