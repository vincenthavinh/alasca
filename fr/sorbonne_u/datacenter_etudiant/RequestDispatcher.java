package fr.sorbonne_u.datacenter_etudiant;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

public class RequestDispatcher extends AbstractComponent {

	//nom de ce composant
	protected String rdURI;
	
	
	//ports fournis
	protected RequestSubmissionInboundPort requestSubmissionInboundPort ;
	protected RequestNotificationInboundPort requestNotificationInboundPort ;
	
	//ports requis
	protected RequestSubmissionOutboundPort requestSubmissionOutboundPort ;
	protected RequestNotificationOutboundPort requestNotificationOutboundPort ;
	
	
	
	public RequestDispatcher(
		String rdURI,
		String requestSubmissionInboundPortURI,
		String requestNotificationInboundPortURI) throws Exception {
		
		super(1, 1);
		
		// Preconditions
		assert	rdURI != null ;
		assert	requestSubmissionInboundPortURI != null ;
		assert	requestNotificationInboundPortURI != null ;
		
		//initialisation
		this.rdURI = rdURI;
		
		//init des ports fournisseurs
		this.addOfferedInterface(RequestSubmissionI.class) ;
		this.requestSubmissionInboundPort = new RequestSubmissionInboundPort(requestSubmissionInboundPortURI, this);
		this.addPort(this.requestSubmissionInboundPort) ;
		this.requestSubmissionInboundPort.publishPort() ;
		
		this.addOfferedInterface(RequestNotificationI.class) ;
		this.requestNotificationInboundPort = new RequestNotificationInboundPort(requestNotificationInboundPortURI, this);
		this.addPort(this.requestNotificationInboundPort) ;
		this.requestNotificationInboundPort.publishPort() ;
		
		//init des ports requis
		this.addRequiredInterface(RequestSubmissionI.class) ;
		this.requestSubmissionOutboundPort = new RequestSubmissionOutboundPort(this) ;
		this.addPort(this.requestSubmissionOutboundPort) ;
		this.requestSubmissionOutboundPort.publishPort() ;
		
		this.addRequiredInterface(RequestNotificationI.class) ;
		this.requestNotificationOutboundPort = new RequestNotificationOutboundPort(this) ;
		this.addPort(this.requestNotificationOutboundPort) ;
		this.requestNotificationOutboundPort.publishPort() ;
			
	
		//Postconditions check
		assert	this.requestSubmissionOutboundPort != null && this.requestSubmissionOutboundPort instanceof RequestSubmissionI ;
		assert	this.requestNotificationOutboundPort != null && this.requestNotificationOutboundPort instanceof RequestNotificationI ;
	}
	
	// Component life cycle

	public void			start() throws ComponentStartException
	{
		super.start() ;

		try {
			this.doPortConnection(
					this.requestSubmissionOutboundPort.getPortURI(),
					this.requestSubmissionInboundPort.getPortURI(),
					RequestSubmissionConnector.class.getCanonicalName()) ;
			this.doPortConnection(
					this.requestNotificationOutboundPort.getPortURI(),
					this.requestNotificationInboundPort.getPortURI(),
					RequestNotificationConnector.class.getCanonicalName()) ;
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	@Override
	public void			finalise() throws Exception
	{
		// TO DO
		this.doPortDisconnection(this.requestNotificationOutboundPort.getPortURI()) ;
		this.doPortDisconnection(this.requestSubmissionOutboundPort.getPortURI()) ;

		super.finalise() ;
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{

		try {
			this.requestSubmissionInboundPort.unpublishPort() ;
			this.requestNotificationInboundPort.unpublishPort() ;
			this.requestSubmissionOutboundPort.unpublishPort() ;
			this.requestNotificationOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
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
}
