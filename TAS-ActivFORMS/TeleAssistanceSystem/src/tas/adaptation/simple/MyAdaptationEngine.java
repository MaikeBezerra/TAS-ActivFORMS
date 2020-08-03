package tas.adaptation.simple;

import service.adaptation.effectors.WorkflowEffector;
import service.auxiliary.ServiceDescription;

/**
 * 
 * @author M. Usman Iftikhar
 *
 */
public class MyAdaptationEngine {

    private WorkflowEffector myEffector;

    public MyAdaptationEngine(MyProbe myProbe, WorkflowEffector workflowEffector) {
	myProbe.connect(this);
	this.myEffector = workflowEffector;
    }

    public void handleServiceFailure(ServiceDescription service, String opName) {
	this.myEffector.removeService(service);
	System.err.println("Serviço falhou mas lidamos com a falha e o sistema não quebrou");
    }

    public void handleServiceNotFound(String serviceType, String opName) {
	myEffector.refreshAllServices(serviceType, opName);
	System.err.println("Serviço não foi encontrado, isso também é uma falha");

    }
}
