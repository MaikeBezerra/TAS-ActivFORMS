package application.view.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;

import activforms.engine.ActivFORMSEngine;
import activforms.engines.GoalManagementEngine;
import activforms.engines.ModelEvolutionEngine;
import activforms.goalmanagement.ActiveModelProbe;
import application.MainGui;
import application.model.CostEntry;
import application.model.PerformanceEntry;
import application.model.ReliabilityEntry;
import application.utility.FileManager;
import application.utility.NodeVisitor;
import application.view.LatencyChartView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.gui.utility.DialogResult;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import profile.InputProfile;
import profile.ProfileExecutor;
import profile.Requirement;
import service.auxiliary.ExecutionThread;
import service.auxiliary.ServiceDescription;
import service.composite.CompositeService;
import service.registry.ServiceRegistry;
import service.workflow.ast.rspLexer;
import service.workflow.ast.rspParser;
import tas.configuration.AdaptationEngine;
import tas.configuration.TASStart;
import tas.services.assistance.AssistanceServiceCostProbe;

public class ApplicationController implements Initializable {
	
	Stage primaryStage;  
	
    // for generating kinds of charts
    ChartController chartController;
    
    // for generating kinds of table views
    TableViewController tableViewController;
    
    LatencyChartView latencyChart;

    String baseDir = "";
    
    String workflowPath    = baseDir + "resources" + File.separator + "TeleAssistanceWorkflow.txt";
    String profileDirPath  = baseDir + "resources" + File.separator + "files" + File.separator;
    String resourceDirPath = baseDir + "resources" + File.separator;
    
    String resultFilePath = baseDir + "results" + File.separator + "result.csv";
    String logFilePath    = baseDir + "results" + File.separator + "log.csv";
    String resultDirPath  = baseDir + "results" + File.separator;
    

    ScheduledExecutorService scheduExec = Executors.newScheduledThreadPool(5);

    CompositeService compositeService;
    AssistanceServiceCostProbe probe;
    ServiceRegistry serviceRegistry;
    TASStart tasStart;

    Set<Button> profileRuns = new HashSet<>();
    int maxSteps;
    String activeModel;
    Map<String, AdaptationEngine> adaptationEngines;
    Map<String, AnchorPane> servicePanes = new ConcurrentHashMap<>();

    @FXML
    ListView<AnchorPane> serviceListView;
    
    @FXML
    ListView<AnchorPane> profileListView;
    
    @FXML
    TextArea workflowTextArea;

    @FXML
    TableView<ReliabilityEntry> reliabilityTableView;

    @FXML
    TableView<CostEntry> costTableView;
    
    @FXML
    TableView<PerformanceEntry> performanceTableView;
    
    @FXML
    MenuItem openWorkflowMenuItem;

    @FXML
    MenuItem openServicesMenuItem;
    
    @FXML
    MenuItem configureMenuItem;
    
    @FXML
    MenuItem openLogMenuItem;

    @FXML
    MenuItem openProfileMenuItem;

    @FXML
    MenuItem saveRunMenuItem;
    
    @FXML
    MenuItem saveLogMenuItem;

    @FXML
    AnchorPane reliabilityChartPane;

    @FXML
    AnchorPane costChartPane;
    
    @FXML
    AnchorPane performanceChartPane;
    
    @FXML
    AnchorPane invCostChartPane;
    
    @FXML
    AnchorPane avgReliabilityChartPane;
    
    @FXML
    AnchorPane avgCostChartPane;
    
    @FXML
    AnchorPane avgPerformanceChartPane;
    
    @FXML
    AnchorPane invRateChartPane;
    
    @FXML
    AnchorPane latencyChartPane;
    
    @FXML
    ScrollPane serviceScrollPane;

    @FXML
    ScrollPane profileScrollPane;
    
    @FXML
    MenuItem openRunMenuItem;

    @FXML
    Button aboutButton;
    
    @FXML
    MenuItem saveReliabilityGraphMenuItem;

    @FXML
    MenuItem saveCostGraphMenuItem;
    
    @FXML
    MenuItem saveInvCostGraphMenuItem;
    
    @FXML
    MenuItem savePerformanceGraphMenuItem;

    @FXML
    MenuItem helpMenuItem;
    
    @FXML 
    MenuItem exampleScenariosMenuItem;
    
    @FXML
    ToolBar toolBar;
    
    @FXML
    Button configureButton;
    
    @FXML
    AnchorPane canvasPane;
    
    @FXML
    TextField sliceTextField;
    
    @FXML
    MenuItem saveInvMenuItem;
    
    @FXML
    MenuItem openActivFORMS;
    
    @FXML
    MenuItem openModelEvolution;
    
    @FXML
    MenuItem openGoalManagement;
    
    @FXML
    Window window;
    
