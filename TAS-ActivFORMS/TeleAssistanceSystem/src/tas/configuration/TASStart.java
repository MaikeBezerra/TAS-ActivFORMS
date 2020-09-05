package tas.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import activforms.engines.GoalManagementEngine;
import activforms.engines.ModelAdaptationEngine;
import activforms.engines.ModelEvolutionEngine;
import profile.InputProfileValue;
import profile.InputProfileVariable;
import profile.ProfileExecutor;
import service.adaptation.effectors.WorkflowEffector;
import service.atomic.AtomicService;
import service.composite.CompositeServiceClient;
import service.registry.ServiceRegistry;
import tas.services.alarm.AlarmService;
import tas.services.assistance.AssistanceService;
import tas.services.assistance.AssistanceServiceCostProbe;
import tas.services.drug.DrugService;
import tas.services.medical.MedicalAnalysisService;
import tas.services.profiles.ServiceDelayProfile;
import tas.services.profiles.ServiceFailureProfile;
import tas.services.qos.MinCostQoS;
import tas.services.qos.PreferencesQoS;
import tas.services.qos.ReliabilityQoS;

public class TASStart {
	
    public static HashMap<String, AdaptationEngine> adaptationEngines = new LinkedHashMap<>();

    protected ServiceRegistry serviceRegistry;
    protected AssistanceService assistanceService;
    protected AssistanceServiceCostProbe monitor;
    protected WorkflowEffector workflowEffector;

    protected AlarmService alarm1;
    protected AlarmService alarm2;
    protected AlarmService alarm3;

    protected MedicalAnalysisService medicalAnalysis1;
    protected MedicalAnalysisService medicalAnalysis2;
    protected MedicalAnalysisService medicalAnalysis3;

    protected DrugService drugService;
    
    private boolean isStopped = false;
    private boolean isPaused = false;
    private int currentSteps;
    
    private LinkedHashMap<String,String> serviceTypes = new LinkedHashMap<>();
    private Map<String,AtomicService> atomicServices = new HashMap<>();
    private List<Class<?>> serviceProfileClasses = new ArrayList<>();

    
    public synchronized void stop(){	
    	isStopped = true;
    	monitor.setDateNowInResult();
    }
    
    private synchronized void start(){
    	isStopped = false;
    	monitor.setDateNowInResult();
    }
    
    public synchronized void pause(){
    	isPaused = true;
    }
    
    public synchronized void go(){
    	isPaused = false;
    	this.notifyAll();
    }
    
    public boolean isStopped(){
    	return isStopped;
    }
    
    public boolean isPaused(){
    	return isPaused;
    }
    
    public int getCurrentSteps(){
    	return currentSteps;
    }
     
    public LinkedHashMap<String,String> getServiceTypes(){
    	return this.serviceTypes;
    }
    
    public List<Class<?>> getServiceProfileClasses(){
    	return this.serviceProfileClasses;
    }
    
    public AtomicService getService(String name){
    	return atomicServices.get(name);
    }

    public ServiceRegistry getServiceRegistry() {
    	return serviceRegistry;
    }

    public AssistanceService getAssistanceService() {
    	return assistanceService;
    }

    public AssistanceServiceCostProbe getMonitor() {
    	return monitor;
    }

    public TASStart() {
	
		initializeTAS();

		adaptationEngines.put("No Adaptation", new DefaultAdaptationEngine());
		adaptationEngines.put("Model Adaptation", new ModelAdaptationEngine(assistanceService));
		adaptationEngines.put("Model Evolution", new ModelEvolutionEngine(assistanceService));
		adaptationEngines.put("Goal Management", new GoalManagementEngine(assistanceService));
    }
    
    public static HashMap<String, AdaptationEngine> getAdaptationEngines() {
    	return adaptationEngines;
    }

