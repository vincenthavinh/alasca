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
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter_etudiant.performanceController.PerformanceController;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.RequestDispatcherPerf;
import fr.sorbonne_u.datacenter_etudiant.tests.integrators.IntegratorPerformanceControllerDecrease;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;

public class				TestPerformanceControllerDecrease
extends		AbstractCVM
{
	public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
	public static final String	ApplicationVM0ManagementInboundPortURI = "avm0-ibp" ;
	public static final String	ApplicationVM1ManagementInboundPortURI = "avm1-ibp" ;
	public static final String	ApplicationVM0IntrospectionInboundPortURI = "intro0-ibp" ;
	public static final String	ApplicationVM1IntrospectionInboundPortURI = "intro1-ibp" ;
	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	public static final String	RequestSubmissionInboundPortURIAVM0 = "rsibp0" ;
	public static final String	RequestSubmissionInboundPortURIAVM1 = "rsibp1" ;
	public static final String	RequestNotificationInboundPortURI = "rnibp" ;
	
	public static final String	RequestDispatcherManagementInboundPortURIdispatcher = "rdmip_disp" ;
	public static final String	RequestSubmissionInboundPortURIdispatcher = "rsibp_disp" ;
	public static final String	RequestNotificationInboundPortURIdispatcher = "rnibp_disp" ;
	
	public static final String  PerformanceControllerManagementInboundPortURI = "pcmip";

	/** 	Computer monitor component.										*/
	protected ComputerMonitor						cm ;
	/** 	Application virtual machine component.							*/
	protected ApplicationVM							vm ;
	protected ApplicationVM							vm1 ;
	/** 	Request generator component.										*/
	protected RequestGenerator						rg ;
	
	/**		Request dispatcher component.								*/
	protected RequestDispatcherPerf 					rd ;
	//protected RequestDispatcher 					rd;
	
	/**     Performance Controller component. 							*/
	protected PerformanceController 				pc ;
	
	/** Integrator component.											*/
	protected IntegratorPerformanceControllerDecrease								integ ;

	// ------------------------------------------------------------------------
	// Component virtual machine constructors
	// ------------------------------------------------------------------------

	public				TestPerformanceControllerDecrease()
	throws Exception
	{
		super();
	}

	// ------------------------------------------------------------------------
	// Component virtual machine methods
	// ------------------------------------------------------------------------

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
							//1500,		// Test scenario 1, frequency = 1,5 GHz
							3000,	// Test scenario 2, frequency = 3 GHz
							1500,		// max frequency gap within a processor
							numberOfProcessors,
							numberOfCores,
							ComputerServicesInboundPortURI,
							ComputerStaticStateDataInboundPortURI,
							ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(c) ;
		c.toggleLogging() ;
		c.toggleTracing() ;
		c.logMessage("computer0 start");
		// --------------------------------------------------------------------

		
		
		// --------------------------------------------------------------------
		// Create the computer monitor component and connect its to ports
		// with the computer component.
		// --------------------------------------------------------------------
		this.cm = new ComputerMonitor(computerURI,
									 true,
									 ComputerStaticStateDataInboundPortURI,
									 ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(this.cm) ;
		// --------------------------------------------------------------------

		
		
		// --------------------------------------------------------------------
		// Create an Application VM component
		// --------------------------------------------------------------------
		this.vm = new ApplicationVM("vm0",	// application vm component URI
								    ApplicationVM0ManagementInboundPortURI,
								    ApplicationVM0IntrospectionInboundPortURI,
								    RequestSubmissionInboundPortURIAVM0,
								    RequestNotificationInboundPortURIdispatcher) ;
		this.addDeployedComponent(this.vm) ;
		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.vm.toggleTracing() ;
		this.vm.toggleLogging() ;
		vm.logMessage("vm0 start");
		// --------------------------------------------------------------------
		this.vm1 = new ApplicationVM("vm1",	// application vm component URI
								    ApplicationVM1ManagementInboundPortURI,
								    ApplicationVM1IntrospectionInboundPortURI,
								    RequestSubmissionInboundPortURIAVM1,
								    RequestNotificationInboundPortURIdispatcher) ;
		this.addDeployedComponent(this.vm1) ;
		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.vm1.toggleTracing() ;
		this.vm1.toggleLogging() ;
		vm1.logMessage("vm1 start");
		// --------------------------------------------------------------------
		ArrayList<String> listAVMs = new ArrayList<String>();
		listAVMs.add(RequestSubmissionInboundPortURIAVM0);
		listAVMs.add(RequestSubmissionInboundPortURIAVM1);
		
		// --------------------------------------------------------------------
		// Creating the performance controller component.
		// --------------------------------------------------------------------
		HashMap<String, String> avmsIntrospectionInboundPortURIs = new HashMap<String ,String>();
		avmsIntrospectionInboundPortURIs.put("vm0", ApplicationVM0IntrospectionInboundPortURI);
		avmsIntrospectionInboundPortURIs.put("vm1", ApplicationVM1IntrospectionInboundPortURI);
		
		HashMap<String, String> avmsManagementInboundPortURIs = new HashMap<String ,String>();
		avmsManagementInboundPortURIs.put("vm0", ApplicationVM0ManagementInboundPortURI);
		avmsManagementInboundPortURIs.put("vm1", ApplicationVM1ManagementInboundPortURI);
		
		HashMap<String, String> cp_computerServicesInboundPortURIs = new HashMap<String ,String>();
		cp_computerServicesInboundPortURIs.put("computer0", ComputerServicesInboundPortURI);
		
		this.pc = new PerformanceController(
					"pc",			// performance controller component URI
					PerformanceControllerManagementInboundPortURI,
					RequestDispatcherManagementInboundPortURIdispatcher,
					avmsIntrospectionInboundPortURIs,	
					avmsManagementInboundPortURIs,
					cp_computerServicesInboundPortURIs,
					500000, // FLOOR à changer après
					600000, // CEIL à changer après
					1  /* NB AVM à changer après */ ) ;
		this.addDeployedComponent(this.pc) ;
		this.pc.toggleTracingLogging();
		pc.logMessage("pc start");
		// --------------------------------------------------------------------
		
		
		// --------------------------------------------------------------------
		// Creating the request dispatcher component.
		// --------------------------------------------------------------------
		this.rd = new RequestDispatcherPerf(
					"rd",
					RequestDispatcherManagementInboundPortURIdispatcher,
					RequestNotificationInboundPortURIdispatcher,
					RequestSubmissionInboundPortURIdispatcher,
					RequestNotificationInboundPortURI,
					PerformanceControllerManagementInboundPortURI,
					listAVMs);
		this.addDeployedComponent(rd);
		this.rd.toggleTracing();
		this.rd.toggleLogging();
		rd.logMessage("rd start");
		// --------------------------------------------------------------------
		// Creating the request generator component.
		// --------------------------------------------------------------------
		this.rg = new RequestGenerator(
					"rg",			// generator component URI
					500.0,			// mean time between two requests
					6000000000L,	// mean number of instructions in requests
					RequestGeneratorManagementInboundPortURI,
					RequestSubmissionInboundPortURIdispatcher,
					RequestNotificationInboundPortURI) ;
		this.addDeployedComponent(rg) ;

		// Toggle on tracing and logging in the request generator to
		// follow the submission and end of execution notification of
		// individual requests.
		this.rg.toggleTracing() ;
		this.rg.toggleLogging() ;
		rg.logMessage("rg start");
		// --------------------------------------------------------------------
		// --------------------------------------------------------------------
		// Creating the integrator component.
		// --------------------------------------------------------------------
		this.integ = new IntegratorPerformanceControllerDecrease(
						ComputerServicesInboundPortURI,
						ApplicationVM0ManagementInboundPortURI,
						ApplicationVM1ManagementInboundPortURI,
						RequestGeneratorManagementInboundPortURI,
						RequestDispatcherManagementInboundPortURIdispatcher,
						PerformanceControllerManagementInboundPortURI) ;
		this.addDeployedComponent(this.integ) ;
		// --------------------------------------------------------------------

		// complete the deployment at the component virtual machine level.
		super.deploy();
	}

	// ------------------------------------------------------------------------
	// Test scenarios and main execution.
	// ------------------------------------------------------------------------

	/**
	 * execute the test application.
	 * 
	 * @param args	command line arguments, disregarded here.
	 */
	public static void	main(String[] args)
	{
		// Uncomment next line to execute components in debug mode.
		// AbstractCVM.toggleDebugMode() ;
		try {
			final TestPerformanceControllerDecrease tpc = new TestPerformanceControllerDecrease() ;
			tpc.startStandardLifeCycle(100000L) ;
			// Augment the time if you want to examine the traces after
			// the execution of the program.
			Thread.sleep(1000000L) ;
			// Exit from Java (closes all trace windows...).
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}

