package application.view;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

public class LatencyChartView {

	private AnchorPane latencyPane;
	private LineChart<Number,Number> lineChart;
	
	public LatencyChartView(AnchorPane pane) {
		this.latencyPane = pane;
	}
	
	public void generateLatencyChart(String resultFilePath, int maxSteps){
		try{	
	        BufferedReader br = new BufferedReader(new FileReader(resultFilePath));
			
	        String line;
			String service;
			
			Map<String,XYChart.Series<Number,Number>> latencies = new HashMap<>();

			while ((line = br.readLine()) != null) {
				String[] str = line.split(",");
				
				if(str.length >= 6){		
					
					service = str[1];
					
					if(!service.equals("AssistanceService")){
						Integer invocationNum = Integer.parseInt(str[0]);
						Double latency = Double.parseDouble(str[6]);
						
						if(!latencies.containsKey(service)){
							XYChart.Series<Number,Number> latencySerie = new XYChart.Series<>();
							latencySerie.getData().clear();
							latencySerie.setName(service);
							
							latencies.put(service, latencySerie);
							latency = 0.0;
						}
						
						XYChart.Series<Number,Number> latencySerie = latencies.get(service);
						latencySerie.getData().add(new XYChart.Data<Number,Number>(invocationNum, latency));
					}
				}
			}
			br.close();
			
			NumberAxis xAxis = new NumberAxis();
			NumberAxis yAxis = new NumberAxis();
			
			lineChart = new LineChart<Number,Number>(xAxis,yAxis);
			lineChart.setTitle("Latency");
			latencyPane.getChildren().add(lineChart);
			
			lineChart.prefWidthProperty().bind(latencyPane.widthProperty());
			lineChart.prefHeightProperty().bind(latencyPane.heightProperty());
	        lineChart.getData().clear(); 
			
	        lineChart.getData().addAll(latencies.values());
	        latencies.clear();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void clearData() {
		latencyPane.getChildren().clear();
	}
}
