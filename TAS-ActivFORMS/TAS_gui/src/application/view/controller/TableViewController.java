package application.view.controller;
import application.model.CostEntry;
import application.model.PerformanceEntry;
import application.model.ReliabilityEntry;
import application.view.service.CostTableService;
import application.view.service.PerformanceTableService;
import application.view.service.ReliabilityTableService;
import javafx.scene.control.TableView;

public class TableViewController {
	
	private CostTableService costTableService;
	private PerformanceTableService performanceTableService;
	private ReliabilityTableService reliabilityTableService;
		
	public TableViewController(TableView<ReliabilityEntry> reliabilityTableView, TableView<CostEntry> costTableView,
			TableView<PerformanceEntry> performanceTableView) {
		
		this.reliabilityTableService = new ReliabilityTableService(reliabilityTableView);
		this.costTableService = new CostTableService(costTableView);
		this.performanceTableService = new PerformanceTableService(performanceTableView);
	}
	
	public void fillReliabilityDate(String resultFilePath){
		this.reliabilityTableService.fillReliabilityDate(resultFilePath);
	}
	
	public void fillCostData(String resultFilePath){
		this.costTableService.addCostData(resultFilePath);
	}
	
	public void fillPerformanceData(String resultFilePath) {
		this.performanceTableService.fillPerformanceData(resultFilePath);
	}
	
	public void clear(){
	    reliabilityTableService.clearData();
	    costTableService.clearData();
	    performanceTableService.clearData();
	}
}
