package application.view.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import application.model.CostEntry;
import application.utility.strings.TablesUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class CostTableService {

    private TableView<CostEntry> costTable;
	
	private ObservableList<CostEntry> costData 
				= FXCollections.observableArrayList();
	
	public CostTableService(TableView<CostEntry> costTableView) {
		this.costTable = costTableView;
		
		this.createHeaderTable();
	}
	
	private void createHeaderTable() {
		TableColumn<CostEntry, String> columnService = 
				new TableColumn<CostEntry, String>(TablesUtil.SERVICE);
		columnService.setCellValueFactory(
				new PropertyValueFactory<CostEntry,String>(TablesUtil.PROPERTY_SERVICE));
		columnService.prefWidthProperty()
				.bind(costTable.widthProperty().divide(TablesUtil.DIVIDE_MEDIUM));
		
		TableColumn<CostEntry, Integer> columnInvocation = 
				generateColumn(TablesUtil.INVOCATIONS, TablesUtil.PROPERTY_INVOCATIONS);
		
		TableColumn<CostEntry, Integer> columnCost = 
				generateColumn(TablesUtil.TOTAL_COST, TablesUtil.PROPERTY_TOTAL_COST);
		
		costTable.setItems(costData);
		costTable.getColumns().add(columnService);
		costTable.getColumns().add(columnInvocation);
		costTable.getColumns().add(columnCost);
	}
	
	public void clearData() {
		this.costData.clear();
	}
	
	public void addCostData(String resultFilePath){
		
		Map<String,CostEntry> costEntries = new HashMap<>();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(resultFilePath));
			String line;
			
			String service;
			boolean result;

	        while ((line = br.readLine()) != null) {
				String[] str=line.split(",");
				if(str.length >= 3){
					service=str[1];
					result=Boolean.parseBoolean(str[2]);
					
					if(result && !service.equals("AssistanceService")){
						if(!costEntries.containsKey(service)){
							costEntries.put(service, new CostEntry(service));
						}
						
						CostEntry entry=costEntries.get(service);
						entry.setInvocationNum(entry.getInvocationNum() +1);
						entry.setTotalCost(entry.getTotalCost()+Double.parseDouble(str[3]));
					}
				}
			}
			br.close();
	
			int totalInvocations=0;
			double totalCost=0;
			
			for (CostEntry entry : costEntries.values()) {
				totalInvocations=totalInvocations+entry.getInvocationNum();
				totalCost=totalCost+entry.getTotalCost();
				costData.add(entry);
			}

			costData.add(new CostEntry("Total", totalInvocations, totalCost));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private TableColumn<CostEntry, Integer> generateColumn(String name, String property) {
		
		TableColumn<CostEntry, Integer> column = 
				new TableColumn<CostEntry, Integer>(name);
		column.setCellValueFactory(
				new PropertyValueFactory<CostEntry, Integer>(property));
		column.prefWidthProperty()
				.bind(costTable.widthProperty().divide(TablesUtil.DIVIDE_MEDIUM));
		
		return column;
	}
}
