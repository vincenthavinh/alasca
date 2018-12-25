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
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.datacenter_etudiant.tests.integrators.IntegratorRequestDispatcher_multi_avms;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;

public class				TestRequestDispatcher_multi_amvs
extends		AbstractCVM
{
	public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
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

	/** 	Computer monitor component.										*/
	protected ComputerMonitor						cm ;
	/** 	Application virtual machine component.							*/
	protected ApplicationVM							vm0 ;
	protected ApplicationVM							vm1 ;
	protected ApplicationVM							vm2 ;
	protected ApplicationVM							vm3 ;
	/** 	Request generator component.										*/
	protected RequestGenerator						rg ;
	
	/**		Request dispatcher component.								*/
	protected RequestDispatcher 					rd ;
	
	/** Integrator component.											*/
	protected IntegratorRequestDispatcher_multi_avms								integ ;

	// ------------------------------------------------------------------------
	// Component virtual machine constructors
	// ------------------------------------------------------------------------

	public				TestRequestDispatcher_multi_amvs()
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
		int numberOfProcessors = 4 ;
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
							ComputerServicesInboundPortURI,
							ComputerStaticStateDataInboundPortURI,
							ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(c) ;
		c.toggleLogging() ;
		c.toggleTracing() ;
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
		this.vm0 = new ApplicationVM("vm0",	// application vm component URI
								    ApplicationVM0ManagementInboundPortURI,
								    ApplicationVM0IntrospectionInboundPortURI,
								    RequestSubmissionInboundPortURIAVM0,
								    RequestNotificationInboundPortURIdispatcher) ;
		this.addDeployedComponent(this.vm0) ;
		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.vm0.toggleTracing() ;
		this.vm0.toggleLogging() ;
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
		// --------------------------------------------------------------------
		this.vm2 = new ApplicationVM("vm2",	// application vm component URI
								    ApplicationVM2ManagementInboundPortURI,
								    ApplicationVM2IntrospectionInboundPortURI,
								    RequestSubmissionInboundPortURIAVM2,
								    RequestNotificationInboundPortURIdispatcher) ;
		this.addDeployedComponent(this.vm2) ;
		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.vm2.toggleTracing() ;
		this.vm2.toggleLogging() ;
		// --------------------------------------------------------------------
		this.vm3 = new ApplicationVM("vm3",	// application vm component URI
					    ApplicationVM3ManagementInboundPortURI,
					    ApplicationVM3IntrospectionInboundPortURI,
					    RequestSubmissionInboundPortURIAVM3,
					    RequestNotificationInboundPortURIdispatcher) ;
		this.addDeployedComponent(this.vm3) ;
		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.vm3.toggleTracing() ;
		this.vm3.toggleLogging() ;
		// --------------------------------------------------------------------
		
		ArrayList<String> listAVMs = new ArrayList<String>();
		listAVMs.add(RequestSubmissionInboundPortURIAVM0);
		listAVMs.add(RequestSubmissionInboundPortURIAVM1);
		listAVMs.add(RequestSubmissionInboundPortURIAVM2);
		listAVMs.add(RequestSubmissionInboundPortURIAVM3);
		
		ArrayList<String> listAVMsIntrospection = new ArrayList<String>();
		listAVMsIntrospection.add(ApplicationVM0IntrospectionInboundPortURI);
		listAVMsIntrospection.add(ApplicationVM1IntrospectionInboundPortURI);
		listAVMsIntrospection.add(ApplicationVM2IntrospectionInboundPortURI);
		listAVMsIntrospection.add(ApplicationVM3IntrospectionInboundPortURI);
		// --------------------------------------------------------------------
		// Creating the request dispatcher component.
		// --------------------------------------------------------------------
		this.rd = new RequestDispatcher(
					"rd",
					RequestDispatcherManagementInboundPortURIdispatcher,
					RequestNotificationInboundPortURIdispatcher,
					RequestSubmissionInboundPortURIdispatcher,
					RequestNotificationInboundPortURI,
					listAVMs);
		this.addDeployedComponent(rd);
		this.rd.toggleTracing();
		this.rd.toggleLogging();
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
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Creating the integrator component.
		// --------------------------------------------------------------------
		this.integ = new IntegratorRequestDispatcher_multi_avms(
							ComputerServicesInboundPortURI,
							ApplicationVM0ManagementInboundPortURI,
							ApplicationVM1ManagementInboundPortURI,
							ApplicationVM2ManagementInboundPortURI,
							ApplicationVM3ManagementInboundPortURI,
							RequestGeneratorManagementInboundPortURI,
							RequestDispatcherManagementInboundPortURIdispatcher) ;
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
			final TestRequestDispatcher_multi_amvs trd = new TestRequestDispatcher_multi_amvs() ;
			trd.startStandardLifeCycle(100000L) ;
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
