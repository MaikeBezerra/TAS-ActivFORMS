/**
 * 
 */
package tas.services.assistance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import service.adaptation.probes.interfaces.CostProbeInterface;
import service.adaptation.probes.interfaces.WorkflowProbeInterface;
import service.auxiliary.ServiceDescription;
import service.auxiliary.TimeOutError;
import service.utility.SimClock;
import tas.services.log.Log;

/**
 * @author yfruan
 * @email  ry222ad@student.lnu.se
 *
 */
public class AssistanceServiceCostProbe implements WorkflowProbeInterface,CostProbeInterface{

    private static String resultFilePath="results"+File.separator+"result.csv";
    private static String resultsFilePath="results"+File.separator+"results.csv";
    private double totalCost=0;
    
    private StringBuilder resultBuilder;
    public int workflowInvocationCount = 0;
    private Map<String, Double> delays = new HashMap<>();
    private Map<String, Double> latencies = new HashMap<>();
        
    static{
    	File file = new File(resultFilePath);
    	if(file.exists() && !file.isDirectory()) {
    		file.delete();
    	}
    }
    
    public void reset(){
    	File file = new File(resultFilePath);
    	if(file.exists() && !file.isDirectory()) {
    		file.delete();
    	}
    	workflowInvocationCount=0;
    	totalCost = 0;
    	delays = new HashMap<>();
    	latencies = new HashMap<>();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see service.adaptation.Probe#workflowStarted(java.lang.String, java.lang.Object[])
     */
    @Override
    public void workflowStarted(String qosRequirement, Object[] params) {
    	System.out.println("Probe: workflowStarted");
	    Log.addLog("WorkflowStarted", "Workflow Started monitoring");
    	resultBuilder = new StringBuilder();
    	totalCost=0;
    	workflowInvocationCount++;
    }

    /*
     * (non-Javadoc)
     * 
     * @see service.adaptation.Probe#workflowEnded(java.lang.Object, java.lang.String, java.lang.Object[])
     */
    @Override
    public void workflowEnded(Object result, String qosRequirement, Object[] params) {
    	System.out.println("Probe: workflowEnded");
    	if(result instanceof TimeOutError){
    		System.out.println("WorkflowError!!!");
        	resultBuilder.append(workflowInvocationCount+","+"AssistanceService"+",false,"+totalCost+"\n");
    	}
    	else
        	resultBuilder.append(workflowInvocationCount+","+"AssistanceService"+",true,"+totalCost+"\n");
    	
    	fileWriter(resultFilePath);
    	fileWriter(resultsFilePath);
    }
	
    /*
     * (non-Javadoc)
     * 
     * @see service.adaptation.Probe#timeout(service.auxiliary.ServiceDescription, java.lang.String, java.lang.Object[])
     */
    @Override
    public void serviceOperationTimeout(ServiceDescription service, String opName, Object[] params) {
    	System.out.println("Probe: timeout");
    	
    	String serviceName = service.getServiceName();
    	Double begin = delays.get(serviceName);
		Double end = SimClock.getCurrentTime();
		Double latency = latencies.get(serviceName);
    	resultBuilder.append(workflowInvocationCount+","+service.getServiceName()+
    						",false, 0.0," + begin +  "," +(end-begin)+ 
    						"," + latency +"\n");
    }

	@Override
	public void serviceCost(ServiceDescription service, String opName, double cost) {
		String serviceName = service.getServiceName();
		System.out.println("Serivice Cost: "+cost);
		Double begin = delays.get(serviceName);
		Double end = SimClock.getCurrentTime();
		Double latency = latencies.get(serviceName);
		
		totalCost=totalCost+cost;
    	
		// Ivocation | service | Call sucess? | cost | startupTime | responseTime | latency
		resultBuilder.append(workflowInvocationCount +"," + serviceName +
    						",true," + cost + "," + begin +  "," +(end-begin)+ 
    						"," + latency +"\n");
	}

	@Override
	public void serviceOperationInvoked(ServiceDescription service,
			String opName, Object[] params) {
		
		String serviceName = service.getServiceName();
		double time = SimClock.getCurrentTime();
		
		
		if (latencies.containsKey(serviceName) ) {
			latencies.put(serviceName, time - delays.get(serviceName));
		} else {
			latencies.put(serviceName, time);
		}
		
		delays.put(serviceName, time);
	}

	@Override
	public void serviceOperationReturned(ServiceDescription service,
			Object result, String opName, Object[] params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serviceNotFound(String serviceType, String opName) {
	    // TODO Auto-generated method stub
	    
	}
	
	public void setDateNowInResult() {
		
    	SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();  
		
		resultBuilder = new StringBuilder();
		resultBuilder.append("Date,"+ dataFormat.format(date).toString() + "\n");
		
		fileWriter(resultFilePath);
    	fileWriter(resultsFilePath);
	}
	
	private void fileWriter(String path) {
		try(
    		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
    	    out.println(resultBuilder.toString());
    	}catch (IOException e) {
    		e.printStackTrace();
    	}
	}
}
