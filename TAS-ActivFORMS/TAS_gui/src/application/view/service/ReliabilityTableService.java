package application.view.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.model.ReliabilityEntry;
import application.utility.strings.TablesUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReliabilityTableService {

	private TableView<ReliabilityEntry> reliabilityTableView;
	private ObservableList<ReliabilityEntry> reliabilityData = FXCollections.observableArrayList();
	
	public ReliabilityTableService(TableView<ReliabilityEntry> reliabilityTableView) {
		this.reliabilityTableView = reliabilityTableView;
		
		this.generateHeadersTable();
	}
	
	public void clearData() {
		this.reliabilityData.clear();
	}
	
	public void fillReliabilityDate(String resultFilePath){
		
		Map<String,ReliabilityEntry> reliabilityEntries = new HashMap<>();
		
		try{
			
			BufferedReader br = new BufferedReader(new FileReader(resultFilePath));
			String line;

			while ((line = br.readLine()) != null) {
				String[] str=line.split(",");
				if(str.length >= 3){					
					String service = str[1];
					boolean result = Boolean.parseBoolean(str[2]);					
					
					if(!reliabilityEntries.containsKey(service)) {
						reliabilityEntries.put(service, new ReliabilityEntry(service));
					}
					
					ReliabilityEntry reliabilityEntry = reliabilityEntries.get(service);
					reliabilityEntry.setInvocationNum(reliabilityEntry.getInvocationNum() +1);
					
					if(!result)
						reliabilityEntry.setFailNum(reliabilityEntry.getFailNum() +1);				
				}
			}
			br.close();	
			
			for (ReliabilityEntry entry : reliabilityEntries.values()) {
				entry.setRate();
				if(!entry.getService().equals("AssistanceService"))
					reliabilityData.add(entry);
			}
			reliabilityData.add(reliabilityEntries.get("AssistanceService"));
		}
		catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	private void generateHeadersTable(){
		
		TableColumn<ReliabilityEntry, String> serviceColumn = generateColumnReliabilityEntryService();
		
		TableColumn<ReliabilityEntry, Integer> invocationColumn = 
				generateColumnIntegerReliabilityEntry(TablesUtil.INVOCATIONS, TablesUtil.PROPERTY_INVOCATIONS);
		TableColumn<ReliabilityEntry, Integer> failColumn = 
				generateColumnIntegerReliabilityEntry(TablesUtil.FAIL, TablesUtil.PROPERTY_FAIL);
		TableColumn<ReliabilityEntry, Double> failRateColumn = 
				generateColumnDoubleReliabilityEntry(TablesUtil.FAIL_RATE, TablesUtil.PROPERTY_FAIL_RATE);
		TableColumn<ReliabilityEntry, Double> successRateColumn = 
				generateColumnDoubleReliabilityEntry(TablesUtil.SUCCESS_RATE, TablesUtil.PROPERTY_SUCCESS_RATE);
		
		List<TableColumn<ReliabilityEntry, ? extends Object>> columns = 
				Arrays.asList(serviceColumn, invocationColumn, failColumn, failRateColumn, successRateColumn);
		
		reliabilityTableView.setItems(reliabilityData);
		addColumnsInTableReliabilityEntry(reliabilityTableView, columns);
	}
	
	
	
	private TableColumn<ReliabilityEntry, String> generateColumnReliabilityEntryService() {
		TableColumn<ReliabilityEntry, String> column = new TableColumn<ReliabilityEntry, String>(TablesUtil.SERVICE);
		column.setCellValueFactory(new PropertyValueFactory<ReliabilityEntry, String>(TablesUtil.PROPERTY_SERVICE));
		column.prefWidthProperty().bind(reliabilityTableView.widthProperty().divide(TablesUtil.DIVIDE_SMALL).multiply(TablesUtil.MULTIPLY_SMALL));
		return column;
	}
	
	private TableColumn<ReliabilityEntry, Integer> generateColumnIntegerReliabilityEntry(String columnName, String columnProperty) {
		TableColumn<ReliabilityEntry, Integer> column = new TableColumn<ReliabilityEntry, Integer>(columnName);
		column.setCellValueFactory(new PropertyValueFactory<ReliabilityEntry, Integer>(columnProperty));
		column.prefWidthProperty().bind(reliabilityTableView.widthProperty().divide(TablesUtil.DIVIDE_SMALL));
		return column;
	}
	
	private TableColumn<ReliabilityEntry, Double> generateColumnDoubleReliabilityEntry(String columnName, String columnProperty) {
		TableColumn<ReliabilityEntry, Double> column = new TableColumn<ReliabilityEntry, Double>(columnName);
		column.setCellValueFactory(new PropertyValueFactory<ReliabilityEntry, Double>(columnProperty));
		column.prefWidthProperty().bind(reliabilityTableView.widthProperty().divide(TablesUtil.DIVIDE_SMALL));
		return column;
	}
	
	private void addColumnsInTableReliabilityEntry(TableView<ReliabilityEntry> table, 
									List<TableColumn<ReliabilityEntry, ?>> columns) {
		for (TableColumn<ReliabilityEntry, ?> tableColumn : columns) {
			table.getColumns().add(tableColumn);
		}
	}
}
