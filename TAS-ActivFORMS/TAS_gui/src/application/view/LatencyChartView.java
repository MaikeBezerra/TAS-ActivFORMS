package application.view;

import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

public class LatencyChartView {
	
	private LineChartView lineChartView;
	final XYChart.Series<Number, Number> series = new XYChart.Series<>();
	
	public LatencyChartView(AnchorPane latencyChartPane) {
		this.lineChartView = new LineChartView(latencyChartPane);
	}
	
	public void addPropertyLatencyChart(int invocation, double latency) {
		series.getData().add(new XYChart.Data<>(latency, invocation));
		
	}
	
	public void clearPane() {
		this.lineChartView.clearData();
	}
	
	public void generateLatencyChart() {
		startConfig();
		this.lineChartView.setSeries(series);
	}
	
	private void startConfig() {
		this.lineChartView.startConfigChart();
	}
}