    Object preAdaptation;
    Object currentAdaptation="No Adaptation";
    
    private String currentPath;
    
    ProgressBar progressBar;
    Label invocationLabel;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	try {
    	    String content = new String(Files.readAllBytes(Paths.get(workflowPath)));
    	    workflowTextArea.setText(content);
    	    
    	    this.generateSequenceDiagram(workflowPath);
    	    
    	} catch (IOException e) {
    	    e.printStackTrace();  
    	}

    	this.fillProfiles();
    	this.setButton();

    	scheduExec.scheduleAtFixedRate(new Runnable() {
    	    @Override
    		public void run() {
    	    	
    			Set<String> services = compositeService.getCache().getServices();
    			Set<String> registeredServices = servicePanes.keySet();

    			for (String service : registeredServices) {
    			    
    			    Platform.runLater(new Runnable() {
	    				@Override
	    				public void run() {
	    					
	    					Circle circle = (Circle) servicePanes.get(service).getChildren().get(0);
	    					
	    				    if(Objects.nonNull(services) && services.contains(service))
	    					    circle.setFill(Color.GREEN);
	    				    else
	    					    circle.setFill(Color.DARKRED);
	    				}
    			    });
    			}
    	    }
    	}, 0, 1000, TimeUnit.MILLISECONDS);
    } 
       
    private void generateSequenceDiagram(String workflowPath){
    	
    	canvasPane.getChildren().clear();
    	
		try {
			rspLexer lexer = new rspLexer(new ANTLRFileStream(workflowPath));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			rspParser parser = new rspParser(tokens);
			NodeVisitor visitor=new NodeVisitor();
			visitor.setCanvasPane(canvasPane);
			visitor.visit((CommonTree)parser.start().getTree());
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
    }
    
    public void setPrimaryStage(Stage primaryStage) {
    	this.primaryStage = primaryStage;
    }

    public void setConfigurations(Map<String, AdaptationEngine> adaptationEngines){
    	this.adaptationEngines=adaptationEngines;
    	this.addItems();
    }
    
    public void setCompositeService(CompositeService service) {
    	this.compositeService = service;
    }

    public void setProbe(AssistanceServiceCostProbe probe) {
    	this.probe = probe;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
    	this.serviceRegistry = serviceRegistry;
    	openServicesMenuItem.fire();
    }
    
    public void setTasStart(TASStart tasStart) {
    	this.tasStart = tasStart;
      	chartController = new ChartController(reliabilityChartPane, costChartPane,performanceChartPane
      			,invCostChartPane, avgReliabilityChartPane, avgCostChartPane, avgPerformanceChartPane
      			,invRateChartPane, tasStart.getServiceTypes());
    	tableViewController = new TableViewController(reliabilityTableView,costTableView,performanceTableView);
    	
    	this.latencyChart = new LatencyChartView(performanceChartPane);
    }

    private void addItems() {
    	final ToggleGroup group=new ToggleGroup();
    	boolean selected = true;
    	for(String key : adaptationEngines.keySet()){
    	    ToggleButton button = new ToggleButton(key);
    	    button.setToggleGroup(group);
    	    button.setUserData(key);
    	    if(selected){
	    		button.setSelected(true);
	    		selected = false;
    	    }
    	    
    	    Separator separator = new Separator();
    	    separator.setPrefWidth(27);
    	    toolBar.getItems().addAll(button,separator);
    	}
    	
    	group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
    	    @Override    
    		public void changed(ObservableValue<? extends Toggle> observable,
    	            final Toggle oldValue, final Toggle newValue) {
    	    	if ((newValue == null)) {
    	    		Platform.runLater(new Runnable() {
    	    			@Override
    	    			public void run() {
    	    				group.selectToggle(oldValue);
    	    			}
    	    		});
    	    	}
    	      
    	    	currentAdaptation=newValue.getUserData();
    	    }
    	});
    	
    	progressBar = new ProgressBar(0);
    	invocationLabel = new Label();
    	toolBar.getItems().addAll(new Label("Progress "), progressBar,invocationLabel);
    }
    
    private void setButton() {
    	
    	sliceTextField.textProperty().addListener(new ChangeListener<String>() {
    	    @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
    	        if (newValue.matches("\\d*")) {
    	        	
    	        } else {
    	        	sliceTextField.setText(oldValue);
    	        }
    	    }
    	});
    	
    	sliceTextField.setOnKeyPressed(new EventHandler<KeyEvent>(){
    		@Override
    		public void handle(KeyEvent event){
    			if (event.getCode().equals(KeyCode.ENTER)){
    				chartController.generateAvgCharts(resultFilePath, tasStart.getCurrentSteps(),Integer.parseInt(sliceTextField.getText()));
    		    }
    		 }
    	});
    	
    	openWorkflowMenuItem.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent event) {
	    		FileChooser fileChooser = new FileChooser();
	    		fileChooser.setInitialDirectory(new File(resourceDirPath));
	    		fileChooser.setTitle("Select workflow");
	    		FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("Add Files(*.txt)", "*.txt");
	    		fileChooser.getExtensionFilters().add(extension);
	    		File file = fileChooser.showOpenDialog(primaryStage);
	    		if (file != null) {
	    		    System.out.println(file.getPath());
	    		    try {
		    			String content = new String(Files.readAllBytes(file.toPath()));
		    			workflowPath = file.getPath();
		    			workflowTextArea.setText(content);
		    			
		    			generateSequenceDiagram(workflowPath);
		
		    			Platform.runLater(new Runnable() {
		    			    @Override
		    			    public void run() {
		    			    	for (Button runButton : profileRuns)
		    			    		runButton.setDisable(false);
	    			    	}
		    			});
	
	    		    } catch (IOException e) {
	    		    	e.printStackTrace();
	    		    }
	    		}
    	    }
    	});
    	
    	openServicesMenuItem.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent event) {
    	    	List<String> services = serviceRegistry.getAllServices();
    	    	for (String service : services) {
    	    		if (!service.equals("TeleAssistanceService"))
    	    			servicePanes.put(service, addService(service,false));
    	
    	    	}
    	    }
    	});
    	
    	configureMenuItem.setOnAction(event->{	
    		try{
    	
    			FXMLLoader loader = new FXMLLoader();
    			loader.setLocation(MainGui.class.getResource("view/configureDialog.fxml"));
    			GridPane configurePane = (GridPane) loader.load();

    			Stage dialogStage = new Stage();
    			dialogStage.setTitle("ReSeP Configuration");
        
    			ConfigureController controller=(ConfigureController)loader.getController();
    			controller.setStage(dialogStage);
    		
    			Scene dialogScene = new Scene(configurePane);
    			dialogScene.getStylesheets().add(MainGui.class.getResource("view/application.css").toExternalForm());

    			dialogStage.initOwner(primaryStage);
    			dialogStage.setScene(dialogScene);
    			dialogStage.setResizable(false);
    			dialogStage.show(); 
    		} catch(Exception e){
    			e.printStackTrace();
    		}
    	});
    	
    	configureButton.setOnAction(event->{
    		try{
    			
    			FXMLLoader loader = new FXMLLoader();
    			loader.setLocation(MainGui.class.getResource("view/configureDialog.fxml"));
    			GridPane configurePane = (GridPane) loader.load();

    			Stage dialogStage = new Stage();
    			dialogStage.setTitle("ReSeP Configuration");
        
    			ConfigureController controller=(ConfigureController)loader.getController();
    			controller.setStage(dialogStage);
    			
    			Scene dialogScene = new Scene(configurePane);
    			dialogScene.getStylesheets().add(MainGui.class.getResource("view/application.css").toExternalForm());

    			dialogStage.initOwner(primaryStage);
    			dialogStage.setScene(dialogScene);
    			dialogStage.setResizable(false);
    			dialogStage.show(); 
    		} catch(Exception e){
    			e.printStackTrace();
    		}
    	});

    	openProfileMenuItem.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent event) {
	    		FileChooser fileChooser = new FileChooser();
	    		fileChooser.setInitialDirectory(new File(resourceDirPath));
	    		fileChooser.setTitle("Select profile");
	    		FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("Add Files(*.xml)", "*.xml");
	    		fileChooser.getExtensionFilters().add(extension);
	    		File file = fileChooser.showOpenDialog(primaryStage);
	    		if (file != null) {
	    		    System.out.println(file.getPath());
	    		    addProfile(file.getPath());
	    		}
    	    }
    	});

    	saveRunMenuItem.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent event) {
	    		FileChooser fileChooser = new FileChooser();
	    		fileChooser.setInitialDirectory(new File(resourceDirPath));
	    		fileChooser.setTitle("Save Run");
	    		File file = fileChooser.showSaveDialog(primaryStage);
	    		if (file != null) {
	    		    try {
	    		    	Files.copy(Paths.get(resultFilePath), 
	    		    				Paths.get(file.getPath() + ".csv"), 
	    		    				StandardCopyOption.REPLACE_EXISTING);
	    		    } catch (IOException e) {
	    		    	e.printStackTrace();
	    		    }
	    		}
    	    }
    	});

    	openRunMenuItem.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent event) {
	    		FileChooser fileChooser = new FileChooser();
	    		fileChooser.setTitle("Select profile");
	    		FileChooser.ExtensionFilter extension =  
	    				new FileChooser.ExtensionFilter("Add Files(*.csv)", "*.csv");
	    		fileChooser.getExtensionFilters().add(extension);
	    		File file = fileChooser.showOpenDialog(primaryStage);
	    		
	    		if (file != null) {
    				
		    		chartController.generateCharts(resultFilePath, tasStart.getCurrentSteps());
	    			chartController.generateAvgCharts(resultFilePath, 
	    					tasStart.getCurrentSteps(), Integer.parseInt(sliceTextField.getText()));
	    			
	    			tableViewController.fillReliabilityDate(file.getPath());
	    			tableViewController.fillCostData(file.getPath());
	    			tableViewController.fillPerformanceData(file.getPath());
	    			
	    		}
    	    }
    	});

    	saveLogMenuItem.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent event) {
	    		FileChooser fileChooser = new FileChooser();
	    		fileChooser.setInitialDirectory(new File(resultDirPath));
	    		fileChooser.setTitle("Save Log");
	    		File file = fileChooser.showSaveDialog(primaryStage);
	    		if (file != null) {
	    		    try {
	    		    	Files.copy(Paths.get(logFilePath), Paths.get(file.getPath() + ".csv"), 
	    		    			StandardCopyOption.REPLACE_EXISTING);
	    		    } catch (IOException e) {
	    		    	e.printStackTrace();
	    			
	    		    }
	    		}
    	    }
    	});

    	saveReliabilityGraphMenuItem.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent event) {
	    		try {
	    		    FileChooser fileChooser = new FileChooser();
	    		    fileChooser.setInitialDirectory(new File(resultDirPath));
	    		    fileChooser.setTitle("Save Reliability Graph");
	    		    File file = fileChooser.showSaveDialog(primaryStage);
	    		    if (file != null) {
		    			try {
		    			    SnapshotParameters param = new SnapshotParameters();
		    			    param.setDepthBuffer(true);
		    			    WritableImage snapshot = chartController.reliabilityChart.snapshot(param, null);
		    			    BufferedImage tempImg = SwingFXUtils.fromFXImage(snapshot, null);
		
		    			    File outputfile = new File(file.getPath() + ".png");
		    			    ImageIO.write(tempImg, "png", outputfile);
		
		    			} catch (IOException e) {
		    			    e.printStackTrace();
		    			    
		    			}
	    		    }
	    		} catch (Exception e) {
	    		    e.printStackTrace();
	    		    
	    		}
    	    }
    	});

    	saveCostGraphMenuItem.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent event) {
    		try {
    		    FileChooser fileChooser = new FileChooser();
    		    fileChooser.setInitialDirectory(new File(resultDirPath));
    		    fileChooser.setTitle("Save Cost Graph");
    		    File file = fileChooser.showSaveDialog(primaryStage);
    		    if (file != null) {
    			try {
    			    SnapshotParameters param = new SnapshotParameters();
    			    param.setDepthBuffer(true);
    			    WritableImage snapshot = chartController.costChart.snapshot(param, null);
    			    BufferedImage tempImg = SwingFXUtils.fromFXImage(snapshot, null);

    			    File outputfile = new File(file.getPath() + ".png");
    			    ImageIO.write(tempImg, "png", outputfile);

    			} catch (IOException e) {
    			    e.printStackTrace();
    			    
    			}
    		    }
    		} catch (Exception e) {
    		    e.printStackTrace();
    		    
    		}
    	    }
    	});
    	
    	
    	saveInvCostGraphMenuItem.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent event) {
    		try {
    		    FileChooser fileChooser = new FileChooser();
    		    fileChooser.setInitialDirectory(new File(resultDirPath));
    		    fileChooser.setTitle("Save Invocation Cost Graph");
    		    File file = fileChooser.showSaveDialog(primaryStage);
    		    if (file != null) {
    			try {
    			    SnapshotParameters param = new SnapshotParameters();
    			    param.setDepthBuffer(true);
    			    WritableImage snapshot = chartController.invCostChart.snapshot(param, null);
    			    BufferedImage tempImg = SwingFXUtils.fromFXImage(snapshot, null);

    			    File outputfile = new File(file.getPath() + ".png");
    			    ImageIO.write(tempImg, "png", outputfile);

    			} catch (IOException e) {
    			    e.printStackTrace();
    			}
    		    }
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
    	    }
    	});

    	savePerformanceGraphMenuItem.setOnAction(event->{
    		try {
    		    FileChooser fileChooser = new FileChooser();
    		    fileChooser.setInitialDirectory(new File(resultDirPath));
    		    fileChooser.setTitle("Save Performance Graph");
    		    File file = fileChooser.showSaveDialog(primaryStage);
    		    if (file != null) {
    			try {
    			    SnapshotParameters param = new SnapshotParameters();
    			    param.setDepthBuffer(true);
    			    WritableImage snapshot = chartController.performanceChart.snapshot(param, null);
    			    BufferedImage tempImg = SwingFXUtils.fromFXImage(snapshot, null);

    			    File outputfile = new File(file.getPath() + ".png");
    			    ImageIO.write(tempImg, "png", outputfile);

    			} catch (IOException e) {
    			    e.printStackTrace();
    			}
    		    }
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
    	});
    	
    	
    	saveInvMenuItem.setOnAction(event->{
    		try {
    		    FileChooser fileChooser = new FileChooser();
    		    fileChooser.setInitialDirectory(new File(resultDirPath));
    		    fileChooser.setTitle("Save Invocations");
    		    File file = fileChooser.showSaveDialog(primaryStage);
    		    if (file != null) {
    		    	    		        		    	
    		    	List<String> services=new ArrayList<>();
    		    	for(String serviceName:tasStart.getServiceTypes().keySet())
    		    		services.add(serviceName);
    		    	
    				FileManager writer=new FileManager(file.getPath()+".csv");
    				writer.setMode(FileManager.WRITING);
    				writer.open();
    				
    				LinkedHashMap<Integer,Double> failureRates = chartController.failureRates;
    				LinkedHashMap<Integer,Double> responseTimes = chartController.responseTimes;
    				LinkedHashMap<Integer,Double> costs = chartController.costs;   
    				LinkedHashMap<Integer,Map<String,Double>> invocationRates = chartController.invocationRates;
    				
    				StringBuilder build;
    				
    				if (ProfileExecutor.profile != null) {
    					
    					InputProfile profile=ProfileExecutor.profile;
    					
    					Requirement reliabilityReq=profile.getRequirement("reliability");
    					Requirement performanceReq=profile.getRequirement("performance");
    					
    					List<Integer> frInvocations=new ArrayList<>();
    					String curFailureRate="0";
    					int frIndex=1;
    					List<Integer> rtInvocations=new ArrayList<>();
    					String curResponseTime="0";
    					int rtIndex=1;
    					
    					if(reliabilityReq!=null){
    						for(int invocations: reliabilityReq.getValues().keySet()){
    							frInvocations.add(invocations);
    						}
    						curFailureRate=reliabilityReq.getValues().get(0);
    					}

    					if(performanceReq!=null){
    						for(int invocations:performanceReq.getValues().keySet()){
    							rtInvocations.add(invocations);
    						}
    						curResponseTime=performanceReq.getValues().get(0);
    					}
    					
        				for(int invocations:failureRates.keySet()){
        					build=new StringBuilder();
        					
        					if(reliabilityReq!=null){
        						if(frIndex<frInvocations.size()){
        							if(invocations>=frInvocations.get(frIndex)){
        								curFailureRate=reliabilityReq.getValues().get(frInvocations.get(frIndex));
        								frIndex++;
        							}
        						}
        					}
        					
        					if(performanceReq!=null){
        						if(rtIndex<rtInvocations.size()){
        							if(invocations>=rtInvocations.get(rtIndex)){
        								curResponseTime=performanceReq.getValues().get(rtInvocations.get(rtIndex));
        								rtIndex++;
        							}
        						}
        					}
        					
        					build.append(invocations+","+failureRates.get(invocations)+","+curFailureRate+","+
        							responseTimes.get(invocations)+","+curResponseTime+","+costs.get(invocations)+",");
        									
        					Map<String,Double> rates=invocationRates.get(invocations);
        					for(String service:services){ 						
        						Double rate=0.0;
        						if(rates.containsKey(service))
        							rate=rates.get(service);
        						build.append(rate+",");
        					}
        					writer.write(build.toString());
        				}
    				}
    				writer.close();	    		    	
    		    }
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
    	});
    	
    	aboutButton.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent event) {
    		try {
    		    FXMLLoader loader = new FXMLLoader();
    		    loader.setLocation(MainGui.class.getResource("view/aboutDialog.fxml"));
    		    AnchorPane aboutPane = (AnchorPane) loader.load();

    		    Stage dialogStage = new Stage();
    		    dialogStage.setTitle("About");
    		    dialogStage.setResizable(false);

    		    Scene dialogScene = new Scene(aboutPane);
    		    dialogStage.initOwner(primaryStage);
    		    dialogStage.setScene(dialogScene);
    		    dialogStage.show();
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
    	    }
    	});

    	helpMenuItem.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent event) {
    		try {
    		    FXMLLoader loader = new FXMLLoader();
    		    loader.setLocation(MainGui.class.getResource("view/helpDialog.fxml"));
    		    AnchorPane helpPane = (AnchorPane) loader.load();

    		    Stage dialogStage = new Stage();
    		    dialogStage.setTitle("Help");
    		    dialogStage.setResizable(false);

    		    Scene dialogScene = new Scene(helpPane);
    		    dialogStage.initOwner(primaryStage);
    		    dialogStage.setScene(dialogScene);
    		    dialogStage.show();
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
    	    }
    	});
    	
    	openLogMenuItem.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent event) {
    		try {
    		    FXMLLoader loader = new FXMLLoader();
    		    loader.setLocation(MainGui.class.getResource("view/logDialog.fxml"));
    		    AnchorPane helpPane = (AnchorPane) loader.load();

    		    Stage dialogStage = new Stage();
    		    dialogStage.setTitle("Log");
    		    
    			LogController controller=(LogController)loader.getController();
    			controller.setStage(dialogStage);

    		    Scene dialogScene = new Scene(helpPane);
    		    dialogScene.getStylesheets().add(MainGui.class.getResource("view/application.css").toExternalForm());

    		    dialogStage.initOwner(primaryStage);
    		    dialogStage.setScene(dialogScene);
    		    dialogStage.show();
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
    	    }
    	});
    	
	openActivFORMS.setOnAction(event->{
		try {		    
		    String command = String.format("java -jar %s ModelAdaptation ModelEvolution GoalManagement", System.getProperty("user.dir") + "/libs/ActivFORMSv2.7.jar");
		    System.out.println(command);
		        Runtime.getRuntime().exec(command);
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	});
	
	openModelEvolution.setOnAction(event->{
		
		Dialog<?> dialog = new Dialog<>();
		dialog.setTitle("Model Evolution");
				
		ButtonType okButtonType = new ButtonType("Update",ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().setAll(okButtonType);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 40, 10, 20));

		Label currentPathLabel = new Label();
		if(currentPath == null)
			currentPathLabel.setText("Model not updated yet!");
		else
			currentPathLabel.setText(currentPath);

		grid.add(new Label("Current model path: "), 0, 0);
		grid.add(currentPathLabel, 1, 0);
		
		
		TextField pathField = new TextField();
		pathField.setPromptText("Model Path");
		
		Button selectButton=new Button("Select model");
		selectButton.setOnAction(e->{
			
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select model");
			FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("Uppaal XML file((*.xml)", "*.xml");
			fileChooser.getExtensionFilters().add(extension);
			File file = fileChooser.showOpenDialog(null);
			
			if (file != null) {
				pathField.setText(file.getPath());
			}		
		});
		
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == okButtonType) {
		    	if(!pathField.getText().isEmpty()){
		    				    		
			    	ActivFORMSEngine engine =((ModelEvolutionEngine) TASStart.getAdaptationEngines().get("Model Evolution")).getEngine();
			    	String name=engine.getGoalManager().getActiveModelKey();
				    try {
						engine.getGoalManager().updateModelFromFile(name, pathField.getText());
						currentPath=pathField.getText();
			    		pathField.clear();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
		    	}
		    }
			return null;
		});

		grid.add(selectButton, 0, 1);
		grid.add(pathField, 1, 1);
		dialog.getDialogPane().setContent(grid);
		dialog.show();
	});
	
	openGoalManagement.setOnAction(event->{
	
		Dialog<DialogResult> dialog = new Dialog<>();
		dialog.setTitle("Goal Management");
		
		ButtonType okButtonType = new ButtonType("OK",ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().setAll(okButtonType);
		
		GridPane grid = new GridPane();
		grid.setPrefWidth(250);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 40, 10, 20));

		
		Label modelLabel=new Label();
		modelLabel.setText(activeModel == null? "Regular": activeModel);
		Label loadLabel=new Label();

		grid.add(new Label("Model Active: "), 0, 0);
		grid.add(modelLabel, 1, 0);
		grid.add(new Label("Service Load: "), 0, 1);
		grid.add(loadLabel, 1, 1);
		
		GoalManagementEngine gmEngine=((GoalManagementEngine)TASStart.getAdaptationEngines().get("Goal Management"));
		
		ActivFORMSEngine engine=gmEngine.engine;
		
		engine.getGoalManager().setActiveModelProbe(new ActiveModelProbe(){
			@Override
			public void activeModelAdapted(String newModelName,
					String previousModelName) {
			    Platform.runLater(new Runnable() {
					@Override
					public void run() {
					    	activeModel = newModelName;
						modelLabel.setText(newModelName);
					}
				});
			}	
		});

		gmEngine.getLoadBalancer().handler=(object)->{
			if(object instanceof Integer){
			    Platform.runLater(new Runnable() {
					@Override
					public void run() {
						loadLabel.setText(object.toString());
					}
				});				
			}
		};
		
		dialog.getDialogPane().setContent(grid);
		dialog.show();
	});

    }

    private void fillProfiles() {
		File folder = new File(profileDirPath);
		File[] files = folder.listFiles();
	
		try {
		    for (File file : files) {
			if (file.isFile()) {
			    if (file.getName().lastIndexOf('.') > 0)
				this.addProfile(file.getAbsolutePath());
			}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
    }

    private void addProfile(String profilePath) {

    	final String path = profilePath;

    	AnchorPane itemPane = new AnchorPane();

    	Button inspectButton = new Button();
    	inspectButton.setPrefWidth(32);
    	inspectButton.setPrefHeight(32);
    	inspectButton.setLayoutY(5);
    	inspectButton.setId("inspectButton");

    	inspectButton.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent event) {
	    		try {
	    			
	    		    FXMLLoader loader = new FXMLLoader();
	    		    loader.setLocation(MainGui.class.getResource("view/inputProfileDialog.fxml"));
	    		    AnchorPane pane = (AnchorPane) loader.load();
	
	    		    Stage dialogStage = new Stage();
	    		    dialogStage.setTitle("Input Profile");
	    		    
	    			InputProfileController controller=(InputProfileController)loader.getController();
	    			controller.setStage(dialogStage);
	    			controller.viewProfile(path);
	
	    		    Scene dialogScene = new Scene(pane);
	    		    dialogScene.getStylesheets().add(MainGui.class.getResource("view/application.css").toExternalForm());
	
	    		    dialogStage.initOwner(primaryStage);
	    		    dialogStage.setResizable(false);
	    		    dialogStage.setScene(dialogScene);
	    		    dialogStage.show();    		    
	    		    
	    		} catch (Exception e) {
	    		    e.printStackTrace();
	    		}

    	    }
    	});

    	Button runButton = new Button();
    	runButton.setPrefWidth(32);
    	runButton.setPrefHeight(32);
    	runButton.setLayoutY(5);
    	runButton.setId("runButton");
    	profileRuns.add(runButton);
    	if (this.workflowPath == null)
    	    runButton.setDisable(true);

    	Label label = new Label();
    	label.setLayoutY(15);
    	label.setText(Paths.get(profilePath).getFileName().toString().split("\\.")[0]);

    	final Circle circle = new Circle();
    	circle.setLayoutY(20);
    	circle.setFill(Color.GREEN);
    	circle.setRadius(10);

    	runButton.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent event) {

    	    if(runButton.getId().equals("runButton")){

    			probe.reset();
    			
    			Task<Void> task = new Task<Void>() {
    			    @Override
    			    protected Void call() throws Exception {

    				if (workflowPath != null) {
    					
    				    if(preAdaptation != null)
    				    	adaptationEngines.get(preAdaptation).stop();
    					    
    				    preAdaptation = currentAdaptation;
					    adaptationEngines.get(currentAdaptation).start();

					    Platform.runLater(new Runnable() {
	    					@Override
	    					public void run() {
	    					    circle.setFill(Color.DARKRED);
	    					    runButton.setId("stopButton");
    						}
					    });

    				    tasStart.executeWorkflow(workflowPath, path);

    				    if(preAdaptation != null)
    						adaptationEngines.get(preAdaptation).stop();
					    
    				    preAdaptation = null;
    					
    				    Platform.runLater(new Runnable() {
        					@Override
        					public void run() {
        						
        						circle.setFill(Color.GREEN);
        					    runButton.setId("runButton");
        						chartController.clear();
        						tableViewController.clear();
        						latencyChart.clearData();
        						
        						chartController.generateCharts(resultFilePath, tasStart.getCurrentSteps());
        						chartController.generateAvgCharts(resultFilePath, tasStart.getCurrentSteps(),Integer.parseInt(sliceTextField.getText()));
        						latencyChart.generateLatencyChart(resultFilePath, tasStart.getCurrentSteps());
        						
        					    tableViewController.fillReliabilityDate(resultFilePath);
        					    tableViewController.fillCostData(resultFilePath);
        						tableViewController.fillPerformanceData(resultFilePath);
        					}
        					
        					
    				    });
    				}
    				return null;
    			    }
    			};

    			ExecutionThread thread = new ExecutionThread("main",task);
    			thread.setDaemon(true);
    			thread.start();

    			System.out.println("Start task!!");
    			ProfileExecutor.readFromXml(path);
    			maxSteps = ProfileExecutor.profile.getMaxSteps();
    			Task<Void> progressTask = new Task<Void>() {
    			    @Override
    			    protected Void call() throws Exception {
    			    	while (probe.workflowInvocationCount < maxSteps) {
        					Platform.runLater(new Runnable() {
            					@Override
            					public void run() {
            					    invocationLabel.setText(" " + probe.workflowInvocationCount + " / " + maxSteps);  
            					}
        				    });
        					updateProgress(probe.workflowInvocationCount, maxSteps);
        				    Thread.sleep(1000);   
    			    	}

    			    	Platform.runLater(new Runnable() {
        				    @Override
        				    public void run() {
        				    	invocationLabel.setText("" + maxSteps + " / " + maxSteps);
        				    }
        				});
        				updateProgress(probe.workflowInvocationCount, maxSteps);        				
        				return null;
    			    }
    			};
    			progressBar.progressProperty().bind(progressTask.progressProperty());
    			new Thread(progressTask).start();
    	    }
    	    
    	    else{ 	
    	    	 if(preAdaptation!=null)
    				 adaptationEngines.get(preAdaptation).stop();
    			    preAdaptation=null;
    			    
    	    	tasStart.stop();
    	    				
    		    Platform.runLater(new Runnable() {
    			@Override
    			public void run() {
    			    circle.setFill(Color.GREEN);
    			    runButton.setId("runButton");
    				chartController.clear();
    				latencyChart.clearData();
    				tableViewController.clear();
    				
    				chartController.generateCharts(resultFilePath, tasStart.getCurrentSteps());
    				chartController.generateAvgCharts(resultFilePath, tasStart.getCurrentSteps(),Integer.parseInt(sliceTextField.getText()));
    				latencyChart.generateLatencyChart(resultFilePath, tasStart.getCurrentSteps());
    				
    			    tableViewController.fillReliabilityDate(resultFilePath);
    			    tableViewController.fillCostData(resultFilePath);
    				tableViewController.fillPerformanceData(resultFilePath);
    			}
    		    });
    	    }
    	    }
    	});

    	AnchorPane.setLeftAnchor(circle, 10.0);
    	AnchorPane.setLeftAnchor(label, 40.0);
    	AnchorPane.setRightAnchor(inspectButton, 60.0);
    	AnchorPane.setRightAnchor(runButton, 10.0);

    	itemPane.getChildren().setAll(circle, label, runButton, inspectButton);

    	profileListView.getItems().add(itemPane);
        
    }
    

    private AnchorPane addService(String serviceName, boolean state) {
	
    	AnchorPane itemPane = new AnchorPane();
    	
    	Button inspectButton = new Button();
    	inspectButton.setPrefWidth(32);
    	inspectButton.setPrefHeight(32);
    	inspectButton.setLayoutY(5);
    	inspectButton.setId("inspectButton");
    	inspectButton.setOnAction(event->{
    		 			
    		try{
        	    FXMLLoader loader = new FXMLLoader();
        	    loader.setLocation(MainGui.class.getResource("view/serviceProfileDialog.fxml"));
        	    AnchorPane pane = (AnchorPane) loader.load();

        	    Stage dialogStage = new Stage();
        	    dialogStage.setTitle(serviceName);
        	    
        		ServiceProfileController controller=(ServiceProfileController)loader.getController();
        		controller.setStage(dialogStage);
        		controller.setServiceProfileClasses(tasStart.getServiceProfileClasses());
        		controller.setService(tasStart.getService(serviceName));

        	    Scene dialogScene = new Scene(pane);
        	    dialogScene.getStylesheets().add(MainGui.class.getResource("view/application.css").toExternalForm());

        	    dialogStage.initOwner(primaryStage);
        	    dialogStage.setScene(dialogScene);
        	    dialogStage.setResizable(false);
        	    dialogStage.show();
    		}
    		catch(Exception e){
    			e.printStackTrace();
    		}
    		
    		
    	});

    	Label label = new Label();
    	label.setLayoutY(15);
    	label.setText(serviceName);

    	ServiceDescription description=serviceRegistry.getService(serviceName);
 	
    	Circle circle = new Circle();
    	circle.setOnMouseClicked(event->{
    		if(circle.getFill().equals(Color.BLACK)){
    			compositeService.getCache().addService(description);
    			serviceRegistry.addService(description);
    			servicePanes.put(serviceName, itemPane);
    		    circle.setFill(Color.GREEN);
    		}
    		else{    		    
    			compositeService.getCache().remove(description);
    			serviceRegistry.removeService(description);
    			servicePanes.remove(serviceName);
    		    circle.setFill(Color.BLACK);
    		}
    	});
    	
    	circle.setLayoutY(20);
    	if (state)
    	    circle.setFill(Color.GREEN);
    	
    	else
    	    circle.setFill(Color.DARKRED);
    	circle.setRadius(10);

    	AnchorPane.setLeftAnchor(circle, 10.0);
    	AnchorPane.setLeftAnchor(label, 40.0);
    	AnchorPane.setRightAnchor(inspectButton, 10.0);
    	itemPane.getChildren().setAll(circle, label, inspectButton);
    	
    	serviceListView.getItems().add(itemPane);
    	return itemPane;
    }
    
}
