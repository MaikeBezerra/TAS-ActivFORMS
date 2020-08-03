package application.view.controller;

import application.view.StackedBarChartView;
import application.view.service.chart.LatencyChartService;
import javafx.scene.layout.AnchorPane;

public class LatencyChartController {
	
	private StackedBarChartView chartView;
	private LatencyChartService chartService;
	
	public LatencyChartController(AnchorPane anchorPane) {
		this.chartView = new StackedBarChartView(anchorPane);
		this.chartService = new LatencyChartService();	
	}
	
	public void generateLatencyChart(String resultFilePath, int maxSteps) {
		this.chartView.setLabelXAxis("Invocations");
		this.chartView.setLabelYAxis("Response Time / ms ");
		this.chartView.generateStackedBarChart();
		
		this.chartView.setCategoryConfig(
				this.chartService.getCategories(maxSteps), maxSteps);
		
		this.chartView.setDelays(
				this.chartService.getDelays(resultFilePath, maxSteps));
	}
	
	public void resetPane() {
		this.chartView.clearPane();
	}
}
