package application.view;

import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

public class StackedBarChartView {
	
	private AnchorPane anchorPane;
	private StackedBarChart<String,Number> stackedBarChart;
	
	private CategoryAxis xAxis;
	private NumberAxis yAxis;
	
	public StackedBarChartView(AnchorPane anchorPane) {
		this.anchorPane = anchorPane;
		
		this.xAxis = new CategoryAxis();
		this.yAxis = new NumberAxis();
	}
	
	public CategoryAxis getxAxis() {
		return xAxis;
	}

	public NumberAxis getyAxis() {
		return yAxis;
	}

	public void setLabelXAxis(String name) {
		this.xAxis.setLabel(name);
	}
	
	public void setLabelYAxis(String name) {
		this.yAxis.setLabel(name);
	}
	
	public void setCategoryConfig(List<String> categories, int width) {
		this.stackedBarChart.setCategoryGap(
				this.stackedBarChart.widthProperty().divide(width*5).get());
		
		getxAxis().setCategories(
				FXCollections.<String>observableArrayList(categories));
	}
	
	public void setDelays(Map<String,XYChart.Series<String,Number>> delays) {
		this.stackedBarChart.getData().clear();
		this.stackedBarChart.getData().addAll(delays.values());
	}
	
	public void clearPane() {
		this.anchorPane.getChildren().clear();
	}
	
	public void generateStackedBarChart() {
		this.stackedBarChart = new 
				StackedBarChart<String,Number>(getxAxis(), getyAxis());
		
		this.anchorPane.getChildren().add(stackedBarChart);
		this.stackedBarChart.prefWidthProperty()
				.bind(this.anchorPane.widthProperty());
		this.stackedBarChart.prefHeightProperty()
				.bind(this.anchorPane.heightProperty());
	}	
}
