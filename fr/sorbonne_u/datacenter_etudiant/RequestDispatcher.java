package fr.sorbonne_u.datacenter_etudiant;

import java.util.ArrayList;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

public class RequestDispatcher 
	extends AbstractComponent
	implements RequestSubmissionHandlerI{

	//nom de ce composant
	protected String rdURI;
	
	// liste de port des VMs
	protected ArrayList<String> requestSubmissionInboundPortsURI ; // AVMs
	
	protected String requestNotificationInboundPortURI ; // RG
	
	// ports appartenant au dispatcher
	protected RequestSubmissionInboundPort requestSubmissionInboundPort ;
	protected RequestSubmissionOutboundPort requestSubmissionOutboundPort ;
	protected RequestNotificationInboundPort requestNotificationInboundPort ;
	protected RequestNotificationOutboundPort requestNotificationOutboundPort ;
	
	public RequestDispatcher(
		String rdURI,
		String requestSubmissionInboundPortURI, // RD
		String requestNotificationInboundPortURI, // RG
		ArrayList<String> requestSubmissionInboundPortsURI /* AVMs */) throws Exception {
		
		super(1, 1);
		
		// Preconditions
		assert	rdURI != null ;
		assert	requestSubmissionInboundPortURI != null ;
		assert	requestSubmissionInboundPortsURI != null ;
		assert  requestSubmissionInboundPortsURI.size() != 0;
		
		//initialisation
		this.rdURI = rdURI;
		
		//init des ports dont dispatcher est le owner
		this.addOfferedInterface(RequestSubmissionI.class) ;
		this.requestSubmissionInboundPort = new RequestSubmissionInboundPort(requestSubmissionInboundPortURI, this);
		this.addPort(this.requestSubmissionInboundPort) ;
		this.requestSubmissionInboundPort.publishPort() ;
		
		this.addRequiredInterface(RequestSubmissionI.class) ;
		this.requestSubmissionOutboundPort = new RequestSubmissionOutboundPort(this) ;
		this.addPort(this.requestSubmissionOutboundPort) ;
		this.requestSubmissionOutboundPort.publishPort() ;
		
		//init des ports a connecter
		this.requestSubmissionInboundPortsURI = requestSubmissionInboundPortsURI;
	
		//Postconditions check
		assert	this.requestSubmissionOutboundPort != null && this.requestSubmissionOutboundPort instanceof RequestSubmissionI ;
	}
	
	// Component life cycle
	@Override
	public void			finalise() throws Exception
	{
		if(this.requestSubmissionOutboundPort.connected()) {
			for(String p : requestSubmissionInboundPortsURI) {
				if(this.isPortConnected(p)) {
					this.doPortDisconnection(p);
				}
			}
		}
		super.finalise() ;
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{

		try {
			this.requestSubmissionInboundPort.unpublishPort() ;
			this.requestSubmissionOutboundPort.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
	}
	
	@Override
	public void			acceptRequestSubmissionAndNotify(
		final RequestI r
		) throws Exception
	{
		
	}
	
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		
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
