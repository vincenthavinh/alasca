package fr.sorbonne_u.datacenter_etudiant.clientapplication;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.connectors.ApplicationSubmissionConnector;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationNotificationHandlerI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports.ApplicationNotificationInboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports.ApplicationSubmissionOutboundPort;

public class ClientApplication 
extends AbstractComponent 
implements ApplicationNotificationHandlerI {

	/**URI de ce composant**/
	protected String ca_URI;
	
	/**admission controller**/
	//inboundport
	protected ApplicationNotificationInboundPort ca_ApplicationNotificationInboundPort;
	//outboundport
	protected String ac_ApplicationSubmissionInboundPortURI;
	protected ApplicationSubmissionOutboundPort ca_ApplicationSubmissionOutboundPort;
		
	
	/**params du request generator**/
	protected final String rg_URI ;
	protected double rg_meanInterArrivalTime ;
	protected long rg_meanNumberOfInstructions ;
	protected String rg_rgmipURI ;
	protected String rg_rsipURI;
	protected String rg_rnipURI;
	
	
	public ClientApplication(
			
			//params du client application
			String ca_URI,
			String ca_ApplicationNotificationInboundPortURI,
			String ac_ApplicationSubmissionInboundPortURI,
			
			//params du request generator
			String rg_URI,
			double rg_meanInterArrivalTime,
			long rg_meanNumberOfInstructions
//			String rg_rgmipURI,
//			String rg_rsipURI,
//			String rg_rnipURI
	
	) throws Exception {
		super(1, 1) ;

		//svgd des params du request generator
		this.rg_URI = rg_URI;
		this.rg_meanInterArrivalTime = rg_meanInterArrivalTime;
		this.rg_meanNumberOfInstructions = rg_meanNumberOfInstructions;
//		this.rg_rgmipURI = rg_rgmipURI;
//		this.rg_rsipURI = rg_rsipURI;
//		this.rg_rnipURI = rg_rnipURI;
		
		//initialisation
		this.ca_URI = ca_URI;
		this.ac_ApplicationSubmissionInboundPortURI = ac_ApplicationSubmissionInboundPortURI;
		
		//initialisation des ports
		
		/**offered**/
		//ApplicationNotification
		this.addOfferedInterface(ApplicationNotificationI.class) ;
		this.ca_ApplicationNotificationInboundPort = new ApplicationNotificationInboundPort(ca_ApplicationNotificationInboundPortURI, this);
		this.addPort(this.ca_ApplicationNotificationInboundPort) ;
		this.ca_ApplicationNotificationInboundPort.publishPort() ;
		
		/**required**/
		//ApplicationSubmission
		this.addRequiredInterface(ApplicationSubmissionI.class) ;
		this.ca_ApplicationSubmissionOutboundPort = new ApplicationSubmissionOutboundPort(this) ;
		this.addPort(this.ca_ApplicationSubmissionOutboundPort) ;
		this.ca_ApplicationSubmissionOutboundPort.publishPort() ;
		
	}
	
	
	//Component Life Cycle

	@Override
	public void	start() throws ComponentStartException {
		super.start() ;

		try {
			this.doPortConnection(
					this.ca_ApplicationSubmissionOutboundPort.getPortURI(),
					this.ac_ApplicationSubmissionInboundPortURI,
					ApplicationSubmissionConnector.class.getCanonicalName()) ;
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	@Override
	public void execute() throws Exception {
		super.execute();
		this.ca_ApplicationSubmissionOutboundPort.submitApplicationAndNotify();
	}
	

	@Override
	public void	finalise() throws Exception {
		this.doPortDisconnection(this.ca_ApplicationSubmissionOutboundPort.getPortURI()) ;
		super.finalise() ;
	}
	
	@Override
	public void	shutdown() throws ComponentShutdownException {
		try {
			this.ca_ApplicationNotificationInboundPort.unpublishPort();
			this.ca_ApplicationSubmissionOutboundPort.unpublishPort();
//			this.rsop.unpublishPort() ;
//			this.rnip.unpublishPort() ;
//			this.rgmip.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
	}
	
	//Handler

	@Override
	public void acceptApplicationReadyNotification() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void acceptApplicationRejectedNotification() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
