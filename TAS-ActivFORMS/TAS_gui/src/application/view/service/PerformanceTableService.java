package application.view.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import application.model.PerformanceEntry;
import application.utility.strings.TablesUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PerformanceTableService {
	
	private TableView<PerformanceEntry> performanceTable;
	
	private ObservableList<PerformanceEntry> performanceData 
				= FXCollections.observableArrayList();
	
	public PerformanceTableService(TableView<PerformanceEntry> performanceTableView) {
		this.performanceTable = performanceTableView;
		
		this.createHeaderTable();
	}
	
	private void createHeaderTable() {
		TableColumn<PerformanceEntry, String> columnService = 
				new TableColumn<PerformanceEntry, String>(TablesUtil.SERVICE);
		columnService.setCellValueFactory(
				new PropertyValueFactory<PerformanceEntry, String>(TablesUtil.PROPERTY_SERVICE));
		columnService.prefWidthProperty().bind(performanceTable.widthProperty()
				.divide(TablesUtil.DIVIDE_SMALL)
				.multiply(TablesUtil.MULTIPLY_SMALL));
		
		TableColumn<PerformanceEntry,Integer> columnInvocation = 
				generateColumnIntegerPerformanceEntry(TablesUtil.INVOCATIONS, TablesUtil.PROPERTY_INVOCATIONS);
		TableColumn<PerformanceEntry,Integer> columnFail = 
				generateColumnIntegerPerformanceEntry(TablesUtil.FAIL, TablesUtil.PROPERTY_FAIL);
		TableColumn<PerformanceEntry,Double> columnResponse = 
				generateColumnDoublePerformanceEntry(TablesUtil.AVG_RESPONSE_TIME, TablesUtil.PROPERTY_AVG_RESPONSE_TIME);

		performanceTable.setItems(performanceData);
		performanceTable.getColumns().add(columnService);
		performanceTable.getColumns().add(columnInvocation);
		performanceTable.getColumns().add(columnFail);
		performanceTable.getColumns().add(columnResponse);
	}
	
	public void clearData() {
		this.performanceData.clear();
	}
	
	public void fillPerformanceData(String resultFilePath){
		
		Map<String,PerformanceEntry> performanceEntries=new HashMap<>();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(resultFilePath));
			String line;
			String service;
			boolean result;
			
			double maxResponseTime = 0;
			
			while ((line = br.readLine()) != null) {
				
				String[] str=line.split(",");
				
				if(str.length>=3){					
					service=str[1];
					result=Boolean.parseBoolean(str[2]);					
					
					if(!service.equals("AssistanceService")){
						
						if(!performanceEntries.containsKey(service)){
							performanceEntries.put(service, new PerformanceEntry(service));
						}
						PerformanceEntry performanceEntry=performanceEntries.get(service);

						performanceEntry.setInvocationNum(performanceEntry.getInvocationNum()+1);
						
						if(result) {
							performanceEntry.addResponseTime(Double.parseDouble(str[5]));
							if(maxResponseTime < Double.parseDouble(str[5])) {
								maxResponseTime = Double.parseDouble(str[5]);
							}
						}	
						else
							performanceEntry.setFailNum(performanceEntry.getFailNum()+1);
					}			
				}
			}
			br.close();	
			
			//addTimeEntry(new TimeEntry("Frame processing time", maxResponseTime));
			
			for (PerformanceEntry entry : performanceEntries.values()) {
				entry.setAvgResponseTime();
				performanceData.add(entry);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private TableColumn<PerformanceEntry, Integer> generateColumnIntegerPerformanceEntry(String columnName, String columnProperty) {
		TableColumn<PerformanceEntry, Integer> column = new TableColumn<PerformanceEntry, Integer>(columnName);
		column.setCellValueFactory(new PropertyValueFactory<PerformanceEntry, Integer>(columnProperty));
		column.prefWidthProperty().bind(performanceTable.widthProperty().divide(TablesUtil.DIVIDE_SMALL));
		return column;
	}
	
	private TableColumn<PerformanceEntry, Double> generateColumnDoublePerformanceEntry(String columnName, String columnProperty) {
		TableColumn<PerformanceEntry, Double> column = new TableColumn<PerformanceEntry, Double>(columnName);
		column.setCellValueFactory(new PropertyValueFactory<PerformanceEntry, Double>(columnProperty));
		column.prefWidthProperty().bind(performanceTable.widthProperty().divide(TablesUtil.DIVIDE_MEDIUM));
		return column;
	}
}
