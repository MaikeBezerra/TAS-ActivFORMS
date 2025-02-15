package application;
	
import java.io.File;

import tas.configuration.*;
import tas.services.log.Log;
import application.utility.Utility;
import application.view.controller.ApplicationController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;

public class MainGui extends Application {	
	
	static final String logFile="results"+File.separator+"log.csv";
	static final String resultFile="results"+File.separator+"result.csv";

	@Override
	public void start(Stage primaryStage) {
		try {
			
	    	Utility.createFile(logFile);
	    	Utility.createFile(resultFile);
	    	Log.initialize(logFile);

			TASStart tasStart = new TASStart();
 
			//System.out.println("Start services");
			FXMLLoader loader=new FXMLLoader();
			loader.setLocation(getClass().getResource("view/application.fxml"));
			SplitPane pane=(SplitPane)loader.load();			
			
			ApplicationController controller=(ApplicationController)loader.getController();
			controller.setPrimaryStage(primaryStage);
			controller.setTasStart(tasStart);
			controller.setCompositeService(tasStart.getAssistanceService());
			controller.setProbe(tasStart.getMonitor());
			controller.setConfigurations(tasStart.getAdaptationEngines());
			controller.setServiceRegistry(tasStart.getServiceRegistry());
			
			Scene scene=new Scene(pane);
			scene.getStylesheets().add(getClass().getResource("view/application.css").toExternalForm());
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent arg0) {
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
			e.printStackTrace();
		}
	}
			
	public static void main(String[] args) {
	    launch(args);
	}
}
