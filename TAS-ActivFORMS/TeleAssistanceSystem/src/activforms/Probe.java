package activforms;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import activforms.engine.ActivFORMSEngine;
import service.adaptation.probes.interfaces.WorkflowProbeInterface;
import service.auxiliary.ServiceDescription;
import service.auxiliary.TimeOutError;

public class Probe implements WorkflowProbeInterface {
	
	public static int falhasAdaptacaoToleradas = 0;
	public static int falhasAdaptacaoNaoToleradas = 0;
	
	public static int adaptacoesCorretas = 0;
	public static int adaptacoesIncorretas = 0;
	
	public static int interrupcoesPorAdpt = 0;
	
	public static int probeDowntime = 0;
	public static int probeInterruptions = 0;
	
	public static long failureInitialInstant = 0;
	public static long finalInstantOfFailureRecovery = 0;
	
	public static ArrayList<Long> initials = new ArrayList<Long>();
	public static ArrayList<Long> finals = new ArrayList<Long>();
	
	public static boolean flagFalhou = false;
	public static boolean flagRecuperou = false;
	
	public static int countRecovery = 0;
	
	
	public static ServiceDescription serv = null;

	ActivFORMSEngine engine;
	int serviceSelected, serviceSucceed, serviceFailed, AS_Started, AS_Succeed, AS_Failed;
	AtomicBoolean adaptationDone = new AtomicBoolean(false);
	
	public Probe(ActivFORMSEngine engine) {
		this.engine = engine;

		serviceSelected = engine.getChannel("service_Selected");
		serviceSucceed = engine.getChannel("service_Succeed");
		serviceFailed = engine.getChannel("service_Failed");

		AS_Started = engine.getChannel("AS_started");
		AS_Succeed = engine.getChannel("AS_succeed");
		AS_Failed = engine.getChannel("AS_failed");
	}

	@Override
	public void serviceOperationInvoked(ServiceDescription service, String opName, Object[] params) {
		engine.send(serviceSelected, "serviceId = " + service.getRegisterID());
		serv = service;
	}

	@Override
	public void serviceOperationReturned(ServiceDescription service, Object result, String opName, Object[] params) {
		engine.send(serviceSucceed, "serviceId = " + service.getRegisterID());
	}

	long times[] = new long[1000];
	int i = 0, j = 0;

	@Override
	public void serviceOperationTimeout(ServiceDescription service, String opName, Object[] params) {
		adaptationDone.set(false);
		failureInitialInstant = System.currentTimeMillis();
		initials.add(failureInitialInstant);
		long start = System.currentTimeMillis();
		engine.send(serviceFailed, "serviceId=" + service.getRegisterID());
		synchronized (engine) {
			System.err.println("ActiveForms Service Failed. Waiting " + service.getServiceName());
			falhasAdaptacaoToleradas++;
			adaptacoesIncorretas++;
			interrupcoesPorAdpt++;
			flagFalhou = true;
			try {
				while (!adaptationDone.get())
					engine.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long end = System.currentTimeMillis();
			times[i++] = end - start;
			probeDowntime += end - start;
			probeInterruptions++;
			finalInstantOfFailureRecovery = System.currentTimeMillis();
			finals.add(finalInstantOfFailureRecovery);
			if (i == 1000) {
				System.err.println("done 1000 times.." + j);
				i = 0;

				try {
					PrintWriter writer = new PrintWriter("/Users/muiadmin/Documents/times" + j++ + ".txt", "UTF-8");
					writer.println(Arrays.toString(times));
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Verification times: " + Arrays.toString(times));
					falhasAdaptacaoNaoToleradas++;
					probeInterruptions++;
				}

				System.err.println("VerificationTime printed");

			}
		}
	}

	@Override
	public void workflowStarted(String qosRequirement, Object[] params) {
		engine.send(AS_Started);
	}

	@Override
	public void workflowEnded(Object result, String qosRequirement, Object[] params) {
		if (result instanceof TimeOutError) {
			engine.send(AS_Failed);
			falhasAdaptacaoNaoToleradas++;
			adaptacoesIncorretas++;
			flagRecuperou = false;
		} else {
			engine.send(AS_Succeed);
			adaptacoesCorretas++;
			flagRecuperou = true;
		}
		
		numRecoveryFails();
	}

	public void setAdaptationDone(boolean adaptationDone) {
		this.adaptationDone.set(adaptationDone);
		System.out.println("ADAPTAÇÃO REALIZADA");
		adaptacoesCorretas++;
	}

	@Override
	public void serviceNotFound(String serviceType, String opName) {
		System.out.println("SERVIÇO NÃO ENCONTRADO, ADAPTAÇÃO FALHOU");
		falhasAdaptacaoNaoToleradas++;
		adaptacoesIncorretas++;
	}
	
	public static ArrayList<Long> adptInitRecoveryTime() {
		return initials;
	}
	
	public static ArrayList<Long> adptFinalRecoveryTime() {
		return finals;
	}
	
	public static int numRecoveryFails() {
		if(flagFalhou == true && flagFalhou == true) {
			countRecovery++;
			flagFalhou = false;
			flagRecuperou = false;
			
		}
		return 0;
	}
	
}
