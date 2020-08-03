package tas.adaptation.simple;

import service.adaptation.probes.interfaces.WorkflowProbeInterface;
import service.auxiliary.ServiceDescription;
import tas.services.log.Log;

/**
 * 
 * @author M. Usman Iftikhar
 *
 */
public class MyProbe implements WorkflowProbeInterface {

	MyAdaptationEngine myAdaptationEngine;
	
	public int falhasAdaptacaoToleradas = 0;
	public int falhasAdaptacaoNaoToleradas = 0;
	
		
	public void connect(MyAdaptationEngine myAdaptationEngine) {
	    this.myAdaptationEngine = myAdaptationEngine;
	}

	@Override
	public void workflowStarted(String qosRequirement, Object[] params) {
	    System.err.println("Workflow Started monitoring");
	    Log.addLog("WorkflowStarted", "Workflow Started monitoring");
	}

	@Override
	public void workflowEnded(Object result, String qosRequirement, Object[] params) {
	    System.err.println("Workflow Ended");
	}

	@Override
	public void serviceOperationInvoked(ServiceDescription service, String opName, Object[] params) {

	}

	@Override
	public void serviceOperationReturned(ServiceDescription service, Object result, String opName, Object[] params) {

	}

	@Override
	public void serviceOperationTimeout(ServiceDescription service, String opName, Object[] params) {
	    System.err.println("Simple Adaptation! Service Failed: " + service.getServiceName() );
		//System.err.println("Numero de operações invocadas " + service.getOperationList().size());
	    falhasAdaptacaoToleradas++;
	    //Falha de Adaptação

	    // Remove service from cache
	    myAdaptationEngine.handleServiceFailure(service, opName);
	}
	
	@Override
	public void serviceNotFound(String serviceType, String opName){
	    System.err.println(serviceType + opName + "Not found");
	    
	    //Falha de Adaptação
	    falhasAdaptacaoNaoToleradas++;
	    
	    myAdaptationEngine.handleServiceNotFound(serviceType, opName);
	}

	public int getFalhasAdaptacaoToleradas() {
		return falhasAdaptacaoToleradas;
	}

	public void setFalhasAdaptacaoToleradas(int falhasAdaptacaoToleradas) {
		this.falhasAdaptacaoToleradas = falhasAdaptacaoToleradas;
	}

	public int getFalhasAdaptacaoNaoToleradas() {
		return falhasAdaptacaoNaoToleradas;
	}

	public void setFalhasAdaptacaoNaoToleradas(int falhasAdaptacaoNaoToleradas) {
		this.falhasAdaptacaoNaoToleradas = falhasAdaptacaoNaoToleradas;
	}


	
	
}
