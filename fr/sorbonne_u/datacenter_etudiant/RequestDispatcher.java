package fr.sorbonne_u.datacenter_etudiant;

import java.util.ArrayList;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

public class RequestDispatcher 
	extends AbstractComponent
	implements RequestSubmissionHandlerI, 
			   RequestNotificationHandlerI{

	//nom de ce composant
	protected String rdURI;
	
	// liste de port des VMs
	//protected ArrayList<String> requestSubmissionInboundPortsURI ; // AVMs
	protected String requestSubmissionInboundPortURI ; // AVM
	
	protected String requestNotificationInboundPortURI ; // RG
	
	// ports appartenant au dispatcher
	protected RequestSubmissionInboundPort requestSubmissionInboundPort ;
	protected RequestSubmissionOutboundPort requestSubmissionOutboundPort ;
	protected RequestNotificationInboundPort requestNotificationInboundPort ;
	protected RequestNotificationOutboundPort requestNotificationOutboundPort ;
	
	public RequestDispatcher(
		String rdURI,
		String requestNotificationInboundPortURIdispatcher,
		String requestSubmissionInboundPortURIdispatcher,
		String requestNotificationInboundPortURI, //RG
		//ArrayList<String> requestSubmissionInboundPortsURI /* AVMs */) throws Exception {
		String requestSubmissionInboundPortURI /* AVM */) throws Exception {
		
		super(1, 1);
		
		// Preconditions
		assert	rdURI != null ;
		assert	requestSubmissionInboundPortURIdispatcher != null ;
		assert	requestNotificationInboundPortURIdispatcher != null ;
		
		assert	requestNotificationInboundPortURI != null ;
		//assert	requestSubmissionInboundPortsURI != null ;
		//assert  requestSubmissionInboundPortsURI.size() != 0;
		assert	requestSubmissionInboundPortURI != null ;
		
		//initialisation
		this.rdURI = rdURI;
		
		//init des ports dont dispatcher est le owner
		
		//offered
		
		/*Submission*/
		this.addOfferedInterface(RequestSubmissionI.class) ;
		this.requestSubmissionInboundPort = new RequestSubmissionInboundPort(requestSubmissionInboundPortURIdispatcher, this);
		this.addPort(this.requestSubmissionInboundPort) ;
		this.requestSubmissionInboundPort.publishPort() ;
		
		/*Notification*/
		this.addOfferedInterface(RequestNotificationI.class) ;
		this.requestNotificationInboundPort = new RequestNotificationInboundPort(requestNotificationInboundPortURIdispatcher, this);
		this.addPort(this.requestNotificationInboundPort) ;
		this.requestNotificationInboundPort.publishPort() ;
		
		//required
		
		/*Submission*/
		this.addRequiredInterface(RequestSubmissionI.class) ;
		this.requestSubmissionOutboundPort = new RequestSubmissionOutboundPort(this) ;
		this.addPort(this.requestSubmissionOutboundPort) ;
		this.requestSubmissionOutboundPort.publishPort() ;
		
		/*Notification*/
		this.addRequiredInterface(RequestNotificationI.class) ;
		this.requestNotificationOutboundPort = new RequestNotificationOutboundPort(this) ;
		this.addPort(this.requestNotificationOutboundPort) ;
		this.requestNotificationOutboundPort.publishPort() ;
		
		//init des ports a connecter
		//this.requestSubmissionInboundPortsURI = requestSubmissionInboundPortsURI; //aVMs
		this.requestSubmissionInboundPortURI = requestSubmissionInboundPortURI; //aVMs
		this.requestNotificationInboundPortURI = requestNotificationInboundPortURI; //RG
	
		//Postconditions check
		assert	this.requestSubmissionOutboundPort != null && this.requestSubmissionOutboundPort instanceof RequestSubmissionI ;
		assert	this.requestNotificationOutboundPort != null && this.requestNotificationOutboundPort instanceof RequestNotificationI ;
	}
	
	
	
	// Component life cycle
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;

		try {
			this.doPortConnection(
					this.requestNotificationOutboundPort.getPortURI(),
					requestNotificationInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName()) ;  //Connection RG
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	
	@Override
	public void			finalise() throws Exception
	{	
		this.doPortDisconnection(this.requestSubmissionOutboundPort.getPortURI()); //deconnection aVMs
		this.doPortDisconnection(this.requestNotificationOutboundPort.getPortURI()) ; //deconnection RG

		super.finalise() ;
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{

		try {
			this.requestSubmissionInboundPort.unpublishPort() ;
			this.requestSubmissionOutboundPort.unpublishPort() ;
			this.requestNotificationInboundPort.unpublishPort();
			this.requestNotificationOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
		
	}
	
	@Override
	public void	acceptRequestSubmissionAndNotify(final RequestI r) throws Exception {
		this.requestSubmissionOutboundPort.submitRequestAndNotify(r) ;
	}
	
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		this.requestSubmissionOutboundPort.submitRequest(r) ;
	}
	
	/*
	 * Une politique de répartition simple consiste à supposer que sur chaque machine virtuelle,
	l’instance de l’application possède une file d’attente des requêtes devant être exécutées, et le
	répartiteur envoie les requêtes à tour de rôle à chaque machine virtuelle.
	 */
	
	public void allocationMachineVirtuelle() {
		/*
		 * L’allocation d’une machine
		virtuelle à une application suppose donc la création d’une instance de l’application dans cette
		machine virtuelle puis l’inscription de cette instance auprès du répartiteur
		 */
	}
	
	public void deallocationMachineVirtuelle() {
		/*
		 * La déallocation d’une
		machine virtuelle suppose l’arrêt de l’envoi de requêtes à cette instance d’application, le traitement
		des dernières requêtes dans la file d’attente puis la désinscription auprès du répartiteur et la
		réinitialisation ou la destruction de la machine virtuelle.
		 */
	}



	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		this.requestNotificationOutboundPort.notifyRequestTermination(r);
	}

	
}
