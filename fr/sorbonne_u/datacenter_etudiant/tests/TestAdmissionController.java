package fr.sorbonne_u.datacenter_etudiant.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.AdmissionController;
import fr.sorbonne_u.datacenter_etudiant.clientapplication.ClientApplication;

public class TestAdmissionController extends AbstractCVM {

	/** static URIs **/
	//computer
	public static final String	cp_ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	cp_ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	cp_ComputerDynamicStateDataInboundPortURI = "cds-dip" ;

	//admission controller
	public static final String ac_ApplicationSubmissionInboundPortURI = "asip" ;
	
	//client application
	public static final String	ca_ApplicationNotificationInboundPortURI = "anip" ;
	
	//dynamic component creator 
	public static final String dcc_DynamicComponentCreationInboundPortURI = AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX ;
	
	/** static components **/
	protected ComputerMonitor cm ;
	protected AdmissionController ac ;
	protected ClientApplication ca;

	
	
	public TestAdmissionController() throws Exception {
		// TODO Auto-generated constructor stub
		super();
	}
	
	@Override
	public void			deploy() throws Exception
	{
		Processor.DEBUG = true ;

		// --------------------------------------------------------------------
		// Create and deploy a computer component with its 2 processors and
		// each with 2 cores.
		// --------------------------------------------------------------------
		String computerURI = "computer0" ;
		int numberOfProcessors = 2 ;
		int numberOfCores = 2 ;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
		admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000) ;	// and at 3 GHz
		Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
		processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips
		Computer c = new Computer(
							computerURI,
							admissibleFrequencies,
							processingPower,  
							1500,		// Test scenario 1, frequency = 1,5 GHz
							// 3000,	// Test scenario 2, frequency = 3 GHz
							1500,		// max frequency gap within a processor
							numberOfProcessors,
							numberOfCores,
							cp_ComputerServicesInboundPortURI,
							cp_ComputerStaticStateDataInboundPortURI,
							cp_ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(c) ;
		c.toggleTracing() ;
		c.toggleLogging() ;
		// --------------------------------------------------------------------
		
		
		// --------------------------------------------------------------------
		// Create the computer monitor component and connect its to ports
		// with the computer component.
		// --------------------------------------------------------------------
		this.cm = new ComputerMonitor(computerURI,
									 true,
									 cp_ComputerStaticStateDataInboundPortURI,
									 cp_ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(this.cm) ;
		// --------------------------------------------------------------------
	
		
		// --------------------------------------------------------------------
		// Create the Admission Controller component.
		// Il faut lui passer le(s) ordinateur(s) existant(s).
		// --------------------------------------------------------------------
		ArrayList<String> csipURIs = new ArrayList<String>();
		csipURIs.add(cp_ComputerServicesInboundPortURI);
		
		String ac_URI = "AdmissionController0";
		this.ac = new AdmissionController(
				ac_URI, 
				ac_ApplicationSubmissionInboundPortURI, 
				ca_ApplicationNotificationInboundPortURI,
				csipURIs,
				dcc_DynamicComponentCreationInboundPortURI);
		this.addDeployedComponent(this.ac);
		this.ac.toggleTracing() ;
		this.ac.toggleLogging() ;
		// --------------------------------------------------------------------
		
		// --------------------------------------------------------------------
		// Create the Client Application component.
		// Il faut lui passer l'admission controller pour communiquer avec
		// --------------------------------------------------------------------
		String ca_URI = "ClientApplication0";
		this.ca = new ClientApplication(
				ca_URI, 
				ca_ApplicationNotificationInboundPortURI, 
				ac_ApplicationSubmissionInboundPortURI, 
				"rg", 500.0, 6000000000L);
		this.addDeployedComponent(this.ca);
		this.ca.toggleTracing() ;
		this.ca.toggleLogging() ;
		// --------------------------------------------------------------------

		// complete the deployment at the component virtual machine level.
		super.deploy();
	}
	
	
	/**
	 * execute the test application.
	 * 
	 * @param args	command line arguments, disregarded here.
	 */
	public static void	main(String[] args)
	{
		// Uncomment next line to execute components in debug mode.
		//AbstractCVM.toggleDebugMode() ;
		try {
			final TestAdmissionController tac = new TestAdmissionController();
			tac.startStandardLifeCycle(100000L) ;
			// Augment the time if you want to examine the traces after
			// the execution of the program.
			Thread.sleep(100000L) ;
			// Exit from Java (closes all trace windows...).
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
	
}
