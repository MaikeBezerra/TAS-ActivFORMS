package application;
	
import java.io.File;

import application.model.ReliabilityEntry;
import application.utility.Utility;
import application.view.controller.ApplicationController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tas.configuration.TASStart;
import tas.services.log.Log;

public class MainGui extends Application {	
	
	private static final String logFile = "results" + File.separator + "log.csv";
	private static final String resultFile = "results" + File.separator + "result.csv";
	
	public static Long initialTime;
	public static Long finalTime;
	
	public static long screenDowntimeStart;
	public static long screenDowntimeStop;
	public static long screenDowntime;

	@Override
	public void start(Stage primaryStage) {
		
		initialTime = System.currentTimeMillis();
		
		try {
			
			Handler globalExceptionHandler = new Handler();
			Thread.setDefaultUncaughtExceptionHandler(globalExceptionHandler);
				
	    	Utility.createFile(logFile);
	    	Utility.createFile(resultFile);
	    	Log.initialize(logFile);

			TASStart tasStart = new TASStart(); 
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/application.fxml"));
			
			SplitPane pane = (SplitPane) loader.load();			
			
			ApplicationController controller = (ApplicationController) loader.getController();
			controller.setPrimaryStage(primaryStage);
			controller.setTasStart(tasStart);
			controller.setCompositeService(tasStart.getAssistanceService());
			controller.setProbe(tasStart.getMonitor());
			controller.setConfigurations(TASStart.getAdaptationEngines());
			controller.setServiceRegistry(tasStart.getServiceRegistry());
			
			Scene scene = new Scene(pane);
			scene.getStylesheets().add(getClass().getResource("view/application.css").toExternalForm());
			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent arg0) {
					finalTime = System.currentTimeMillis();	
					ReliabilityEntry.mtbf();
					ReliabilityEntry.mtwf();
					ReliabilityEntry.meanRecovery();
					Log.stop();
		    		System.exit(0);
				}
			});

			primaryStage.setScene(scene);
			primaryStage.setTitle("Tele Assistance System");
			primaryStage.show();
			
			Screen screen = Screen.getPrimary();
			Rectangle2D bounds = screen.getVisualBounds();

			primaryStage.setX(bounds.getMinX());
			primaryStage.setY(bounds.getMinY());
			primaryStage.setWidth(bounds.getWidth());
			primaryStage.setHeight(bounds.getHeight());
			
	        
		} catch(Exception e) {
			screenDowntimeStart = System.currentTimeMillis();
			e.printStackTrace();
			screenDowntimeStop = System.currentTimeMillis();
			screenDowntime = screenDowntimeStop - screenDowntimeStart;
			System.err.println("Tele of Exception");
			
		}
		
	}
			
	public static void main(String[] args) {
	    launch(args);   
	}
}
