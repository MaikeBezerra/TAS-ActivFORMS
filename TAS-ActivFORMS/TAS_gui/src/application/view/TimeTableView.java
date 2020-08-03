package application.view;

import java.util.List;

import application.model.TimeEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TimeTableView {

	private static final String MEASURE = "Measure";
	private static final String VALUE = "Value";
	
	private static final String MEASURE_NAME = "name";
	private static final String MEASURE_VALUE = "value";
	
	private TableView<TimeEntry> timeTableView;
	
	private ObservableList<TimeEntry> timeData = FXCollections.observableArrayList();
	
	public TimeTableView(TableView<TimeEntry> timeTableView) {
		this.timeTableView = timeTableView;
		this.generateHeardersColumn();
	}
	
	public void fillTimeData(List<TimeEntry> timeEntries) {
		for (TimeEntry timeEntry : timeEntries) {
			this.timeData.add(timeEntry);
		}
	}
	
	public void clearTable() {
		this.timeData.clear();
	}
	
	private void generateHeardersColumn() {
		TableColumn<TimeEntry, Object> measureColumn = generateColumn(MEASURE, MEASURE_NAME);
		TableColumn<TimeEntry, Object> valueColumn = generateColumn(VALUE, MEASURE_VALUE);
		
		this.timeTableView.setItems(this.timeData);
		this.timeTableView.getColumns().add(measureColumn);
		this.timeTableView.getColumns().add(valueColumn);
	}

	private TableColumn<TimeEntry, Object> generateColumn(String measure, String measureName) {
		TableColumn<TimeEntry, Object> column = new TableColumn<TimeEntry, Object>(measure);
		column.setCellValueFactory(new PropertyValueFactory<TimeEntry, Object>(measureName));
		column.prefWidthProperty().bind(timeTableView.widthProperty().divide(6).multiply(3));
		return column;
	}
}