    public void initializeTAS() {

		serviceRegistry = new ServiceRegistry();
		serviceRegistry.startService();
	
		// ALarm Services
		alarm1 = new AlarmService("AlarmService1", "service.alarmService1");
		setPropertiesService(alarm1, 4.0, 0.11, 3);
		setOperationCost(alarm1, "triggerAlarm", 4.0);
		startService(alarm1);
			
		alarm2 = new AlarmService("AlarmService2", "service.alarmService2");
		setPropertiesService(alarm2, 12.0, 0.24, 5);
		setOperationCost(alarm2, "triggerAlarm", 12.0);
		startService(alarm2);
		
		alarm3 = new AlarmService("AlarmService3", "service.alarmService3");
		setPropertiesService(alarm3, 2.0, 0.18, 8);
		setOperationCost(alarm2, "triggerAlarm", 2.0);
		startService(alarm3);
		
		// Medical Analysis Services
		medicalAnalysis1 = new MedicalAnalysisService("MedicalService1", "service.medical1");
		setPropertiesService(medicalAnalysis1, 4.0, 0.12, 10);
		setOperationCost(medicalAnalysis1, "analyzeData", 4.0);
		startService(medicalAnalysis1);
		
		medicalAnalysis2 = new MedicalAnalysisService("MedicalService2", "service.medical2");
		setPropertiesService(medicalAnalysis2, 14.0, 0.07, 14);
		setOperationCost(medicalAnalysis2, "analyzeData", 14.0);
		startService(medicalAnalysis2);
		
		medicalAnalysis3 = new MedicalAnalysisService("MedicalService3", "service.medical3");
		setPropertiesService(medicalAnalysis3, 2.15, 0.18, 20);
		setOperationCost(medicalAnalysis3, "analyzeData", 2.15);
		startService(medicalAnalysis3);
		
		// Drug Services
		drugService = new DrugService("DrugService", "service.drug");
		setPropertiesService(drugService, 2.0, 0.01, 5);
		setOperationCost(drugService, "changeDoses", 5.0);
		setOperationCost(drugService, "changeDrug", 5.0);
		startService(drugService);
		
		// Assistance Service. Workflow is provided by TAS_gui through executeWorkflow method
		assistanceService = new AssistanceService("TeleAssistanceService", "service.assistance", "resources/TeleAssistanceWorkflow.txt");
		assistanceService.startService();
		assistanceService.register();
		
		monitor = new AssistanceServiceCostProbe();
		//monitor.setDateNowInResult();
		assistanceService.getCostProbe().register(monitor);
		assistanceService.getWorkflowProbe().register(monitor);
		assistanceService.addQosRequirement("ReliabilityQoS", new ReliabilityQoS());
		assistanceService.addQosRequirement("PreferencesQoS", new PreferencesQoS());
		assistanceService.addQosRequirement("CostQoS", new MinCostQoS());
		
		workflowEffector = new WorkflowEffector(assistanceService);
		
		this.addAllServices(alarm1,alarm2,alarm3,medicalAnalysis1,medicalAnalysis2,medicalAnalysis3,drugService);
		
		this.serviceProfileClasses.add(ServiceFailureProfile.class);
		this.serviceProfileClasses.add(ServiceDelayProfile.class);
    }
    
    public void addAllServices(AtomicService... services){
    	for(AtomicService service:services){
    		atomicServices.put(service.getServiceDescription().getServiceName(), service);	
    		this.serviceTypes.put(service.getServiceDescription().getServiceName(), 
    				service.getServiceDescription().getServiceType());
    	}
    	
    }

    public void stopServices() {
		serviceRegistry.stopService();
	
		alarm1.stopService();
		alarm2.stopService();
		alarm3.stopService();
	
		medicalAnalysis1.stopService();
		medicalAnalysis2.stopService();
		medicalAnalysis3.stopService();
	
		drugService.stopService();
		assistanceService.stopService();

    }

    public void executeWorkflow(String workflowPath, String profilePath) {

		CompositeServiceClient client = new CompositeServiceClient("service.assistance");
		assistanceService.setWorkflow(workflowPath);
		workflowEffector.refreshAllServices();
	
		ProfileExecutor.readFromXml(profilePath);
		if (ProfileExecutor.profile != null) {
		    int maxSteps = (int) ProfileExecutor.profile.getMaxSteps();
		    InputProfileVariable variable = ProfileExecutor.profile.getVariable("pick");
		    List<InputProfileValue> values = variable.getValues();
	
		    int patientId = (int) ProfileExecutor.profile.getVariable("patientId").getValues().get(0).getData();
		    int pick;
    
		    start();
		    Random rand = new Random();
		    
		    for (currentSteps = 0; currentSteps < maxSteps; currentSteps++) {
		   
				double probability = rand.nextDouble();
				double valueProbability = 0;
				
				for (int j = 0; j < values.size(); j++) {
				    if ((values.get(j).getRatio() + valueProbability) > probability) {
						pick = (int) values.get(j).getData();
						client.invokeCompositeService(ProfileExecutor.profile.getQosRequirement(), patientId, pick);
						break;
				    } else
				    	valueProbability = valueProbability + values.get(j).getRatio();
				}
				
		    	if(isStopped)
		    		break;
		    	
		    }
		    stop();
		    System.out.println("finish executing workflow !!!");
		}
    }
    
    private void setPropertiesService(AtomicService service, 
    					Double cost, Double failureRate, Integer responseTime) {
    	
    	service.getServiceDescription().getCustomProperties().put("Cost", cost);
		service.getServiceDescription().getCustomProperties().put("FailureRate", failureRate);
		service.getServiceDescription().setResponseTime(responseTime);
		
		addServicesProfile(service, failureRate, responseTime);
    }
    
    private void setOperationCost(AtomicService service, 
			String opName, Double cost) {
    	service.getServiceDescription().setOperationCost( opName, cost);
    }
    
    private void addServicesProfile(AtomicService service, Double failureRate, Integer delay) {
    	service.addServiceProfile(new ServiceFailureProfile(failureRate));
		service.addServiceProfile(new ServiceDelayProfile(delay));
    }
    
    private void startService(AtomicService service) {
    	service.startService();
		service.register();
    }
   
}
