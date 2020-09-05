package application.view;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

public class LineChartView {
	
    private AnchorPane anchorPane;
    private LineChart<Number, Number> lineChart;
    
    private NumberAxis xAxis;
    private NumberAxis yAxis;
    
    public LineChartView(AnchorPane anchorPane) {
    	this.anchorPane = anchorPane;
    	
    	this.xAxis = new NumberAxis();
    	this.yAxis = new NumberAxis();
    }
    
    public void setXAxisLabel(String label) {
    	this.xAxis.setLabel(label);
    }
    
    public void setChartTitle(String title) {
    	if (lineChart != null) {
			lineChart.setTitle(title);
		}
    }
    
    public void startConfigChart() {
    	this.lineChart = new 
				LineChart<Number,Number>(xAxis, yAxis);
		
		this.anchorPane.getChildren().add(lineChart);
		this.lineChart.prefWidthProperty()
				.bind(this.anchorPane.widthProperty());
		this.lineChart.prefHeightProperty()
				.bind(this.anchorPane.heightProperty());
    }
    
    public void setSeries(XYChart.Series<Number, Number> series) {
		this.lineChart.getData().add(series);
    }
    
    public void clearData() {
    	this.anchorPane.getChildren().clear();
    	this.lineChart.getData().clear();
    }
}
