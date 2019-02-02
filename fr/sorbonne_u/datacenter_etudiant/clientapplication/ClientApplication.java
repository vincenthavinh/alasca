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

/**
 * La classe <code>ClientApplication</code> simule le client qui demande l'hébergement de son
 * application.
 *
 * <p><strong>Description</strong></p>
 * 
 * Le <code>ClientApplication</code> est un composant qui simule le comportement d'un client,
 * il peut demander d'hébergement de son application au controleur d'admission et se connecte
 * aux composants de traitements après une réponse positive du controleur d'admission. Ensuite,
 * il lance le générateur de requête qui va passer les requêtes vers le répartiteur de requête
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * TODO: complete!
 * 
 * <pre>
 * invariant		ca_URI != null
 * invariant		ca_ApplicationNotificationInboundPortURI != null
 * invariant		ac_ApplicationSubmissionInboundPortURI != null
 * invariant		nbCoresByAVM != 0
 * invariant		rg_URI != null
 * invariant		rg_meanInterArrivalTime != 0
 * invariant		rg_meanNumberOfInstructions != 0
 * invariant		pc_seuil_inf <= pc_seuil_sup
 * </pre>
 * 
 * <p>Created on : February 1, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class ClientApplication 
extends AbstractComponent {

	/**URI de ce composant**/
	protected String ca_URI;
	
	/**Nombre de coeurs que le client estime pour chaque avm qui va exécuter les requêtes 
	 * (on a choisi arbitrairement que 2 avms par client pour le début)*/
	protected int nbCoresByAVM;
	
	/**admission controller**/
	protected String ac_ApplicationSubmissionInboundPortURI;
	protected ApplicationHostingOutboundPort ca_ApplicationSubmissionOutboundPort;
	
	
	/**params du request generator**/
	protected final String rg_URI ;
	protected double rg_meanInterArrivalTime ;
	protected long rg_meanNumberOfInstructions ;
	protected String rg_RequestGeneratorManagementInboundPortURI ;
	protected String rg_RequestNotificationInboundPortURI;
	
	/**Le submission inbound port du répartiteur de requête*/
	protected String rd_RequestSubmissionInboundPortURI;
	
	/** params du performance controller*/
	protected int pc_seuil_inf;
	protected int pc_seuil_sup;
	
	/**
	 * Créer un client application en donnant son URI et les inbound ports
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre		ca_URI != null
	 * pre		ca_ApplicationNotificationInboundPortURI != null
	 * pre		ac_ApplicationSubmissionInboundPortURI != null
	 * pre		nbCoresByAVM != 0
	 * pre		rg_URI != null
	 * pre		rg_meanInterArrivalTime != 0
	 * pre		rg_meanNumberOfInstructions != 0
	 * pre		pc_seuil_inf <= pc_seuil_sup
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param ca_URI									URI du clientapplication
	 * @param ca_ApplicationNotificationInboundPortURI	URI du notification inbound port du  clientapplication
	 * @param ac_ApplicationSubmissionInboundPortURI	URI du submission inbound port du admission controlleur
	 * @param nbCoresByAVM								Nombre de coeurs estimé pour chaque avm attribué à cet application
	 * @param rg_URI									URI du générateur de requête
	 * @param rg_meanInterArrivalTime					La moyenne des intervalles de temps d'envoie de requêtes
	 * @param rg_meanNumberOfInstructions				La moyenne du nombre d'instructions des requêtes
	 * @param pc_seuil_inf								Seuil inférieur du temps moyen d'exécution souhaité
	 * @param pc_seuil_sup								Seuil supérieur du temps moyen d'exécution souhaité
	 * @throws Exception
	 */
	public ClientApplication(
			
			//params du client application
			String ca_URI,
			String ca_ApplicationNotificationInboundPortURI,
			String ac_ApplicationSubmissionInboundPortURI,
			int nbCoresByAVM,
			
			//params du request generator
			String rg_URI,
			double rg_meanInterArrivalTime,
			long rg_meanNumberOfInstructions,
			
			//params du performance controller
			int pc_seuil_inf,
			int pc_seuil_sup
	
	) throws Exception {
		super(1, 1) ;
		 assert ca_URI != null;
		 assert ca_ApplicationNotificationInboundPortURI != null;
		 assert ac_ApplicationSubmissionInboundPortURI != null;
		 assert nbCoresByAVM != 0;
		 assert rg_URI != null;
		 assert rg_meanInterArrivalTime != 0;
		 assert rg_meanNumberOfInstructions != 0;
		 assert pc_seuil_inf <= pc_seuil_sup;
		//sauvegarde des params du request generator
		this.rg_URI = rg_URI;
		this.rg_meanInterArrivalTime = rg_meanInterArrivalTime;
		this.rg_meanNumberOfInstructions = rg_meanNumberOfInstructions;
		
		//initialisation
		this.ca_URI = ca_URI;
		this.ac_ApplicationSubmissionInboundPortURI = ac_ApplicationSubmissionInboundPortURI;
		this.nbCoresByAVM = nbCoresByAVM;
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
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
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
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void execute() throws Exception {
		super.execute();
		
		// Génère les ports du générateur de requête
		this.rg_RequestNotificationInboundPortURI = AbstractPort.generatePortURI(RequestNotificationInboundPort.class);
		this.rg_RequestGeneratorManagementInboundPortURI = AbstractPort.generatePortURI(RequestGeneratorManagementInboundPort.class);
		
		// Demande l'hébergement au controlleur d'admission et reçoit le request submission inbound port du répartiteur de requête attribué
		this.rd_RequestSubmissionInboundPortURI = this.ca_ApplicationSubmissionOutboundPort.askHosting(rg_RequestNotificationInboundPortURI, nbCoresByAVM, pc_seuil_inf, pc_seuil_sup);
		
		// Si la demande est refusée, on reçoit null
		if(this.rd_RequestSubmissionInboundPortURI == null) {
			logMessage("CliApp. "+  this.ca_URI +"| La demande d'hébèrgement a été refusée...");
			return;
		}
		logMessage("CliApp. "+  this.ca_URI +"| a recu [" +this.rd_RequestSubmissionInboundPortURI+ "] de l'Admission Controller.");
		
		// sinon commence créer le générateur de requête
		RequestGenerator rg = new RequestGenerator(
				this.rg_URI, 
				this.rg_meanInterArrivalTime, 
				this.rg_meanNumberOfInstructions, 
				this.rg_RequestGeneratorManagementInboundPortURI, 
				this.rd_RequestSubmissionInboundPortURI, 
				this.rg_RequestNotificationInboundPortURI);
		rg.toggleTracing();
		rg.toggleLogging();
		rg.start();
		
		logMessage("CliApp. "+  this.ca_URI +"| askHostToConnect(..) a AdmissionController");
		
		// connect les composants entre eux
		Boolean isHostConnected = this.ca_ApplicationSubmissionOutboundPort.
				askHostToConnect(rg_RequestNotificationInboundPortURI);
		logMessage("CliApp. "+  this.ca_URI +"| askToConnect(..) returned: " +isHostConnected);
		
		// si les connections réussissent, commence la génération de requête 
		if(isHostConnected == true) {
			logMessage("CliApp. "+  this.ca_URI +"| démarrage du request generator...");
			rg.startGeneration();
			// wait 20 seconds
			Thread.sleep(80000L) ;
			// then stop the generation.
			rg.stopGeneration() ;
		}else {
			logMessage("CliApp. "+  this.ca_URI +"| le datacenter n'a pas pu héberger l'application.");
		}
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void	finalise() throws Exception {
		this.doPortDisconnection(this.ca_ApplicationSubmissionOutboundPort.getPortURI()) ;
		super.finalise() ;
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
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
