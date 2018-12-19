package fr.sorbonne_u.datacenter_etudiant.performanceController;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI;
import fr.sorbonne_u.datacenter_etudiant.performanceController.ports.PerformanceControllerManagementInboundPort;

public class PerformanceController 
extends AbstractComponent{	
	protected String pcURI;
	
	//var
	protected long tmp_moyenne;
	protected int qt_req;
	
	protected PerformanceControllerManagementInboundPort pcmip;
	
	public PerformanceController(
			String pcURI,
			String pc_management_ipURI) throws Exception {
		
		super(1,1);
		
		assert pcURI != null && pc_management_ipURI != null;
		
		this.pcURI = pcURI;
		
		/*Management*/
		this.addOfferedInterface(PerformanceControllerManagementI.class);
		this.pcmip = new PerformanceControllerManagementInboundPort(pc_management_ipURI, this);
		this.addPort(this.pcmip);
		this.pcmip.publishPort();
	}
	
	// Component life cycle
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
	}
	
	@Override
	public void			finalise() throws Exception
	{	
		super.finalise() ;
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.pcmip.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
		
	}

	public void connectOutboundPorts() {
		
	}
	
	public void toggleTracingLogging() {
		System.out.println("PC "+this.pcURI+" tooggled...");
		this.toggleTracing();
		this.toggleLogging();
	}
	
	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------
	
	public void receiveData(long temps_requete) {
		long total = tmp_moyenne * qt_req; 
		qt_req++;
		tmp_moyenne = (total + temps_requete) / qt_req;
	}
	
	
}
