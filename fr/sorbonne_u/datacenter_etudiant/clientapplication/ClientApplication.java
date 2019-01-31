package fr.sorbonne_u.datacenter_etudiant.clientapplication;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.AbstractPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.connectors.ApplicationHostingConnector;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports.ApplicationHostingOutboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementInboundPort;

public class ClientApplication 
extends AbstractComponent {

	/**URI de ce composant**/
	protected String ca_URI;
	
	protected int nbCores_required;
	
	/**admission controller**/
	//outboundport
	protected String ac_ApplicationSubmissionInboundPortURI;
	protected ApplicationHostingOutboundPort ca_ApplicationSubmissionOutboundPort;
	
	
	/**params du request generator**/
	protected final String rg_URI ;
	protected double rg_meanInterArrivalTime ;
	protected long rg_meanNumberOfInstructions ;
	protected String rg_RequestGeneratorManagementInboundPortURI ;
	protected String rg_RequestSubmissionInboundPortURI;
	protected String rg_RequestNotificationInboundPortURI;
	
	/** params du performance controller*/
	protected int pc_seuil_inf;
	protected int pc_seuil_sup;
	
	public ClientApplication(
			
			//params du client application
			String ca_URI,
			String ca_ApplicationNotificationInboundPortURI,
			String ac_ApplicationSubmissionInboundPortURI,
			int nbCores_required,
			
			//params du request generator
			String rg_URI,
			double rg_meanInterArrivalTime,
			long rg_meanNumberOfInstructions,
			
			//params du performance controller
			int pc_seuil_inf,
			int pc_seuil_sup
	
	) throws Exception {
		super(1, 1) ;

		//sauvegarde des params du request generator
		this.rg_URI = rg_URI;
		this.rg_meanInterArrivalTime = rg_meanInterArrivalTime;
		this.rg_meanNumberOfInstructions = rg_meanNumberOfInstructions;
		
		//initialisation
		this.ca_URI = ca_URI;
		this.ac_ApplicationSubmissionInboundPortURI = ac_ApplicationSubmissionInboundPortURI;
		this.nbCores_required = nbCores_required;
		this.pc_seuil_inf = pc_seuil_inf;
		this.pc_seuil_sup = pc_seuil_sup;
		
		//initialisation des ports
		
		/**offered**/

		/**required**/
		//ApplicationSubmission
		this.addRequiredInterface(ApplicationHostingI.class) ;
		this.ca_ApplicationSubmissionOutboundPort = new ApplicationHostingOutboundPort(this) ;
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
					ApplicationHostingConnector.class.getCanonicalName()) ;
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	@Override
	public void execute() throws Exception {
		super.execute();
		
		this.rg_RequestNotificationInboundPortURI = AbstractPort.generatePortURI(RequestNotificationInboundPort.class);
		this.rg_RequestGeneratorManagementInboundPortURI = AbstractPort.generatePortURI(RequestGeneratorManagementInboundPort.class);
		this.rg_RequestSubmissionInboundPortURI = this.ca_ApplicationSubmissionOutboundPort.askHosting(rg_RequestNotificationInboundPortURI, nbCores_required, pc_seuil_inf, pc_seuil_sup);
	
		logMessage("CliApp. "+  this.ca_URI +"| a recu [" +this.rg_RequestSubmissionInboundPortURI+ "] de l'Admission Controller.");
		
		if(this.rg_RequestSubmissionInboundPortURI == null) {
			logMessage("CliApp. "+  this.ca_URI +"| La demande d'hébèrgement a été refusée...");
			return;
		}
		RequestGenerator rg = new RequestGenerator(
				this.rg_URI, 
				this.rg_meanInterArrivalTime, 
				this.rg_meanNumberOfInstructions, 
				this.rg_RequestGeneratorManagementInboundPortURI, 
				this.rg_RequestSubmissionInboundPortURI, 
				this.rg_RequestNotificationInboundPortURI);
		rg.toggleTracing();
		rg.toggleLogging();
		rg.start();
		
		logMessage("CliApp. "+  this.ca_URI +"| askHostToConnect(..) a AdmissionController");
		
		Boolean isHostConnected = this.ca_ApplicationSubmissionOutboundPort.
				askHostToConnect(rg_RequestNotificationInboundPortURI);
		
		logMessage("CliApp. "+  this.ca_URI +"| askToConnect(..) returned: " +isHostConnected);
		
		if(isHostConnected == true) {
			logMessage("CliApp. "+  this.ca_URI +"| démarrage du request generator...");
			rg.startGeneration();
			// wait 20 seconds
			Thread.sleep(8000L) ;
			// then stop the generation.
			rg.stopGeneration() ;
		}else {
			logMessage("CliApp. "+  this.ca_URI +"| le datacenter n'a pas pu héberger l'application.");
		}
	}
	

	@Override
	public void	finalise() throws Exception {
		this.doPortDisconnection(this.ca_ApplicationSubmissionOutboundPort.getPortURI()) ;
		super.finalise() ;
	}
	
	@Override
	public void	shutdown() throws ComponentShutdownException {
		try {
			this.ca_ApplicationSubmissionOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
	}

}
