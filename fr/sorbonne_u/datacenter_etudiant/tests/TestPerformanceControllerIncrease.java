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
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM.ApplicationVMPortTypes;
import fr.sorbonne_u.datacenter_etudiant.performanceController.PerformanceController;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.RequestDispatcherPerf;
import fr.sorbonne_u.datacenter_etudiant.tests.integrators.IntegratorPerformanceControllerIncrease;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;

public class				TestPerformanceControllerIncrease
extends		AbstractCVM
{
	public static final String	Computer0ServicesInboundPortURI = "c0s-ibp" ;
	public static final String	Computer0StaticStateDataInboundPortURI = "c0ss-dip" ;
	public static final String	Computer0DynamicStateDataInboundPortURI = "c0ds-dip" ;
	public static final String	Computer1ServicesInboundPortURI = "c1s-ibp" ;
	public static final String	Computer1StaticStateDataInboundPortURI = "c1ss-dip" ;
	public static final String	Computer1DynamicStateDataInboundPortURI = "c1ds-dip" ;
	
	public static final String	ApplicationVM0ManagementInboundPortURI = "avm0-ibp" ;
	public static final String	ApplicationVM1ManagementInboundPortURI = "avm1-ibp" ;
	public static final String	ApplicationVM2ManagementInboundPortURI = "avm2-ibp" ;
	public static final String	ApplicationVM3ManagementInboundPortURI = "avm3-ibp" ;
	public static final String	ApplicationVM0IntrospectionInboundPortURI = "intro0-ibp" ;
	public static final String	ApplicationVM1IntrospectionInboundPortURI = "intro1-ibp" ;
	public static final String	ApplicationVM2IntrospectionInboundPortURI = "intro2-ibp" ;
	public static final String	ApplicationVM3IntrospectionInboundPortURI = "intro3-ibp" ;
	
	
	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	public static final String	RequestSubmissionInboundPortURIAVM0 = "rsibp0" ;
	public static final String	RequestSubmissionInboundPortURIAVM1 = "rsibp1" ;
	public static final String	RequestSubmissionInboundPortURIAVM2 = "rsibp2" ;
	public static final String	RequestSubmissionInboundPortURIAVM3 = "rsibp3" ;
	public static final String	RequestNotificationInboundPortURI = "rnibp" ;
	
	public static final String	RequestDispatcherManagementInboundPortURIdispatcher = "rdmip_disp" ;
	public static final String	RequestSubmissionInboundPortURIdispatcher = "rsibp_disp" ;
	public static final String	RequestNotificationInboundPortURIdispatcher = "rnibp_disp" ;
	
	public static final String  PerformanceControllerManagementInboundPortURI = "pcmip";

	/** 	Computer monitor component.										*/
	protected ComputerMonitor						c0m ;
	protected ComputerMonitor						c1m ;
	
	/** 	Application virtual machine component.							*/
	protected ApplicationVM							vm ;
	protected ApplicationVM							vm1 ;
	protected ApplicationVM							vm2 ;
	protected ApplicationVM							vm3 ;
	
	/** 	Request generator component.										*/
	protected RequestGenerator						rg ;
	
	/**		Request dispatcher component.								*/
	protected RequestDispatcherPerf 					rd ;
	//protected RequestDispatcher 					rd;
	
	/**     Performance Controller component. 							*/
	protected PerformanceController 				pc ;
	
	/** Integrator component.											*/
	protected IntegratorPerformanceControllerIncrease								integ ;

	// ------------------------------------------------------------------------
	// Component virtual machine constructors
	// ------------------------------------------------------------------------

	public				TestPerformanceControllerIncrease()
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
		String computer0URI = "computer0" ;
		int numberOfProcessors = 2 ;
		int numberOfCores = 2 ;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
		admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000) ;	// and at 3 GHz
		Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
		processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips
		Computer c0 = new Computer(
							computer0URI,
							admissibleFrequencies,
							processingPower,  
							1500,		// Test scenario 1, frequency = 1,5 GHz
							// 3000,	// Test scenario 2, frequency = 3 GHz
							1500,		// max frequency gap within a processor
							numberOfProcessors,
							numberOfCores,
							Computer0ServicesInboundPortURI,
							Computer0StaticStateDataInboundPortURI,
							Computer0DynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(c0) ;
		c0.toggleLogging() ;
		c0.toggleTracing() ;
		c0.logMessage("computer0 start");
		// --------------------------------------------------------------------

		
		
		// --------------------------------------------------------------------
		// Create the computer monitor component and connect its to ports
		// with the computer component.
		// --------------------------------------------------------------------
		this.c0m = new ComputerMonitor(computer0URI,
									 true,
									 Computer0StaticStateDataInboundPortURI,
									 Computer0DynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(this.c0m) ;
		// --------------------------------------------------------------------
		
		// --------------------------------------------------------------------
		// Create and deploy a computer component with its 2 processors and
		// each with 2 cores.
		// --------------------------------------------------------------------
		String computer1URI = "computer1" ;
		Computer c1 = new Computer(
							computer1URI,
							admissibleFrequencies,
							processingPower,  
							1500,		// Test scenario 1, frequency = 1,5 GHz
							// 3000,	// Test scenario 2, frequency = 3 GHz
							1500,		// max frequency gap within a processor
							numberOfProcessors,
							numberOfCores,
							Computer1ServicesInboundPortURI,
							Computer1StaticStateDataInboundPortURI,
							Computer1DynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(c1) ;
		c1.toggleLogging() ;
		c1.toggleTracing() ;
		c1.logMessage("computer1 start");
		// --------------------------------------------------------------------

		
		
		// --------------------------------------------------------------------
		// Create the computer monitor component and connect its to ports
		// with the computer component.
		// --------------------------------------------------------------------
		this.c1m = new ComputerMonitor(computer1URI,
									 true,
									 Computer1StaticStateDataInboundPortURI,
									 Computer1DynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(this.c1m) ;
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
		this.vm2 = new ApplicationVM("vm2",	// application vm component URI
								    ApplicationVM2ManagementInboundPortURI,
								    ApplicationVM2IntrospectionInboundPortURI,
								    RequestSubmissionInboundPortURIAVM2,
								    RequestNotificationInboundPortURIdispatcher) ;
		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.addDeployedComponent(this.vm2) ;
		this.vm2.toggleTracing() ;
		this.vm2.toggleLogging() ;
		vm2.logMessage("vm2 start");
		// --------------------------------------------------------------------
		this.vm3 = new ApplicationVM("vm3",	// application vm component URI
								    ApplicationVM3ManagementInboundPortURI,
								    ApplicationVM3IntrospectionInboundPortURI,
								    RequestSubmissionInboundPortURIAVM3,
								    RequestNotificationInboundPortURIdispatcher) ;
		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.addDeployedComponent(this.vm3) ;
		this.vm3.toggleTracing() ;
		this.vm3.toggleLogging() ;
		vm3.logMessage("vm3 start");
		// --------------------------------------------------------------------
		ArrayList<String> listAVMs_libre = new ArrayList<String>();
		listAVMs_libre.add("vm2");
		listAVMs_libre.add("vm3");
		
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
		
		HashMap<String, Map<ApplicationVMPortTypes, String>> listAVMs_libre_ports = new HashMap<String, Map<ApplicationVMPortTypes, String>>();
		HashMap<ApplicationVMPortTypes, String> avm2Ports = new HashMap<ApplicationVMPortTypes, String>();
		avm2Ports.put(ApplicationVMPortTypes.REQUEST_SUBMISSION, RequestSubmissionInboundPortURIAVM2);
		avm2Ports.put(ApplicationVMPortTypes.INTROSPECTION, ApplicationVM2IntrospectionInboundPortURI);
		avm2Ports.put(ApplicationVMPortTypes.MANAGEMENT, ApplicationVM2IntrospectionInboundPortURI);
		listAVMs_libre_ports.put("vm2",avm2Ports);
		HashMap<ApplicationVMPortTypes, String> avm3Ports = new HashMap<ApplicationVMPortTypes, String>();
		avm3Ports.put(ApplicationVMPortTypes.REQUEST_SUBMISSION, RequestSubmissionInboundPortURIAVM3);
		avm3Ports.put(ApplicationVMPortTypes.INTROSPECTION, ApplicationVM3IntrospectionInboundPortURI);
		avm3Ports.put(ApplicationVMPortTypes.MANAGEMENT, ApplicationVM3IntrospectionInboundPortURI);
		listAVMs_libre_ports.put("vm3",avm3Ports);
		
		
		// --------------------------------------------------------------------
		// Creating the performance controller component.
		// --------------------------------------------------------------------
		HashMap<String, String> avmsIntrospectionInboundPortURIs = new HashMap<String ,String>();
		avmsIntrospectionInboundPortURIs.put("vm0", ApplicationVM0IntrospectionInboundPortURI);
		avmsIntrospectionInboundPortURIs.put("vm1", ApplicationVM1IntrospectionInboundPortURI);
		
		HashMap<String, String> avmsManagementInboundPortURIs = new HashMap<String ,String>();
		avmsManagementInboundPortURIs.put("vm0", ApplicationVM0IntrospectionInboundPortURI);
		avmsManagementInboundPortURIs.put("vm1", ApplicationVM1ManagementInboundPortURI);
		
		HashMap<String, String> cp_computerServicesInboundPortURIs = new HashMap<String ,String>();
		cp_computerServicesInboundPortURIs.put(computer0URI, Computer0ServicesInboundPortURI);
		cp_computerServicesInboundPortURIs.put(computer1URI, Computer1ServicesInboundPortURI);
		
		this.pc = new PerformanceController(
					"pc",			// performance controller component URI
					PerformanceControllerManagementInboundPortURI,
					RequestDispatcherManagementInboundPortURIdispatcher,
					avmsIntrospectionInboundPortURIs,	
					avmsManagementInboundPortURIs,
					cp_computerServicesInboundPortURIs,
					2000, // FLOOR à changer après
					3000, // CEIL à changer après
					listAVMs_libre,
					listAVMs_libre_ports,
					2  /* NB AVM à changer après */ ) ;
		this.addDeployedComponent(this.pc) ;
		this.pc.toggleTracingLogging();
		pc.logMessage("pc start");
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
		this.integ = new IntegratorPerformanceControllerIncrease(
						Computer0ServicesInboundPortURI,
						Computer1ServicesInboundPortURI,
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
			final TestPerformanceControllerIncrease tpc = new TestPerformanceControllerIncrease() ;
			tpc.startStandardLifeCycle(50000L) ;
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

