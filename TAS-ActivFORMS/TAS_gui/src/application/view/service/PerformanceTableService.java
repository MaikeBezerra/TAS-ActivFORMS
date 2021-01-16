package application.view.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.model.PerformanceEntry;
import application.utility.strings.TablesUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PerformanceTableService {
	
	private TableView<PerformanceEntry> table;
	
	private ObservableList<PerformanceEntry> data 
				= FXCollections.observableArrayList();
	
	private Map<String,PerformanceEntry> entries;
		
	private Map<String,List<Double>> latencies;
	
	public PerformanceTableService(TableView<PerformanceEntry> performanceTableView) {
		this.table = performanceTableView;
		this.entries = new HashMap<>();
		this.latencies = new HashMap<>();	
		
		this.createHeaderTable();
	}
	
	private void createHeaderTable() {
		TableColumn<PerformanceEntry, String> columnService = 
				new TableColumn<PerformanceEntry, String>(TablesUtil.SERVICE);
		columnService.setCellValueFactory(
				new PropertyValueFactory<PerformanceEntry, String>(TablesUtil.PROPERTY_SERVICE));
		columnService.prefWidthProperty().bind(table.widthProperty()
				.divide(TablesUtil.DIVIDE_SMALL)
				.multiply(TablesUtil.MULTIPLY_SMALL));
		
		TableColumn<PerformanceEntry,Integer> columnInvocation = 
				generateColumnIntegerPerformanceEntry(TablesUtil.INVOCATIONS, TablesUtil.PROPERTY_INVOCATIONS);
		TableColumn<PerformanceEntry,Integer> columnFail = 
				generateColumnIntegerPerformanceEntry(TablesUtil.FAIL, TablesUtil.PROPERTY_FAIL);
		TableColumn<PerformanceEntry,Double> columnResponse = 
				generateColumnDoublePerformanceEntry(TablesUtil.AVG_RESPONSE_TIME, TablesUtil.PROPERTY_AVG_RESPONSE_TIME);
		TableColumn<PerformanceEntry,Double> columnLatency = 
				generateColumnDoublePerformanceEntry("Latency", "latency");
		TableColumn<PerformanceEntry,Double> columnTroughput = 
				generateColumnDoublePerformanceEntry("Throughput", "throughput");
		TableColumn<PerformanceEntry,Double> columnQuerySuccessRate = 
				generateColumnDoublePerformanceEntry("QuerySuccessRates", "querySuccessRates");
		TableColumn<PerformanceEntry,Double> columnQueryDelay = 
				generateColumnDoublePerformanceEntry("QueryDelay", "queryDelay");
		
		table.setItems(data);
		table.getColumns().add(columnService);
		table.getColumns().add(columnInvocation);	
		table.getColumns().add(columnFail);
		table.getColumns().add(columnResponse);
		table.getColumns().add(columnLatency);
		table.getColumns().add(columnTroughput);
		table.getColumns().add(columnQuerySuccessRate);
		table.getColumns().add(columnQueryDelay);
	}
	
	public void clearData() {
		this.data.clear();
		this.entries.clear();
	}
	
	public void fillPerformanceData(String resultFilePath){
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(resultFilePath));
			String line;
			String service;
			boolean result;
			
			while ((line = br.readLine()) != null) {
				
				String[] str=line.split(",");
				
				if(str.length>=3){					
					service=str[1];
					result = Boolean.parseBoolean(str[2]);					
					
					if(!service.equals("AssistanceService")){
						
						if(!entries.containsKey(service)){
							entries.put(service, new PerformanceEntry(service));
							latencies.put(service, new ArrayList<Double>());
						}
						
						PerformanceEntry performanceEntry = entries.get(service);
						latencies.get(service).add(Double.parseDouble(str[6]));
						
						performanceEntry.setInvocationNum(performanceEntry.getInvocationNum()+1);
						
						if(result) {
							performanceEntry.addResponseTime(Double.parseDouble(str[5]));
						}	
						else
							performanceEntry.setFailNum(performanceEntry.getFailNum()+1);
						
					}			
				}
			}
			br.close();	
				
			for (PerformanceEntry entry : entries.values()) {
				
				
				if (latencies.containsKey(entry.getService())) {
					Collections.sort(latencies.get(entry.getService()));
					double latency;
					if (latencies.get(entry.getService()).size() % 2 == 0) {
						int i = (int) ((latencies.get(entry.getService()).size() / 2) + 0.5);
						latency = (latencies.get(entry.getService()).get(i) +
								latencies.get(entry.getService()).get(i - 1)) / 2;
						
					} else { 
						int i = latencies.get(entry.getService()).size() / 2;
						latency = latencies.get(entry.getService()).get(i) / 2;
					}
					entry.setLatency(latency);
				}
				entry.setAvgResponseTime();
				
				final double executionTime = (entry.getAvgResponseTime() * entry.getInvocationNum()) / 1000;
				final double invocations = entry.getInvocationNum();
				final double invocationSuccess = invocations - entry.getFailNum();
				
				entry.setThroughput(invocationSuccess / executionTime);
				entry.setQuerySuccessRates(entry.getInvocationNum() / executionTime);
				entry.setQueryDelay(entry.getFailNum() / invocations);
				
				data.add(entry);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private TableColumn<PerformanceEntry, Integer> generateColumnIntegerPerformanceEntry(String columnName, String columnProperty) {
		TableColumn<PerformanceEntry, Integer> column = new TableColumn<PerformanceEntry, Integer>(columnName);
		column.setCellValueFactory(new PropertyValueFactory<PerformanceEntry, Integer>(columnProperty));
		column.prefWidthProperty().bind(table.widthProperty().divide(TablesUtil.DIVIDE_SMALL));
		return column;
	}
	
	private TableColumn<PerformanceEntry, Double> generateColumnDoublePerformanceEntry(String columnName, String columnProperty) {
		TableColumn<PerformanceEntry, Double> column = new TableColumn<PerformanceEntry, Double>(columnName);
		column.setCellValueFactory(new PropertyValueFactory<PerformanceEntry, Double>(columnProperty));
		column.prefWidthProperty().bind(table.widthProperty().divide(TablesUtil.DIVIDE_SMALL));
		return column;
	}
}
